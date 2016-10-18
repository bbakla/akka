package scrum.project.story;

public abstract class Story implements IStory{
	
	private final String category;
	private final String identity;
	
	public Story(String category, String identity) {
		this.category = category;
		this.identity = identity;
	}

	@Override
	public String getCategory() {
		return category;
	}
	
	@Override
	public String getIdentifier() {
		return identity;
	}
	
	

}
