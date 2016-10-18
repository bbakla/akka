package ch1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestActorRef;

public class AkkademyDBTest {
	
	ActorSystem system = ActorSystem.create();
	
	@Test
	public void itShouldPlaceKeyValueFromSetMessageIntoMap(){
		TestActorRef<AkkademyDb> actorRef = TestActorRef.create(system, AkkademyDb.props());
		actorRef.tell(new SetRequest("key", "value"), ActorRef.noSender());

		AkkademyDb akkademyDb = actorRef.underlyingActor();
		assertEquals(akkademyDb.map.get("key"), "value");
	}


}
