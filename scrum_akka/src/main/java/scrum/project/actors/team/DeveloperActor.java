package scrum.project.actors.team;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.CurrentlyInProgressStory;
import scrum.project.story.IStory;
import scrum.project.story.Story;

public class DeveloperActor extends AbstractActor {

    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);

    private Map<String, Object> archived = new HashMap<>();
    private Map<String, Object> assignedStories = new HashMap<String, Object>();
    private IStory inProgressStory;

    public static Props props() {

	return Props.create(DeveloperActor.class, DeveloperActor::new);
    }

    public DeveloperActor() {

	receive(ReceiveBuilder.match(CurrentlyInProgressStory.class, message -> {
	    LOGGER.info("Currently worked story, {}, is asked from {}", inProgressStory.getIdentifier(),
		    sender().toString());
	    sender().tell(inProgressStory, self());
	}).match(IStory.class, message -> {
	    LOGGER.info("A new story is sent from {}", sender().toString());
	    classifyStory(message);
	}).matchAny(o -> {
	    LOGGER.info("received unknown message: {}", o);
	    sender().tell(new Status.Failure(new ClassNotFoundException()), self());
	}).build());
    }

    private void classifyStory(IStory story) {

	if (!hasAInProgressStory()) {
	    inProgressStory = story;
	} else {
	    assignedStories.put(story.getIdentifier(), story);
	}
    }

    private boolean hasAInProgressStory() {
	return inProgressStory == null ? false : true;
    }
}
