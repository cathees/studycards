package flash.client.main;

import flash.common.dto.SessionDTO;

public final class ClientSession {

    private static volatile SessionDTO session;

    private ClientSession() {}

    public static void set(SessionDTO s) {
        session = s;
    }

    public static SessionDTO get() {
        return session;
    }

    public static String sessionId() {
        return session == null ? null : session.sessionId();
    }

    public static String email() {
        return session == null ? null : session.email();
    }

    public static String name() {
        return session == null ? null : session.name();
    }

    public static boolean isLoggedIn() {
        return session != null;
    }

    public static void clear() {
        session = null;
    }
}
