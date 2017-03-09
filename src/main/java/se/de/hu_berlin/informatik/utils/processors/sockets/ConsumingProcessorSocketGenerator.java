package se.de.hu_berlin.informatik.utils.processors.sockets;

import se.de.hu_berlin.informatik.utils.processors.sockets.eh.EHWithInput;
import se.de.hu_berlin.informatik.utils.processors.sockets.module.Module;
import se.de.hu_berlin.informatik.utils.processors.sockets.pipe.Pipe;
import se.de.hu_berlin.informatik.utils.threaded.disruptor.eventhandler.AbstractDisruptorEventHandler;

/**
 * An interface that provides the functionality of being capable to 
 * generate different implementations of {@link ConsumingProcessorSocket}, namely
 * {@link Module}, {@link Pipe} and {@link AbstractDisruptorEventHandler}
 * (in the form of {@link EHWithInput}).
 * 
 * @author Simon
 *
 * @param <A>
 * is the type of the input objects
 */
public interface ConsumingProcessorSocketGenerator<A> extends ProcessorSocketGenerator<A, Object> {

}
