package dev.graffa.springsecurityjpa.user;

import dev.graffa.springsecurityjpa.SecurityObjectNotFound;

public class UserNotFoundException extends SecurityObjectNotFound {

    public UserNotFoundException(String id) {
        super("Could not find User " + id);
    }
}
