package scrum.project.actors.team;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class TesterActor extends AbstractActor{

    public static Props props(){
	return Props.create(TesterActor.class, TesterActor::new);
    }
}
