package scrum.project.actors;

import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;
import scrum.project.actors.team.DeveloperActor;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.story.ActiveBacklogActor;
import scrum.project.story.IStory;
import scrum.project.story.StoryCategory;

public class DeveloperActorTest {

	ActorSystem system = ActorSystem.create();
	
	/**
	 * When developer is done with his task, he has to start on a new Story.
	 * Before that he has to take a story from the activebacklog. Active backlog
	 * should send a new story once it is demanded
	 */
//	@Test
//	public void developerCanTakeStoryFromActiveSprintLog() throws Exception{
////		TestActorRef<DeveloperActor> developer = TestActorRef.create(system, DeveloperActor.props());
//		TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());
//		
//		final Future newStory = Patterns.ask(activeBacklog, new GiveMeDevelopmentStory(), 1000);
//		final CompletionStage<IStory> feedbackFromActiveBacklog = toJava(newStory); 
//		final CompletableFuture<IStory> newStoryReceived = (CompletableFuture<IStory>) feedbackFromActiveBacklog;
//		
//		IStory story = newStoryReceived.get(1000, TimeUnit.MILLISECONDS);
//		assert(story.getCategory()).equals(StoryCategory.DEVELOPMENT);
//	}
}
