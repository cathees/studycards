import flash.common.dto.*;
import flash.server.auth.SessionManager;
import flash.server.cards.*;
import flash.server.quiz.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizListIntegrationTest {

    @Test
    void listQuizzes_isDerivedFromCardsByCategory() throws Exception {
        SessionManager sessionManager = new SessionManager();
        CardRepository cardRepo = new InMemoryCardRepository();
        QuizSessionRepository sessionRepo = new QuizSessionRepository();

        // Server services
        CardServiceImpl cardService = new CardServiceImpl(sessionManager, cardRepo);
        QuizServiceImpl quizService = new QuizServiceImpl(sessionManager, sessionRepo, cardRepo);

        String sessionId = "test";
        sessionManager.add(sessionId,"u@test.com");

        // Create 2 categories
        cardService.createCard(sessionId, new CardCreateDTO("Geographie", "AT?", "Wien"));
        cardService.createCard(sessionId, new CardCreateDTO("Geographie", "DE?", "Berlin"));
        cardService.createCard(sessionId, new CardCreateDTO("Literatur", "Faust?", "Goethe"));

        List<QuizInfoDTO> quizzes = quizService.listQuizzes(sessionId);

        assertEquals(2, quizzes.size());
        assertTrue(quizzes.stream().anyMatch(q -> q.category().equalsIgnoreCase("Geographie") && q.questions() == 2));
        assertTrue(quizzes.stream().anyMatch(q -> q.category().equalsIgnoreCase("Literatur") && q.questions() == 1));
    }
}
