package scrum.project.actors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.MoveNextToActiveSprint;
import scrum.project.story.IStory;

public class BacklogActor extends AbstractActor {
    
    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);
    
    private Map<String, IStory> stories = new LinkedHashMap<>();
    private static int index = 0;
    
    public static Props props(){
	return Props.create(BacklogActor.class, BacklogActor::new);
    }
    
    public BacklogActor(){
	receive(
		ReceiveBuilder
		.match(Map.class, message->{
		    LOGGER.info("{} stories are sent to the backlog", message.size());
		    stories.putAll(message);
		    index = stories.size() -1;
		})
		.match(MoveNextToActiveSprint.class, 
			message->{
			    String identifier = (String) stories.keySet().toArray()[index];
			    LOGGER.info("the story with identifier {} will be moved to the  active sprint", identifier);
			    sender().tell(stories.get(identifier), self());
			    index--;
			}).build()
		);
    }

}
