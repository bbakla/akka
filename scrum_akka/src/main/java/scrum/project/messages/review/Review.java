package scrum.project.messages.review;

import scrum.project.story.IStory;

public class Review {
    
    private IStory story;
    private ReviewStatus status;

    public Review(IStory story) {
	super();
	this.story = story;
	this.status = ReviewStatus.REJECTED;
    }
    
    public IStory getStory(){
	return story;
    }
    
    public ReviewStatus getStatus(){
	return status;
    }
    
    public void setStatus(ReviewStatus status){
	this.status = status;
    }
}
