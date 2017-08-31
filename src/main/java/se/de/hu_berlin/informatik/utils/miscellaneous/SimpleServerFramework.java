package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class SimpleServerFramework {

	public static int getFreePort() {
		return getFreePort(new Random().nextInt(60536) + 5000);
	}

	public static int getFreePort(final int startPort) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(null);
		} catch (UnknownHostException e1) {
			// should not happen
			return -1;
		}
		// port between 0 and 65535 !
		Random random = new Random();
		int currentPort = startPort;
		int count = 0;
		while (true) {
			if (count > 1000) {
				return -1;
			}
			++count;
			try {
				new Socket(inetAddress, currentPort).close();
			} catch (final IOException e) {
				// found a free port
				break;
			} catch (IllegalArgumentException e) {
				// should only happen on first try (if argument wrong)
			}
			currentPort = random.nextInt(60536) + 5000;
		}
		return currentPort;
	}

	public static ServerSocket startServer() {
		return _startServer(getFreePort());
	}

	public static ServerSocket startServer(int port) {
		return _startServer(getFreePort(port));
	}

	private static ServerSocket _startServer(int port) {
		try {
			ServerSocket socket = new ServerSocket(port);
			// set a timeout to stop blocking in case of errors occurring...
			// socket.setSoTimeout(10000);
			return socket;
		} catch (Exception e) {
			Log.err("server", e, "error: " + e.getMessage());
		}

		return null;
	}

	public static <T extends Serializable, R extends Serializable> ServerSideListener<T, R> startServerListener(
			ServerSocket socket, Object receiveLock, Function<T, R> sendOnReceivedData, Supplier<R> sendOnFailure) {
		ServerSideListener<T, R> serverSideListener = new ServerSideListener<>(socket, receiveLock, sendOnReceivedData,
				sendOnFailure);
		serverSideListener.run();
		return serverSideListener;
	}

	public static class ServerSideListener<T extends Serializable, R extends Serializable> {

		final private ServerSocket serverSocket;
		private final Object receiveLock;
		private final Function<T, R> sendOnReceivedData;
		private final Supplier<R> sendOnFailure;

		private Thread runningThread = null;
		private T data;
		private boolean isShutdown = false;
		private boolean hasNewData = false;
		private boolean serverErrorOccurred = false;

		public ServerSideListener(ServerSocket serverSocket, Object receiveLock, Function<T, R> sendOnReceivedData,
				Supplier<R> sendOnFailure) {
			this.serverSocket = serverSocket;
			this.receiveLock = receiveLock;
			this.sendOnReceivedData = sendOnReceivedData;
			this.sendOnFailure = sendOnFailure;
		}

		public void run() {
			if (this.runningThread == null || !this.runningThread.isAlive()) {
				this.runningThread = new Thread(() -> {
					listenOnSocket(serverSocket);
				});
				this.runningThread.start();
			}
		}

		@SuppressWarnings("unchecked")
		private void listenOnSocket(ServerSocket serverSocket) {
			while (!isShutdown) {
				// Create the Client Socket
				try (Socket clientSocket = serverSocket.accept()) {
//					 Log.out(this, "Server Socket Extablished...");
					// Create input and output streams to client
					ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
					ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());

					boolean read = false;
					try {
						/* Retrieve information */
						this.data = (T) inFromClient.readObject();
						read = true;
					} catch (IOException e) {
						Log.err("server", e, "error: " + e.getMessage());
					}

					// Log.out(this, "read: %b", read);
					int tries = 0;
					while (!read && tries < 10) {
						++tries;
						// Log.out(this, "try: %d", tries);
						try {
							/* Tell the client to send the data again... */
							outToClient.writeObject(sendOnFailure.get());
						} catch (IOException e) {
							Log.err("server", e, "error: " + e.getMessage());
							continue;
						}

						try {
							/* Retrieve information */
							this.data = (T) inFromClient.readObject();
							read = true;
						} catch (IOException e) {
							Log.err("server", e, "error: " + e.getMessage());
						}
					}

					if (!read) {
						// tell any waiting threads that there is an error...
						synchronized (receiveLock) {
							serverErrorOccurred = true;
							receiveLock.notifyAll();
						}
						// cut the connection...
						clientSocket.close();
					} else {
						// tell any waiting threads that there is new data...
						synchronized (receiveLock) {
							hasNewData = true;
							receiveLock.notifyAll();
						}

						boolean sent = false;
						tries = 0;
						while (!sent && tries < 10) {
							// Log.out(this, "sent: %b", sent);
							++tries;
							try {
								/* Send a message object back */
								outToClient.writeObject(sendOnReceivedData.apply(this.data));
								sent = true;
							} catch (IOException e) {
								Log.err("server", e, "error: " + e.getMessage());
							}
						}

						if (!sent) {
							// cut the connection...
							clientSocket.close();
						}
					}
				} catch (Exception e) {
					// tell any waiting threads that there is an error...
					synchronized (receiveLock) {
						serverErrorOccurred = true;
						receiveLock.notifyAll();
					}
					Log.err("server", e, "error: " + e.getMessage());
				}
			}
		}

		public void shutDown() {
			if (this.runningThread != null && this.runningThread.isAlive()) {
				isShutdown = true;
				boolean received = false;
				int count = 0;
				while (!received && count < 5) {
					++count;
					received = sendToServer(null, serverSocket.getLocalPort(), 1, (r) -> {
						return false;
					}, (t, r) -> {
						return true;
					});
				}
				if (received) {
					while (this.runningThread.isAlive()) {
						try {
							this.runningThread.join();
						} catch (InterruptedException e) {
							// wait until finished
						}
					}
				} else {
					// just don't wait?...
				}
				try {
					serverSocket.close();
				} catch (IOException e) {
					// don't care
				}
			}
		}

		public boolean serverErrorOccurred() {
			return serverErrorOccurred;
		}

		public boolean hasNewData() {
			return hasNewData;
		}

		public T getLastData() {
			hasNewData = false;
			return data;
		}

		public void resetListener() {
			serverErrorOccurred = false;
			hasNewData = false;
		}

	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable, R extends Serializable> boolean sendToServer(T data, int port,
			int maxTryCount, Predicate<R> sendAgain, BiPredicate<T, R> isSuccessful) {
		boolean succeeded = false;
		int count = 0;
		while (!succeeded && count < maxTryCount) {
			++count;
			// Create the socket
			try (Socket clientSocket = new Socket((String) null, port)) {
				// Log.out("client", "Client Socket initialized...");
				// Create the input & output streams to the server
				ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());

				boolean sent = false;
				int tries = 0;
				while (!sent && tries < 10) {
					++tries;
					try {
						// Log.out("client", "writing data to port %d...",
						// port);
						/* Send the Message Object to the server */
						outToServer.writeObject(data);
						sent = true;
					} catch (IOException e) {
						Log.err("client", e, "error: " + e.getMessage());
					}
				}

				// Log.out("client", "sent: %b", sent);
				// only wait for messages if actually sent something...
				if (sent) {
					R inFromServerMsg = null;
					boolean read = false;
					tries = 0;
					while (!read && tries < 10) {
						++tries;
						try {
							/* Retrieve the Message Object from server */
							inFromServerMsg = (R) inFromServer.readObject();
							read = true;
						} catch (IOException e) {
							Log.err("client", e, "error: " + e.getMessage());
						}
					}
					// Log.out("client", "read: %b", read);

					if (read) {
						/* Print out the received Message */
						// Log.out("client", "Message from server: " +
						// inFromServerMsg);
						// check if the server wants us to check the data again
						// (could be the case that there was an exception while
						// reading the input stream)
						if (sendAgain.test(inFromServerMsg)) {
							// try again
							continue;
						}
						// check if the message from the server declares the
						// transmission as successful
						succeeded = isSuccessful.test(data, inFromServerMsg);
					}
				}

			} catch (Exception e) {
				Log.err("client", e, "error: " + e.getMessage());
			}
		}

		return succeeded;
	}
}
