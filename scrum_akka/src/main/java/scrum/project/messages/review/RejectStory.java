package scrum.project.messages.review;

import scrum.project.story.IStory;

public class RejectStory implements ReviewResult{
    
   private IStory story;
    
    public RejectStory(IStory story) {
	this.story = story;
    }
    
    @Override
    public IStory getStory() {
	return story;
    }

}
