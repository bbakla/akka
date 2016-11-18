package scrum.project.actors.team;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.CurrentlyInProgressStory;
import scrum.project.story.IStory;

/**
 * 1- Developer can say on which task she is working
 * 2- Developer accepts a Story and starts working on it if there is no other assigned story to her.
 *   If there is, it stores the story to work later
 * she can ask for a new one.
 *  
 * @author tr1b4361
 *
 */
public class DeveloperActor extends AbstractActor {

    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);

    private Map<String, Object> archived = new HashMap<>();
    private Map<String, Object> assignedStories = new HashMap<String, Object>();
    private IStory inProgressStory;
    
    private ActorRef po;
    private ActorRef backlog;
    
    public static Props props(ActorRef productOwner, ActorRef backlog) {

	return Props.create(DeveloperActor.class, ()-> new DeveloperActor(productOwner, backlog));
    }

    public DeveloperActor(ActorRef po, ActorRef backlogActor) {
	this.po = po; 
	this.backlog = backlogActor;

	receive(ReceiveBuilder.match(CurrentlyInProgressStory.class, message -> {
	    LOGGER.info("Currently worked story, {}, is asked from {}", inProgressStory.getIdentifier(),
		    sender().toString());
	    sender().tell(inProgressStory, self());
	}).match(IStory.class, message -> {
	    LOGGER.info("A new story is sent from {}", sender().path().toString());
	    classifyStory(message);
	}).matchAny(o -> {
	    LOGGER.info("received unknown message: {}", o);
	    sender().tell(new Status.Failure(new ClassNotFoundException()), self());
	}).build());
    }

    private void classifyStory(IStory story) {

	if (!hasAInProgressStory()) {
	    inProgressStory = story;
	    startWorking();
	} else {
	    assignedStories.put(story.getIdentifier(), story);
	}
    }

    private void startWorking() {
	// TODO Auto-generated method stub
	
    }

    private void üstartWorking() {
	// TODO Auto-generated method stub
	
    }

    private boolean hasAInProgressStory() {
	return inProgressStory == null ? false : true;
    }
}
