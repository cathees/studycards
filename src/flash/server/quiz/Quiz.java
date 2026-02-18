package flash.server.quiz;

import java.util.List;

public class Quiz {
    private final List<QuizQuestion> questions;
    private int index = 1;
    private int correctCount = 0;
    private final String title;

    public Quiz(String title, List<QuizQuestion> questions) {
        this.title = title;
        this.questions = questions;
    }

    // --- Answer tracking ---
    public void registerAnswer(boolean correct) {
        if (correct) correctCount++;
    }

    // --- Quiz state ---
    public int getCurrentIndex() {
        return index;
    }

    public QuizQuestion getCurrent() {
        return questions.get(index-1);
    }

    public void next() {
        if (index < questions.size()) {
            index++;
        }
    }

    // --- Quiz info ---
    public int getCorrectCount() {
        return correctCount;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFinished() {
        return index >= questions.size();
    }

    public Quiz restart() {
        Quiz q = new Quiz(title, questions);
        return q;
    }
}
