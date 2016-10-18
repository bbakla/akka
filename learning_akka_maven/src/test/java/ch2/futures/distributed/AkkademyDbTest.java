package ch2.futures.distributed;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import akka.testkit.TestActorRef;
import ch1.SetRequest;
import scala.concurrent.Future;
import static scala.compat.java8.FutureConverters.toJava;

public class AkkademyDbTest {
	ActorSystem system = ActorSystem.create();
	protected final LoggingAdapter logger = Logging.getLogger(system, this);
	
	@Test
	public void shouldReturnKeyValueToSetRequestWithAskPatternCS() throws Exception{
//		TestActorRef<AkkademyDb> akkademyTestRef  =TestActorRef.create(system, AkkademyDb.props());
//		
		SetRequest request = new SetRequest("key1", "ich lasse mich verwöhnen");
		
//		akkademyTestRef.tell(request, ActorRef.noSender());
//		AkkademyDb akkademy = akkademyTestRef.underlyingActor(); 
		
		ActorRef akkademyActor = system.actorOf(AkkademyDb.props());
//		akkademyActor.tell(request, ActorRef.noSender());
		
		CompletionStage<Object> completionStage = PatternsCS.ask(akkademyActor, request, 1000);
		CompletableFuture<Object> completable = (CompletableFuture<Object>) completionStage;
		completionStage.thenAccept(message ->{
			logger.info("message is stored with key " + message);
			System.out.println("Message is stored with key " + message);
		});
		
		assert(completable.get(500, TimeUnit.MILLISECONDS)).equals("key1");

	} 
	
	//Set request mesajina aktorun nasil dondugunu yazacagim. future nesnesi kullanacagim.

	@Test
	public void shouldReturnKeyValueToSetRequestWithAskPattern() throws Exception{
		SetRequest request = new SetRequest("key1", "ich lasse mich verwöhnen");
		
		ActorRef akkademyActor = system.actorOf(AkkademyDb.props());
		
		Future<Object> completionStage = Patterns.ask(akkademyActor, request, 1000);
		
		CompletableFuture<Object> completable = (CompletableFuture<Object>) completionStage;
		assert(completable.get(500, TimeUnit.MILLISECONDS)).equals("key1");

	} 

}
