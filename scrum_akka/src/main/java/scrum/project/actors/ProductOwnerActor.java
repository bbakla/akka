package scrum.project.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.review.InReviewStories;

public class ProductOwnerActor extends AbstractActor {
    
    public static Props props(){
	return Props.create(ProductOwnerActor.class, ProductOwnerActor::new);
    }
    
    public ProductOwnerActor(){
	receive(
		ReceiveBuilder.match(InReviewStories.class, message ->
		{
		    
		}).build()
		);
    }

}
