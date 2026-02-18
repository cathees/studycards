package flash.server.quiz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class QuizSessionRepository {

    private final Map<String, QuizSession> sessions = new ConcurrentHashMap<>();

    public void put(QuizSession s) {
        sessions.put(s.id(), s);
    }

    public QuizSession get(String id) {
        return sessions.get(id);
    }
}
