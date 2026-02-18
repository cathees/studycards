import flash.common.dto.*;
import flash.server.auth.SessionManager;
import flash.server.cards.*;
import flash.server.quiz.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizFlowIntegrationTest {

    @Test
    void quizSession_isServerSide_andAdvancesOnSubmit() throws Exception {
        SessionManager sessionManager = new SessionManager();
        CardRepository cardRepo = new InMemoryCardRepository();
        QuizSessionRepository quizSessionRepo = new QuizSessionRepository();

        CardServiceImpl cardService = new CardServiceImpl(sessionManager, cardRepo);
        QuizServiceImpl quizService = new QuizServiceImpl(sessionManager, quizSessionRepo, cardRepo);

        String sessionId = "test";
        sessionManager.add(sessionId,"u@test.com");

        // Two questions => two steps
        cardService.createCard(sessionId, new CardCreateDTO("Geographie", "AT?", "Wien"));
        cardService.createCard(sessionId, new CardCreateDTO("Geographie", "DE?", "Berlin"));

        String quizId = quizService.listQuizzes(sessionId).stream()
                .filter(q -> q.category().equalsIgnoreCase("Geographie"))
                .findFirst().orElseThrow()
                .quizId();

        QuizStartDTO started = quizService.startQuiz(sessionId, quizId);

        // Q1
        QuizQuestionDTO q1 = quizService.getCurrentQuestion(sessionId, started.quizSessionId());
        assertEquals(1, q1.index());
        assertEquals(2, q1.total());

        AnswerResultDTO r1 = quizService.submitAnswer(sessionId, started.quizSessionId(), "Wien");
        assertFalse(r1.finished());

        // Q2
        QuizQuestionDTO q2 = quizService.getCurrentQuestion(sessionId, started.quizSessionId());
        assertEquals(2, q2.index());

        AnswerResultDTO r2 = quizService.submitAnswer(sessionId, started.quizSessionId(), "Berlin");
        assertTrue(r2.finished());

        QuizResultDTO result = quizService.getResult(sessionId, started.quizSessionId());
        assertEquals(2, result.totalCount());
        assertTrue(result.correctCount() >= 0 && result.correctCount() <= 2);
    }
}
