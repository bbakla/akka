package scrum.project.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.actor.Terminated;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.actors.BacklogActor;
import scrum.project.actors.ProductOwnerActor;
import scrum.project.actors.ScrumMasterActor;
import scrum.project.story.DevelopmentStory;

public class ScrumParent extends AbstractActor {

    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);

    private ActorRef backlogActor;
    private ActorRef poActor;
    private ActorRef smActor;

    public static Props props() {
	return Props.create(ScrumParent.class, ScrumParent::new);
    }

    public ScrumParent() {
	receive(ReceiveBuilder.match(Terminated.class, message -> LOGGER.info("Terminated")).matchAny(message -> {
	    LOGGER.error("Message type {} is not known", message.getClass());
	    sender().tell(new Status.Failure(new Exception()), self());
	}).build());
	
	initializeActors();
	prepareBacklog();
    }

    private void initializeActors() {
	backlogActor = context().actorOf(BacklogActor.props(), "backlog");
	context().watch(backlogActor);

	poActor = context().actorOf(ProductOwnerActor.props(), "product-owner");
	context().watch(poActor);

	smActor = context().actorOf(ScrumMasterActor.props(), "scrum-master");
	context().watch(smActor);
    }

    private void prepareBacklog() {
	String fileName = "DevelopmentStoryList.txt";
	addStoriesToBacklog(fileName);;
	
	fileName = "TestStoryList.txt";
	addStoriesToBacklog(fileName);

    }

    private void addStoriesToBacklog(String fileName) {
	ClassLoader loader = this.getClass().getClassLoader();

	File configFile = new File(loader.getResource(fileName).getFile());

	try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		String[] story = line.split(",");
		backlogActor.tell(new DevelopmentStory(story[0], story[1], story[2]), self().noSender());
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
