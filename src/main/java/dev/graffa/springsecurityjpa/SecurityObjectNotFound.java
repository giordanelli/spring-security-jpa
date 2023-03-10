package dev.graffa.springsecurityjpa;

public abstract class SecurityObjectNotFound extends RuntimeException{
    public SecurityObjectNotFound(String s) {
        super(s);
    }
}
