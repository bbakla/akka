package scrum.project.messages;

import scrum.project.story.IStory;

public final class AddStory {
    
    private final String identifier;
    private final IStory story;
    
    public AddStory(String identifier, IStory story) {
	super();
	this.identifier = identifier;
	this.story = story;
    }

    public String getIdentifier() {
        return identifier;
    }

    public IStory getStory() {
        return story;
    }
    
    

}
