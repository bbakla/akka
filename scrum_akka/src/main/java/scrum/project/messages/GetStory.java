package scrum.project.messages;

import akka.actor.ActorRef;
import akka.japi.Function;

public final class GetStory{

    private final String identifier;
    
    public GetStory(String identifier) {

	this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
