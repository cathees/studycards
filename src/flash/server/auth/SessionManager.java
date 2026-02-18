package flash.server.auth;

import flash.common.exceptions.AuthFailedException;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    private static final long TTL_SECONDS = 60L * 60L * 6L; // 6 hours

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public void add(String sessionId, String userEmail) {
        sessions.put(sessionId, new SessionInfo(userEmail, Instant.now().getEpochSecond()));
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public String requireUserEmail(String sessionId) throws AuthFailedException {
        if (sessionId == null || sessionId.isBlank()) {
            throw new AuthFailedException("Nicht angemeldet.");
        }

        SessionInfo info = sessions.get(sessionId);
        if (info == null) {
            throw new AuthFailedException("Session ungültig oder abgelaufen.");
        }

        long now = Instant.now().getEpochSecond();
        if (now - info.createdAtEpochSeconds() > TTL_SECONDS) {
            sessions.remove(sessionId);
            throw new AuthFailedException("Session abgelaufen. Bitte erneut anmelden.");
        }

        return info.userEmail();
    }

    private record SessionInfo(String userEmail, long createdAtEpochSeconds) {}
}
