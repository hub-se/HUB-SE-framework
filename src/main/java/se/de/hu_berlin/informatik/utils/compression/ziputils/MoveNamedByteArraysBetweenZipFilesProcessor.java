package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Pair;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class MoveNamedByteArraysBetweenZipFilesProcessor extends AbstractProcessor<Pair<String, String>, Boolean> {

	private Path zipFilePathSource;
	private Path zipFilePathTarget;

	public MoveNamedByteArraysBetweenZipFilesProcessor(Path zipFilePathSource, Path zipFilePathTarget) {
		//if this module needs an input item
		super();
		
		if (!zipFilePathSource.toFile().exists()) {
			Log.abort(this, "File '%s' does not exist.", zipFilePathSource);
		}
		if (!zipFilePathTarget.toFile().exists()) {
			Log.abort(this, "File '%s' does not exist.", zipFilePathTarget);
		}
		
		this.zipFilePathSource = zipFilePathSource;
		this.zipFilePathTarget = zipFilePathTarget;
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public Boolean processItem(Pair<String, String> sourceAndTargetFileNames) {
		ZipFileWrapper zipFileSource = new ZipFileWrapper(zipFilePathSource);
		ZipFileWrapper zipFileTarget = new ZipFileWrapper(zipFilePathTarget);

		try {
			if (!zipFileSource.exists(sourceAndTargetFileNames.first())) {
				return false;
			}
		} catch (ZipException e1) {
			Log.abort(this, e1, "File '%s' does not exist.", sourceAndTargetFileNames.first());
		}
		try {
			InputStream sourceStream = null;
			PipedOutputStream out = null;
			Thread thread = null;
			try {
				PipedInputStream in = new PipedInputStream();
				out = new PipedOutputStream(in);

				thread = startZipFileListener(zipFileTarget, sourceAndTargetFileNames.second(), in);
				thread.start();

				try (ZipFile zipFile = new ZipFile(zipFileSource.getzipFilePath().toFile())) {
					ZipEntry entry = zipFile.getEntry(sourceAndTargetFileNames.first());

					sourceStream = zipFile.getInputStream(entry);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = sourceStream.read(buffer)) != -1) {
						out.write(buffer, 0, bytesRead);
					}

				} catch (IOException e) {
					throw new ZipException("Reading input stream from file '" + zipFileSource.getzipFilePath() + "' failed!");
				}
			} catch (ZipException e) {
				Log.abort(this, e, "File '%s' does not exist or failed to create stream.", sourceAndTargetFileNames.first());
			} finally {
				if (sourceStream != null) {
					sourceStream.close();
				}
				if (out != null) {
					out.flush();
					out.close();
				}
				if (thread != null) {
					while (thread.isAlive()) {
						try {
							thread.join();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		} catch (IOException e) {
			Log.abort(this, e, "Could not copy files.");
		}
		return true;
	}

	private Thread startZipFileListener(ZipFileWrapper zipFileTarget, String fileName, PipedInputStream in) {
		return new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					// Creates a new entry in the zip file and adds the content to the zip file
					zipFileTarget.addStream(in, fileName);
				} catch (IOException e) {
					Log.abort(this, e, "Zip file '%s' does not exist or could not add stream.", zipFileTarget.getzipFilePath());
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {

						}
					}
				}
			}
		});
	}
}
