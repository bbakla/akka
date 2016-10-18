package ch2.futures.distributed;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import ch1.SetRequest;
import ch2.futures.distributed.messages.GetRequest;
import ch2.futures.distributed.messages.KeyNotFoundException;

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
                            log.info("Received Set request: ({}, {})", message.getKey(), message.getValue());
                            map.put(message.getKey(), message.getValue());
                            sender().tell(new Status.Success(message.getKey()), self());
                        }).
                        match(GetRequest.class, message ->{
                        	log.info("Recieverd Get request: ({}, {})", message.getKey());
                        	String value = (String) map.get(message.getKey());
                        	Object response = value != null ?
                        			value : new Status.Failure
                        			(new KeyNotFoundException(message.getKey() + "not found"));
                        	sender().tell(response, sender());
                        }).
                        matchAny(o -> {
                        	log.info("received unknown message: {}", o);
                        	sender().tell(new Status.Failure(new ClassNotFoundException()), self());
                        }).build()
        );
    }
}
