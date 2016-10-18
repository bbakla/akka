package ch1;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class Hw_MessagerActor extends AbstractActor{
	
	protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
	
	protected String lastMessage;
	
	
	public Hw_MessagerActor(){
		receive(ReceiveBuilder.match(String.class, message -> lastMessage = message)
				              .matchAny(o-> log.info("message is unknown"))
				              .build()
				);
	
	}


	public static Props props() {
		return Props.create(Hw_MessagerActor.class);
	}
	
	
}
