package ch2;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class PingActor extends AbstractActor{
	protected final LoggingAdapter logger = Logging.getLogger(context().system(), this);

	public PingActor() {
		
		receive(ReceiveBuilder
				.matchEquals("Ping", message->{
					logger.info("Ping message is received");
					sender().tell("Pong", self());
		})		.matchAny(message -> logger.info("{} is unknown message in Ping", message) ).build()
				);
	}

	public static Props props() {
		return Props.create(PingActor.class, PingActor::new);
	}
	
	

}
