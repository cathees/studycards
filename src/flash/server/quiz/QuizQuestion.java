package flash.server.quiz;

public class QuizQuestion {
    private final String questionText;
    private final String correctAnswer;

    // vom Benutzer gegebene Antwort
    private String givenAnswer;

    public QuizQuestion(String questionText, String correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }


    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public boolean isCorrect() {
        if (givenAnswer == null) return false;
        return givenAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
    }
}
