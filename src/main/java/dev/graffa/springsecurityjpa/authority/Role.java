package dev.graffa.springsecurityjpa.authority;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    public final String name;

    Role(String label) {
        this.name = label;
    }
}
