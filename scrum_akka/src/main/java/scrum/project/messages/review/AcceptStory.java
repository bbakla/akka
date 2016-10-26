package scrum.project.messages.review;

import scrum.project.story.IStory;

public class AcceptStory  implements ReviewResult{

    private IStory story;
    
    public AcceptStory(IStory story) {
	this.story = story;
    }
    
    @Override
    public IStory getStory() {
	return story;
    }

}
