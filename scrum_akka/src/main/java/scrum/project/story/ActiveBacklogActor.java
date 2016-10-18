package scrum.project.story;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.AddStory;
import scrum.project.messages.GiveMeDevelopmentStory;

/**
 * When {@link GiveMeDevelopmentStory} messages received, a development story is sent
 * When a {@link IStory} is received, it is put into the activeBacklog
 * When a {@link Map} is received, all content is put into the active backlog
 * 
 * 
 * 
 * @author tr1b4361
 *
 */
public class ActiveBacklogActor extends AbstractActor {

    private Map<String, IStory> activeBacklog = new HashMap<>();
    
    
    public static Props props(){
		return Props.create(ActiveBacklogActor.class, ActiveBacklogActor::new);
	}
	
	public ActiveBacklogActor(){
	    receive(ReceiveBuilder
		    .match(GiveMeDevelopmentStory.class, message -> {
			
		IStory story = getDevelopmentStory();
		sender().tell(story, self());
	}).match(IStory.class, story-> activeBacklog.put(story.getIdentifier(), story))
          .match(Map.class, map -> insertBacklog(map))
	  .build());
	}

	private DevelopmentStory getDevelopmentStory() {

	    return (DevelopmentStory) activeBacklog.get("1");
	}
	
	private void insertBacklog(Map<String, IStory> backlog){
	    activeBacklog.putAll(backlog);
	}

}
