package scrum.project.actors;

import java.util.Map;
import java.util.Random;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scrum.project.messages.review.InReviewStories;
import scrum.project.messages.review.Review;
import scrum.project.messages.review.ReviewStatus;
import scrum.project.story.IStory;
import scrum.project.story.StoryStatus;

/**
 * ProductOwner stores the stories that he should review. He stores them in a
 * {@link Map}. In respond to {@link InReviewStories}, he sends the {@link Map}
 * to the consumer.
 * 
 * ProductOwner receives the stories to be reviewed. Them it stores them into a data structure.
 * Then it reviews the story. Currently reviewed story is also stored a state.
 * 
 * 
 * 
 * 
 *     InReviewStories       ProductOwner  storiesInReview 
 * ----------------------->              --------------------->
 * 
 * @author tr1b4361
 *
 */
public class ProductOwnerActor extends AbstractActor {

    protected final LoggingAdapter LOGGER = Logging.getLogger(context().system(), this);

    private ProductOwnerAgenda agenda = new ProductOwnerAgenda();
    
    private Review beingReviewed;

    public static Props props() {
	return Props.create(ProductOwnerActor.class, ProductOwnerActor::new);
    }

    public ProductOwnerActor() {
	receive(ReceiveBuilder.match(InReviewStories.class, message -> {
	    LOGGER.info("Stories to be reviewed by the PO are asked");
	    sender().tell(agenda.getReviewList(), self());
	}).match(Review.class, message -> {
	    
	    Review review = (Review) message;
	    LOGGER.info("The story {} is sent for review", review.getStory().getIdentifier());
	    
	    
	    IStory story = review.getStory();
	    agenda.addStoryToInReviewList(review);
	    reviewStory(story);

	}).matchAny(message -> {
	    LOGGER.error("Message type {} is not known", message.getClass());
	    sender().tell(new Status.Failure(new Exception()), self());
	}).build());
    }

    private void reviewStory(IStory story) {
	beingReviewed = agenda.getReview(story.getIdentifier()); 
	story.setStatus(StoryStatus.INREVIEW);
	determineReviewStatus();
	
	
	if(beingReviewed.getStatus().equals(ReviewStatus.ACCEPTED)){
	    story.setStatus(StoryStatus.REVIEWED);
	    
	} else {
	    story.setStatus(StoryStatus.IN_PROGRESS);
	}
	
	agenda.removeStoryFromInReviewList(beingReviewed);
	beingReviewed = null;
	
    }

    private int changeStatus() {
	ReviewStatus[] statuses = ReviewStatus.values();
	
	Random random = new Random();
	return random.nextInt(statuses.length);
    }
    
    private void determineReviewStatus(){
	int currentStatus = beingReviewed.getStatus().ordinal();
	int futureStatus = 0;
	while(currentStatus == futureStatus){
	    futureStatus = changeStatus();
	} 
	
	ReviewStatus[] statuses = ReviewStatus.values();
	ReviewStatus reviewStatus = statuses[futureStatus];
	beingReviewed.setStatus(reviewStatus);
    }

}
