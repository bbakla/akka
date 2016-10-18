package ch2;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class PongActor extends AbstractActor{
	protected final LoggingAdapter logger = Logging.getLogger(context().system(), this);
	
	public PongActor() {
		receive(ReceiveBuilder.matchEquals("Pong", message ->{
			logger.info("pong message is received");
			sender().tell("Ping", self());
		}).matchAny(o->logger.error("{} is unknown message in Pong", o)).build()
				);
	}

	public static Props props() {
		return Props.create(PongActor.class, PongActor::new);
	}

}
