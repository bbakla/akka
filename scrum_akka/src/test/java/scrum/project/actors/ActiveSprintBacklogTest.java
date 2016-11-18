package scrum.project.actors;

import static scala.compat.java8.FutureConverters.toJava;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.messages.GiveMeTestStory;
import scrum.project.messages.MoveNextToActiveSprint;
import scrum.project.actors.BacklogActor;
import scrum.project.actors.ScrumMasterActor;
import scrum.project.messages.GetActiveBacklog;
import scrum.project.messages.GetSpecificStory;
import scrum.project.story.DevelopmentStory;
import scrum.project.story.IStory;
import scrum.project.story.Story;
import scrum.project.story.StoryCategory;
import scrum.project.story.TestStory;

public class ActiveSprintBacklogTest {

    ActorSystem system = ActorSystem.create();

    /**
     * When developer is done with his task, he has to start on a new Story.
     * Before that he has to take a story from the activebacklog. Active backlog
     * should send a new story once it is demanded
     */
    @Test
    public void developerCanTakeStoryFromActiveSprintLog() throws Exception {
	// TestActorRef<DeveloperActor> developer = TestActorRef.create(system,
	// DeveloperActor.props());
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());

	Map<String, Object> backlog = new HashMap<>();
	backlog.put("1", new DevelopmentStory("Story1", "1", "Wir hören voneinanders"));

	activeBacklog.tell(backlog, TestActorRef.noSender());

	final Future newStory = Patterns.ask(activeBacklog, new GiveMeDevelopmentStory(), 1000);
	final CompletionStage<IStory> feedbackFromActiveBacklog = toJava(newStory);
	final CompletableFuture<IStory> newStoryReceived = (CompletableFuture<IStory>) feedbackFromActiveBacklog;

	IStory story = newStoryReceived.get(1000, TimeUnit.MILLISECONDS);
	assert (story.getCategory()).equals(StoryCategory.DEVELOPMENT);
	assert (story.getIdentifier().equals("1"));
    }

    @Test
    public void testerCanTakeAStoryFromActiveSprint() throws Exception {
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());
	

	Map<String, Object> backlog = new HashMap<>();
	backlog.put("11", new TestStory("bedauern", "11", "Das Bedauern"));
	activeBacklog.tell(backlog, TestActorRef.noSender());
	
	final CompletableFuture<Object> getTestStoryCf = (CompletableFuture<Object>) PatternsCS.ask(activeBacklog, new GiveMeTestStory(), 1000);
	
	TestStory movedStory = (TestStory) getTestStoryCf.get(1000, TimeUnit.MILLISECONDS);	

	assert(movedStory.getIdentifier()).equals("11");

    }

    @Test
    public void activeSprintBacklogCanGiveAllStoriesIfWanted() throws Exception {
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());

	Story story = new DevelopmentStory("herausfordern", "1", "Menchen möchten die Regime eigentlich herausfordern.");
	activeBacklog.tell(story, TestActorRef.noSender());

	final CompletableFuture<Object> getBacklogListFuture = (CompletableFuture<Object>) PatternsCS.ask(activeBacklog,
		new GetActiveBacklog(), 1000);

	Map<String, Object> backlogList = (Map<String, Object>) getBacklogListFuture.get(1000, TimeUnit.MILLISECONDS);
	assertTrue(backlogList.containsKey(story.getIdentifier()));
    }

    @Test
    public void activeSprintBacklogCanSendASpecificStory() throws Exception{
	TestActorRef<ActiveBacklogActor> activeBacklogActor = TestActorRef.create(system, ActiveBacklogActor.props());
	
	Story story = new DevelopmentStory("Story1", "q","Die Werbungen hat viele Tricke");
	Story story2 = new DevelopmentStory("1q", "1q", "Ich habe den Kaffe auf meine Hemd geschüttet");
	
	activeBacklogActor.tell(story, TestActorRef.noSender());
	activeBacklogActor.tell(story2, TestActorRef.noSender());
	
	CompletableFuture<Object> completable = (CompletableFuture<Object>) PatternsCS.ask(activeBacklogActor, new GetSpecificStory(story.getIdentifier()), 1000);
	Story storyBack = (Story)completable.get(1000, TimeUnit.MILLISECONDS);
	
	assert(story.getIdentifier()).equals(storyBack.getIdentifier());
	
	completable = (CompletableFuture<Object>) PatternsCS.ask(activeBacklogActor, new GetSpecificStory(story2.getIdentifier()), 1000);
	Story storyBackAgain = (Story) completable.get(1000, TimeUnit.MILLISECONDS);
	
	assert(story2.getIdentifier()).equals(storyBackAgain.getIdentifier());
    }

    @Test
    public void storyCanBeMovedFromSprintBacklogToTheActiveSprintBacklogBySM() throws Exception{
	TestActorRef<ActiveBacklogActor> activeBacklogActor = TestActorRef.create(system, ActiveBacklogActor.props());
	TestActorRef<ActiveBacklogActor> smActor = TestActorRef.create(system, ScrumMasterActor.props());
	TestActorRef<ActiveBacklogActor> backlogActor = TestActorRef.create(system, BacklogActor.props());
	
	Story story = new DevelopmentStory("abraten", "q", "ich habe ihm abgeraten, seine Team zu andern");
	Story story2 = new DevelopmentStory("bewerben", "11", "ich have mich bei Siemens um als CEO bewerbt.");
	Story story3 = new DevelopmentStory("Menschenkenntnis","22", "In vielen Berufen muss Man gute Menschenkentniss hat");
	Story story4 = new DevelopmentStory("Der Flick", "33", "Es gibt ein grosser Flick auf deinen Pullover");
	Story story5 = new TestStory("sich beklerern", "35", "Er hat sich mit Kaffee beklekert");
	Story story6 = new TestStory("tatig sein", "36", "Ich bin als Engineer tatig");
	
	Map<String, IStory> stories = new HashMap<>();
	stories.put(story.getIdentifier(), story);
	stories.put(story2.getIdentifier(), story2);
	stories.put(story3.getIdentifier(), story3);
	stories.put(story4.getIdentifier(), story4);
	stories.put(story5.getIdentifier(), story5);
	stories.put(story6.getIdentifier(), story6);
	
	//put all stories into backlog
	backlogActor.tell(stories, TestActorRef.noSender());
	
	//get the next story from backlog
	CompletableFuture<Object> storycf = (CompletableFuture<Object>) PatternsCS.ask(backlogActor, new MoveNextToActiveSprint(), 500);
	Story activeStory = (Story) storycf.get(500, TimeUnit.MILLISECONDS);
	
	assertNotNull(activeStory);
	
	//put the story to the active sprint
	activeBacklogActor.tell(activeStory, smActor);
	
	//check if it is there
	CompletableFuture<Object> checkIfThere = (CompletableFuture<Object>) 
		PatternsCS.ask(activeBacklogActor, new GetSpecificStory(activeStory.getIdentifier()), 500);
	
	Story fromActiveBacklog = (Story) checkIfThere.get();
	
	
	assert(activeStory.getCategory()).equals(fromActiveBacklog.getCategory());
	assert(activeStory.getIdentifier()).equals(fromActiveBacklog.getIdentifier());
	
    }
}
