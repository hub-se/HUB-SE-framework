package se.de.hu_berlin.informatik.utils.tm.pipeframework;

public abstract class PipeFactory<A,B> {
	
	APipe<A,B> pipe = null;

	/**
	 * Tries to get or create a pipe.
	 * @return
	 * a pipe, if possible
	 * @throws IllegalStateException
	 * if not possible
	 */
	public APipe<A,B> getPipe() throws IllegalStateException {
		if (pipe == null) {
			pipe = newPipe();
		}
		return pipe;
	}
	
	/**
	 * Tries to create a pipe.
	 * @return
	 * a pipe, if possible
	 * @throws IllegalStateException
	 * if not possible
	 */
	abstract public APipe<A,B> newPipe() throws IllegalStateException;
	
}
