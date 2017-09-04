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

public class SimpleServerFramework {
	
	final private static byte NULL_DATA = 2;
	final private static byte NORMAL_DATA = 3;

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
			ServerSocket socket, Object receiveLock) {
		ServerSideListener<T, R> serverSideListener = new ServerSideListener<>(socket, receiveLock);
		serverSideListener.run();
		return serverSideListener;
	}

	public static class ServerSideListener<T extends Serializable, R extends Serializable> {

		final private ServerSocket serverSocket;
		private final Object receiveLock;

		private Thread runningThread = null;
		private T data;
		private volatile boolean isShutdown = false;
		private volatile boolean hasNewData = false;

		public ServerSideListener(ServerSocket serverSocket, Object receiveLock) {
			this.serverSocket = serverSocket;
			this.receiveLock = receiveLock;
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
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
//					 Log.out(this, "Server Socket Extablished...");
					// Create input and output streams to client
					ObjectOutputStream outToClient = new ObjectOutputStream(clientSocket.getOutputStream());
					ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());

//					Log.out("server", "reading data from port %d...", clientSocket.getLocalPort());
					/* Retrieve data */
					byte status = inFromClient.readByte();
					if (status == NULL_DATA) {
						this.data = null;
					} else {
						this.data = (T) inFromClient.readObject();
					}
					
//					Log.out("server", "writing data to port %d...", clientSocket.getLocalPort());
					if (data == null) {
						/* we received null data */
						outToClient.writeByte(NULL_DATA);
					} else {
						/* we received data other than null (TODO: checksum or something...?) */
						outToClient.writeByte(NORMAL_DATA);
					}
					outToClient.flush();
					
//					Log.out("server", "written data to port %d...", clientSocket.getLocalPort());
					// tell any waiting threads that there is new data...
					synchronized (receiveLock) {
						hasNewData = true;
						receiveLock.notifyAll();
					}
					
				} catch (Exception e) {
					if (clientSocket != null) {
						try {
							clientSocket.close();
						} catch (IOException e1) {
							// do nothing...
						}
					}
					// if any exception occurred, the client should now try to send the data again
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
					received = sendToServer(null, serverSocket.getLocalPort(), 1);
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

		public boolean hasNewData() {
			return hasNewData;
		}

		public T getLastData() {
			hasNewData = false;
			return data;
		}

		public void resetListener() {
			hasNewData = false;
		}

	}

	public static <T extends Serializable, R extends Serializable> boolean sendToServer(
			T data, int port, int maxTryCount) {
		int count = 0;
		while (count < maxTryCount) {
			++count;
			// Create the socket
			try (Socket clientSocket = new Socket((String) null, port)) {
				// Log.out("client", "Client Socket initialized...");
				// Create the input & output streams to the server
				ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());

//				Log.out("client", "writing data to port %d...", port);
				/* Send the Message Object to the server */
				if (data == null) {
					outToServer.writeByte(NULL_DATA);
				} else {
					outToServer.writeByte(NORMAL_DATA);
					outToServer.writeObject(data);
				}
				outToServer.flush();
				
//				Log.out("client", "reading data to port %d...", port);
				/* Retrieve the status byte from server */
				byte status = inFromServer.readByte();
				
				if ((data == null && status == NULL_DATA) ||
						(data != null && status == NORMAL_DATA)) {
//					Log.out("client", "read data to port %d...", port);
					return true;
				} else {
					Log.err("client", "Error sending correct data: try %d", count);
				}

			} catch (Exception e) {
				Log.err("client", e, "Try %d, error: %s", count, e.getMessage());
			}
		}

		return false;
	}
}
