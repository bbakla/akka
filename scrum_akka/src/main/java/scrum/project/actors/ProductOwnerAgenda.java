package scrum.project.actors;

import java.util.HashMap;
import java.util.Map;

import scrum.project.messages.review.Review;
import scrum.project.story.IStory;

public class ProductOwnerAgenda {
    
    private Map<String, Review> inReview = new HashMap<String, Review>();
    private Map<String, IStory> reviewedStories = new HashMap<String, IStory>();
    
    
    public void addStoryToInReviewList(Review review){
	inReview.put(review.getStory().getIdentifier(), review);
    }
    
    public void removeStoryFromInReviewList(Review review){
	inReview.remove(review);
	reviewedStories.put(review.getStory().getIdentifier(), review.getStory());
    }
    
    public Map<String, Review> getReviewList(){
	return inReview;
    }

    public Review getReview(String identifier) {
	return inReview.get(identifier);
    }
    
    
    

}
