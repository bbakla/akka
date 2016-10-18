package ch2.futures;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.pattern.PatternsCS;
import ch2.promise.PongActorPromise;
import scala.concurrent.Future;

import static akka.pattern.Patterns.ask;
import static scala.compat.java8.FutureConverters.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PongActorTest {
	
	ActorSystem system = ActorSystem.create();
	ActorRef pongActorRef = system.actorOf(PongActorPromise.props());

	/**
	 * when PongActor sends a "Ping" message, it should receive a "Pong" message back
	 * 
	 * What we have explained here
	 * 1- ask method 
	 * @throws Exception
	 */
	@Test
	public void shouldReplyToPingWithPong() throws Exception{
		/**
		 * ask method explanation
		 * 1st argument: actorRef to sent the message to
		 * 2nd argument: message we want to send to the actor
		 * 3th argument: the timeout for the future- how long to wait for a result before considering it a failure
		 * */
		Future sFuture = ask(pongActorRef, "Ping", 1000); 
		final CompletionStage<String> completion = toJava(sFuture); //this gives us the placeholder of the response we will get in the future
		final CompletableFuture<String> jFuture = (CompletableFuture<String>) completion; //scala future is converted to Java type
		
		assertEquals("Pong", jFuture.get(100, TimeUnit.MILLISECONDS));//get() call blocks the thread in the test and get the result.
	}
	
	@Test(expected = ExecutionException.class)
	public void shouldReplyToUnknownMessageWithFailure() throws Exception{
		Future sFuture = ask(pongActorRef, "unknown", 1000);
		final CompletionStage<String> completionStage = toJava(sFuture);
		final CompletableFuture<String> jFuture = (CompletableFuture<String>) completionStage;
		jFuture.get(100, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void shouldPringToConsole() throws Exception{
		 askPong("Ping").thenAccept(x -> System.out.println("replied with: " + x));
	        Thread.sleep(100);
	}
	
	 @Test
	    public void shouldTransform() throws Exception {
	        char result = (char) get(askPong("Ping").thenApply(x -> x.charAt(0)));
	        assertEquals('P', result);
	    }
	
	 private Object get(CompletionStage cs) throws Exception{
		   return ((CompletableFuture<String>) cs).get(1000, TimeUnit.MILLISECONDS);
	 }
	 
	 private CompletionStage<String> askPong(String message){
		Future sFuture = ask(pongActorRef, message, 1000);
		final CompletionStage<String> stage = toJava(sFuture);
		
		return stage;
	}
}
