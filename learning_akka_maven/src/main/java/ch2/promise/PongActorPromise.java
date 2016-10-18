package ch2.promise;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class PongActorPromise extends AbstractActor{
	protected final LoggingAdapter logger = Logging.getLogger(context().system(), this);
	
	public static Props props(){
		return Props.create(PongActorPromise.class);
	}
	
	public PongActorPromise() {
		receive(ReceiveBuilder
				.matchEquals("Ping", message->{
					logger.info("{} is received in PongActorPromise", message);
					sender().tell("Pong", self());
		})
				.matchAny(o->{ 
					logger.info("unknown message");
					sender().tell(new Status.Failure(new Exception("unknown message")), self());
		})
				
				.build()
				
				);
	}

}
