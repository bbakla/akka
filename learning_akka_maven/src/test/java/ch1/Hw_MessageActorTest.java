package ch1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;

public class Hw_MessageActorTest {

	ActorSystem system = ActorSystem.create();

	@Test
	public void actorReceivesTheSentString() {
		TestActorRef<Hw_MessagerActor> testActorRef = TestActorRef.create(system, Hw_MessagerActor.props());

		testActorRef.tell("First_String", ActorRef.noSender());
		Hw_MessagerActor actor = testActorRef.underlyingActor();

		assertNotNull(actor.lastMessage);
		assertEquals("First_String", actor.lastMessage);
	}

	@Test
	public void actorOnlyStoresTheLastSentMessage() {
		TestActorRef<Hw_MessagerActor> testActorRef = TestActorRef.create(system, Hw_MessagerActor.props());

		testActorRef.tell("First_String", ActorRef.noSender());
		testActorRef.tell("Last_String", ActorRef.noSender());
		Hw_MessagerActor actor = testActorRef.underlyingActor();

		assertNotNull(actor.lastMessage);
		assertEquals("Last_String", actor.lastMessage);
		
		
	}

}
