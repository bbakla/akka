package scrum.project.messages.review;

import scrum.project.story.IStory;

public class Review {
    
    private IStory story;

    public Review(IStory story) {
	super();
	this.story = story;
    }
    
    public IStory getStory(){
	return story;
    }
    
    

}
