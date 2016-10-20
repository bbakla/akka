package scrum.project.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class Backlog extends AbstractActor {
    
    public static Props props(){
	return Props.create(Backlog.class, Backlog::new);
    }

}
