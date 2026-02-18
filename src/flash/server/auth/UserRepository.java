package flash.server.auth;

import java.util.*;

public class UserRepository {

    private static final Map<String, User> USERS = new HashMap<>();

    static {
        // Demo-Benutzer
        USERS.put(
                "student@lernsystem.de",
                new User(
                        "Demo Student",
                        "student@lernsystem.de",
                        PasswordUtil.hash("student123"),
                        User.Role.USER)
        );
    }

    public static boolean exists(String email) {
        return USERS.containsKey(email.toLowerCase());
    }

    public static void addUser(User user) {
        USERS.put(user.getEmail().toLowerCase(), user);
        System.out.println(USERS.toString());
    }



    public static User login(String email, String password) {
        User user = USERS.get(email.toLowerCase());
        if (user == null) return null;

        if (!PasswordUtil.matches(password, user.getPasswordHash())) return null;

        return user;
    }

    public static User findByEmail(String email) { return USERS.get(email.toLowerCase()); }

}
