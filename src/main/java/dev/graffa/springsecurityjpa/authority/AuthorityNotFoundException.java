package dev.graffa.springsecurityjpa.authority;

import dev.graffa.springsecurityjpa.SecurityObjectNotFound;

public class AuthorityNotFoundException extends SecurityObjectNotFound {

    public AuthorityNotFoundException(String id) {
        super("Could not find Authority " + id);
    }

}
