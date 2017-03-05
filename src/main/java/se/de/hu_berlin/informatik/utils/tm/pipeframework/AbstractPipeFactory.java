package se.de.hu_berlin.informatik.utils.tm.pipeframework;

@Deprecated
public abstract class AbstractPipeFactory<A,B> {
	
	AbstractPipe<A,B> pipe = null;

	/**
	 * Tries to get or create a pipe.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	public AbstractPipe<A,B> getPipe() throws UnsupportedOperationException {
		if (pipe == null) {
			pipe = newPipe();
		}
		return pipe;
	}
	
	/**
	 * Tries to create a pipe.
	 * @return
	 * a pipe, if possible
	 * @throws UnsupportedOperationException
	 * if not possible
	 */
	abstract public AbstractPipe<A,B> newPipe() throws UnsupportedOperationException;
	
}
