package scrum.project.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class ScrumMasterActor extends AbstractActor{
    
    public static Props props(){
	return Props.create(ScrumMasterActor.class, ScrumMasterActor::new);
    }

}
