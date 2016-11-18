package scrum.project.launcher;


import akka.actor.ActorSystem;

public class ScrumLauncher {

    public static void main(String[] args) {

	ActorSystem system = ActorSystem.create("Scrum");
	system.actorOf(ScrumParent.props(), "scrum_parent");
	 
    }

}
