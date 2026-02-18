package flash.server.quiz;

import java.util.List;

public final class QuizSession {
    private final String id;
    private final String ownerEmail;
    private final QuizDefinition quiz;
    private final List<QuizQuestion> questions;

    private int index; // 0-based
    private int correctCount;

    public QuizSession(String id, String ownerEmail, QuizDefinition quiz, List<QuizQuestion> questions) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (ownerEmail == null || ownerEmail.isBlank()) throw new IllegalArgumentException("ownerEmail must not be blank");
        if (quiz == null) throw new IllegalArgumentException("quiz must not be null");
        if (questions == null) throw new IllegalArgumentException("questions must not be null");

        this.id = id;
        this.ownerEmail = ownerEmail;
        this.quiz = quiz;
        this.questions = questions;

        this.index = 0;
        this.correctCount = 0;
    }

    public String id() { return id; }
    public String ownerEmail() { return ownerEmail; }
    public QuizDefinition quiz() { return quiz; }

    public int index() { return index; }              // 0-based
    public int correctCount() { return correctCount; }

    public int total() { return questions.size(); }
    public boolean finished() { return index >= questions.size(); }

    public List<QuizQuestion> questions() { return questions; }

    public QuizQuestion current() {
        if (finished()) return null;
        return questions.get(index);
    }

    public void register(boolean correct) {
        if (!finished()) {
            if (correct) correctCount++;
            index++;
        }
    }
}
