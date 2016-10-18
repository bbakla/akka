package ch2;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;

public class PingPongActorTest {
	
	ActorSystem system = ActorSystem.create();
	
	@Test
	public void pingActorShouldSendPongToPing(){
		TestActorRef<PingActor> pingActorRef = TestActorRef.create(system, PingActor.props());
		TestActorRef<PongActor> pongActorRef = TestActorRef.create(system, PongActor.props());

		//Ping message will be sent to PingActor. Then It will send Pong message to the 
		//PongActor. PongMessage will receive the "Pong" message and send "Ping" message
		//to the PingActor. It will go on like that
		pingActorRef.tell("Ping", pongActorRef);
	}

}
