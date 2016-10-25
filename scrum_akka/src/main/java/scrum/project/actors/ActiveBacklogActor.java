package scrum.project.actors;

import java.util.HashMap;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.GetActiveBacklog;
import scrum.project.messages.GetSpecificStory;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.messages.GiveMeTestStory;
import scrum.project.story.DevelopmentStory;
import scrum.project.story.IStory;
import scrum.project.story.Story;
import scrum.project.story.TestStory;

/**
 * When {@link GiveMeDevelopmentStory} messages received, a development story is
 * sent When a {@link IStory} is received, it is put into the activeBacklog When
 * a {@link Map} is received, all content is put into the active backlog
 * 
 * Send story to the developer if {@link GiveMeDevelopmentStory} is received
 * Developer GiveMeDevelopmentStory ActiveBacklog Story Developer
 * -----------------------------> ------------------->
 * 
 * Send story to the developer if {@link GiveMeTestStory} is received Tester
 * GiveMeTestStory ActiveBacklog Story Tester ----------------------------->
 * ------------------->
 * 
 * If {@link Story} is received, save it to the backlog. Developer, SM, or PM
 * Story ActiveBacklog ----------------> If {@link GetSpecificStory} is
 * received, send the asked story. Developer, SM, or PM GetSpecificStory
 * ActiveBacklog Story ----------------------> ------------------>
 * 
 * @author tr1b4361
 *
 */
public class ActiveBacklogActor extends AbstractActor {

    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);

    private Map<String, IStory> activeBacklog = new HashMap<>();

    public static Props props() {
	return Props.create(ActiveBacklogActor.class, ActiveBacklogActor::new);
    }

    public ActiveBacklogActor() {
	receive(ReceiveBuilder
		.match(GiveMeDevelopmentStory.class, message -> {
		    IStory story = getDevelopmentStory();
		    sender().tell(story, self());
	})
		.match(IStory.class, story -> activeBacklog.put(story.getIdentifier(), story))

		.match(Map.class, map -> insertBacklog(map))

		.match(GetActiveBacklog.class, message -> {
		    LOGGER.info("Active backlog is demanded");
		    sender().tell(activeBacklog, self());
	})

		.match(GiveMeTestStory.class, message -> {
		    LOGGER.info("Tester asks for a new story");
		    IStory story = getATestStory();
		    sender().tell(story, self());
		})

		.match(GetSpecificStory.class, messages -> {
		    LOGGER.info("Story with identifier {} is asked", messages.getIdentifier());

		    IStory story = activeBacklog.get(messages.getIdentifier());
		    sender().tell(story, self());
		})

		.matchAny(message -> {
		    LOGGER.error("Message type {} is not known", message.getClass());
		    sender().tell(new Status.Failure(new Exception()), self());
		})

		.build());
    }

    private IStory getATestStory() {
	// TODO Auto-generated method stub
	return (TestStory) activeBacklog.get("11");
    }

    private DevelopmentStory getDevelopmentStory() {

	return (DevelopmentStory) activeBacklog.get("1");
    }

    private void insertBacklog(Map<String, IStory> backlog) {
	activeBacklog.putAll(backlog);
    }

}
