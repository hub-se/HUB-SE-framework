package se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler;

public abstract class EHWithInputAndReturnMethodProvider<A,B> extends EHWithInputAndReturnFactory<A,B> {

	/**
	 * @param eventHandlerClass
	 * the class type of returned instances 
	 */
	public EHWithInputAndReturnMethodProvider(Class<? extends EHWithInputAndReturn<A,B>> eventHandlerClass) {
		super(eventHandlerClass);
	}
	
	public EHWithInputAndReturnMethodProvider() {
		super();
	}

	@Override
	public EHWithInputAndReturn<A,B> newFreshInstance() {
		return new EHWithInputAndReturn<A,B>() {
			@Override
			public B processInput(A input) {
				return EHWithInputAndReturnMethodProvider.this.processInput(input, this);
			}

			@Override
			public void resetAndInit() {
				//do nothing (no variables specific to this handler)
			}
		};
	}
	
	public abstract B processInput(A input, EHWithInputAndReturn<A,B> executingHandler);
	
}
