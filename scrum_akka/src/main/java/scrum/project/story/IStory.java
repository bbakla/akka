package scrum.project.story;

public interface IStory {
	
	String getCategory();
	String getIdentifier();
	
	void setStatus(StoryStatus status);
	StoryStatus getStatus();
	
	String getDescription();
	void setDescription(String description);
	
	String getName();
	void setName(String name);

}
