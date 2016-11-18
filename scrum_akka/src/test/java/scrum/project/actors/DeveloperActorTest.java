package scrum.project.actors;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import scrum.project.actors.team.DeveloperActor;
import scrum.project.messages.CurrentlyInProgressStory;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.messages.review.InReviewStories;
import scrum.project.messages.review.Review;
import scrum.project.story.DevelopmentStory;
import scrum.project.story.IStory;
import scrum.project.story.Story;

public class DeveloperActorTest {

    ActorSystem system = ActorSystem.create();

    protected final LoggingAdapter LOGGER = Logging.getLogger(system, this);

    /**
     * When developer is done with his task, he has to start on a new Story.
     * Before that he has to take a story from the activebacklog. Active backlog
     * should send a new story once it is demanded
     * 
     * Developer Review PO Accept/Reject Developer
     * Developer Question      PO      Answer      Developer
     *       ----------------->   -----------------> 

     * Developer       Done        Tester  
     *          ----------------->       ----------------->
     * Developer joinMeeting       DailyMeeting
     *         ------------------->        
     *  Developer   GiveMeDevelopmentStory   ActiveBacklog 
     *           -------------------------->
     * Developer   AllAssignedStories
     * 	        ----------------------------->
     * 
     */

    /**
     * tests the communication between ActiveBacklog and Developer. 
     * Developer GiveMeDevelopmentStory     ActiveBacklog 
     *         ------------------------------>
     * @throws Exception
     */
    @Test
    public void developerCanTakeStoryFromActiveSprintLog() throws Exception {
	TestActorRef<ProductOwnerActor> po = TestActorRef.create(system, ProductOwnerActor.props());
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());
	TestActorRef<DeveloperActor> developer = TestActorRef.create(system, DeveloperActor.props(po, activeBacklog));

	IStory story = new DevelopmentStory("Gerecht sein", "1", "AKP ist nicht immer gerecht");
	activeBacklog.tell(story, TestActorRef.noSender());

	// activebackloga soyle ki; ona developer aktorden
	// GiveMeDevelopmentStory geliyor
	activeBacklog.tell(new GiveMeDevelopmentStory(), developer);
	Thread.sleep(20);
	CompletableFuture<Object> currentStory = (CompletableFuture<Object>) PatternsCS.ask(developer,
		new CurrentlyInProgressStory(), 500);
	Story currentStoryInProgress = (Story) currentStory.get();

	assert (story.getIdentifier()).equals(currentStoryInProgress.getIdentifier());
    }

    /**
     * tests the communication between PO and developer 
     *  Developer   Review         PO    Accept/Reject   Developer
     *         ------------------>     ----------------->
     * @throws Exception
     */
    @Test
    public void developerCanSendAReviewRequestToThePO() throws Exception {
	TestActorRef<ProductOwnerActor> po = TestActorRef.create(system, ProductOwnerActor.props());
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());
	TestActorRef<DeveloperActor> developer = TestActorRef.create(system, DeveloperActor.props(po, activeBacklog));

	Review review = new Review(new DevelopmentStory("Der Staatsanwald", "1", "Mein Bruder war ein guter Staatsanwald"));
	po.tell(review, developer);
	CompletableFuture<Object> storiesInReview = (CompletableFuture<Object>) PatternsCS.ask(po,
		new InReviewStories(), 1000);
	Map<String, IStory> result = (Map<String, IStory>) storiesInReview.get();

	assertEquals(1, result.size());
    }
    
    @Test
    public void developerCanTellOnWhichStoryHeIsWorking() throws Exception{
	fail();
    }
    
    
}
