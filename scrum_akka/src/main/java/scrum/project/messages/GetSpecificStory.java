package scrum.project.messages;

import akka.actor.ActorRef;
import akka.japi.Function;

public final class GetSpecificStory{

    private final String identifier;
    
    public GetSpecificStory(String identifier) {

	this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
