package scrum.project.actors;

import static scala.compat.java8.FutureConverters.toJava;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.messages.GiveMeTestStory;
import scrum.project.actors.Backlog;
import scrum.project.actors.ScrumMasterActor;
import scrum.project.actors.team.TesterActor;
import scrum.project.messages.GetActiveBacklog;
import scrum.project.messages.GetStory;
import scrum.project.story.ActiveBacklogActor;
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
	backlog.put("1", new DevelopmentStory("1"));

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
	TestActorRef<TesterActor> tester = TestActorRef.create(system, TesterActor.props());
	

	Map<String, Object> backlog = new HashMap<>();
	backlog.put("11", new TestStory("11"));
	activeBacklog.tell(backlog, TestActorRef.noSender());
	
	final CompletableFuture<Object> getTestStoryCf = (CompletableFuture<Object>) PatternsCS.ask(activeBacklog, new GiveMeTestStory(), 1000);
	
	TestStory movedStory = (TestStory) getTestStoryCf.get(1000, TimeUnit.MILLISECONDS);	

	assert(movedStory.getIdentifier()).equals("11");

    }

    @Test
    public void activeSprintBacklogCanGiveAllStoriesIfWanted() throws Exception {
	TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());

	Story story = new DevelopmentStory("1");
	activeBacklog.tell(story, TestActorRef.noSender());

	final CompletableFuture<Object> getBacklogListFuture = (CompletableFuture<Object>) PatternsCS.ask(activeBacklog,
		new GetActiveBacklog(), 1000);

	Map<String, Object> backlogList = (Map<String, Object>) getBacklogListFuture.get(1000, TimeUnit.MILLISECONDS);
	assertTrue(backlogList.containsKey(story.getIdentifier()));
    }

    @Test
    public void activeSprintBacklogCanSendASpecificStory() throws Exception{
	TestActorRef<ActiveBacklogActor> activeBacklogActor = TestActorRef.create(system, ActiveBacklogActor.props());
	
	Story story = new DevelopmentStory("q");
	Story story2 = new DevelopmentStory("1q");
	
	activeBacklogActor.tell(story, TestActorRef.noSender());
	activeBacklogActor.tell(story2, TestActorRef.noSender());
	
	CompletableFuture<Object> completable = (CompletableFuture<Object>) PatternsCS.ask(activeBacklogActor, new GetStory(story.getIdentifier()), 1000);
	Story storyBack = (Story)completable.get(1000, TimeUnit.MILLISECONDS);
	
	assert(story.getIdentifier()).equals(storyBack.getIdentifier());
	
	completable = (CompletableFuture<Object>) PatternsCS.ask(activeBacklogActor, new GetStory(story2.getIdentifier()), 1000);
	Story storyBackAgain = (Story) completable.get(1000, TimeUnit.MILLISECONDS);
	
	assert(story2.getIdentifier()).equals(storyBackAgain.getIdentifier());
    }

    @Test
    public void storyCanBeMovedFromSprintBacklogToTheActiveSprintBacklogBySM() {
	TestActorRef<ActiveBacklogActor> activeBacklogActor = TestActorRef.create(system, ActiveBacklogActor.props());
	TestActorRef<ActiveBacklogActor> smActor = TestActorRef.create(system, ScrumMasterActor.props());
	TestActorRef<ActiveBacklogActor> backlogActor = TestActorRef.create(system, Backlog.props());
	
	Story story = new DevelopmentStory("q");
	Story story2 = new DevelopmentStory("11");
	Story story3 = new DevelopmentStory("22");
	Story story4 = new DevelopmentStory("33");
	
	Map<String, IStory> stories = new HashMap<>();
	stories.put(story.getIdentifier(), story);
	stories.put(story2.getIdentifier(), story2);
	stories.put(story3.getIdentifier(), story3);
	stories.put(story4.getIdentifier(), story4);
	
	backlogActor.tell(stories, TestActorRef.noSender());
	
    }
}
