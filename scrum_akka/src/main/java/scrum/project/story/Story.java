package scrum.project.story;

public abstract class Story implements IStory {

    private final String category;
    private final String identity;
    private String description;
    private String name;
    private StoryStatus status;


    public Story(String category, String name, String identity, String description) {
	this.category = category;
	this.name = name;
	this.identity = identity;
	this.description = description;
    }

    @Override
    public String getCategory() {
	return category;
    }

    @Override
    public String getIdentifier() {
	return identity;
    }

    @Override
    public void setStatus(StoryStatus status) {
	this.status = status;
    }

    @Override
    public StoryStatus getStatus() {
	return status;
    }
    
    @Override
    public String getDescription(){
	return description;
    }
    
    @Override
    public void setDescription(String description){
	this.description = description;
    }
    
    public String getName(){
	return name;
    }
    
    public void setName(String name){
	this.name = name;
    }
}
