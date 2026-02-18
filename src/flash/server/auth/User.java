package flash.server.auth;

public class User {
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Role role;

    public enum Role {
        ADMIN, USER
    }

    public User(String name, String email, String passwordHash, Role role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
}
