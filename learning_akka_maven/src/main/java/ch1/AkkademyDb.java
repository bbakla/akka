package ch1;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {
    protected final LoggingAdapter log = Logging.getLogger(context().system(), this);
    protected final Map<String, Object> map = new HashMap<String, Object>();

    public static Props props(){
    	return Props.create(AkkademyDb.class);
    }
    
    private AkkademyDb(){
        receive(ReceiveBuilder.
                        match(SetRequest.class, message -> {
                            log.info("Received Set request: {}", message);
                            map.put(message.getKey(), message.getValue());
                        }).
                        matchAny(o -> log.info("received unknown message: {}", o)).build()
        );
    }
}
