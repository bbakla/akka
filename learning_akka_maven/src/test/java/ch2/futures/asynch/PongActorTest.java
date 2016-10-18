package ch2.futures.asynch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import ch2.PongActor;

import scala.concurrent.Future;
import static scala.compat.java8.FutureConverters.toJava;

/**
 * This class uses Futures to test the actor
 *  
 * shouldReplyToPingWithPong and shouldReplyToPingWithPong2 methods actually test the same thing
 * with different coding approach
 * @author barisbakla
 *
 */
public class PongActorTest {

	ActorSystem system = ActorSystem.create();
	ActorRef actorRef = system.actorOf(PongActor.props());

	@Test
	public void shouldReplyToPingWithPong() throws Exception{
		//ask the actor to respond to the message. If it doesnt return any reply in 1 seconds,
		//throw Timeout error. If everything works fine, to code below will return a 
		// placeholder of the response we will get in the future. (Placeholder is a Scala future instance) 
		final Future sFuture = Patterns.ask(actorRef, "Pong", 1000);  
		
		//The scala instance should be converted to Java object.CompletionStage is the interface 
		//for CompletableFuture. so We will cast CompletionStage to the concrete class.
		final CompletionStage<String> cs = toJava(sFuture);
		final CompletableFuture<String> jFuture = (CompletableFuture<String>) cs;
		
		//We call the get method to block the thread in the tests and get the result.
		assert(jFuture.get(1000, TimeUnit.MILLISECONDS)).equals("Ping");
	}
	
	@Test
	public void shouldReplyToPingWithPong2() throws Exception{
		final CompletionStage<Object> cs= PatternsCS.ask(actorRef, "Pong", 1000);
		final CompletableFuture<Object> jFuture = (CompletableFuture<Object>) cs;
		assert(jFuture.get(1000, TimeUnit.MILLISECONDS)).equals("Ping");
	}
	
	@Test(expected = ExecutionException.class)
	public void unknownMessagesShouldThrownException() throws Exception{
		final CompletionStage<Object> cs = PatternsCS.ask(actorRef, "error", 1000);
		final CompletableFuture<Object> jFuture = (CompletableFuture<Object>) cs;
		jFuture.get(1000, TimeUnit.MICROSECONDS);
	}
	
	@Test
	public void printToConsole() throws Exception{
		
		System.out.println("printing");
		
		//askPong().thenAccept(x-> System.out.println("replied with " + x));
		//askPong().thenApply( o -> ((String)o).charAt(0));
		
		CompletableFuture<Object> cf = askPong("Pong");
		cf.thenCompose(x->askPong("Pong"));
		
		assert(cf.get(500, TimeUnit.MILLISECONDS)).equals("Ping");
	}
	
	@Test
	public void handleFailure(){
		
		System.out.println("handling failure");
		
		askPong("create problem").handle((x, t) -> {if(t != null)
			{
				System.out.println("Error "  + x + " " + t);
			}
		return null;
		});
		
		askPong("createProblem").exceptionally(t -> { return "default";});
	}
	
	private CompletableFuture<Object> askPong(String message){
		final CompletionStage<Object> cs = PatternsCS.ask(actorRef, message, 1000);
		return (CompletableFuture<Object>) cs;
		
	}
}
