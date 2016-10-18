package scrum.project.actors.team;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class DeveloperActor extends AbstractActor {

	public static Props props() {

		return Props.create(DeveloperActor.class, DeveloperActor::new);
	}

}
