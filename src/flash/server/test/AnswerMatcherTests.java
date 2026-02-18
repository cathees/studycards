import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnswerMatcherTests {



    @Test
    void exactMatch_isCorrect() {
        assertTrue(flash.server.quiz.AnswerMatcher.isCorrect("Berlin", "Berlin"));
    }

    @Test
    void normalization_ignoresCaseAndSpecialChars() {
        assertTrue(flash.server.quiz.AnswerMatcher.isCorrect("bErl!in", "Berlin"));
    }

    @Test
    void caseAndPunctuationIgnored_isCorrect() {
        assertTrue(flash.server.quiz.AnswerMatcher.isCorrect("JoHaNn-Wolfgang von Goethe!!",
                "Johann Wolfgang von Goethe"));
    }

    @Test
    void variantsCommaSeparated_isCorrect() {
        assertTrue(flash.server.quiz.AnswerMatcher.isCorrect("Vienna", "Wien, Vienna"));
    }

    @Test
    void smallTypo_isCorrect() {
        assertTrue(flash.server.quiz.AnswerMatcher.isCorrect("Berli", "Berlin"));
    }

    @Test
    void wrongAnswer_isFalse() {
        assertFalse(flash.server.quiz.AnswerMatcher.isCorrect("Hamburg", "Berlin"));
    }
}
