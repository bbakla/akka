package scrum.project.actors;

import static scala.compat.java8.FutureConverters.toJava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import scala.concurrent.Future;
import scrum.project.actors.team.DeveloperActor;
import scrum.project.messages.CurrentlyInProgressStory;
import scrum.project.messages.GiveMeDevelopmentStory;
import scrum.project.story.DevelopmentStory;
import scrum.project.story.IStory;
import scrum.project.story.Story;
import scrum.project.story.StoryCategory;

public class DeveloperActorTest {

    ActorSystem system = ActorSystem.create();

    /**
     * When developer is done with his task, he has to start on a new Story.
     * Before that he has to take a story from the activebacklog. Active backlog
     * should send a new story once it is demanded
     * 
     * Developer     Review           PO    Accept/Reject   Developer
     *          -------------------->     ----------------->
     * Developer     Question         PO    Answer   Developer
     *          -------------------->     ----------------->
     * 
     * Developer    Done          Tester    
     *           ---------------->
     *          
     * Developer      joinMeeting     DailyMeeting
     *           ------------------->   
     * Developer      GiveMeDevelopmentStory     ActiveBacklog
     *           ------------------------------>   
     * 
     * Developer    AllAssignedStories          
     *           ----------------------------->
     *     
     */
     @Test
     public void developerCanTakeStoryFromActiveSprintLog() throws Exception{
     TestActorRef<DeveloperActor> developer = TestActorRef.create(system, DeveloperActor.props());
     TestActorRef<ActiveBacklogActor> activeBacklog = TestActorRef.create(system, ActiveBacklogActor.props());
     
     IStory story = new DevelopmentStory("1");
     activeBacklog.tell(story, TestActorRef.noSender());
     
     //activebackloga soyle ki; ona developer aktorden GiveMeDevelopmentStory geliyor
     activeBacklog.tell(new GiveMeDevelopmentStory(), developer);
     Thread.sleep(20);
     CompletableFuture<Object> currentStory = (CompletableFuture<Object>) PatternsCS.ask(developer, new CurrentlyInProgressStory(), 500);
     Story currentStoryInProgress = (Story) currentStory.get();
     
     assert(story.getIdentifier()).equals(currentStoryInProgress.getIdentifier());
     }
}
