package flash.server.quiz;

public final class QuizIdUtil {
    private QuizIdUtil() {}

    public static String quizIdFromCategory(String category) {
        String c = category == null ? "" : category.trim().toLowerCase();
        c = c.replaceAll("[^a-z0-9äöüß ]", "");
        c = c.replaceAll("\\s+", "-");
        return "cat-" + c;
    }

    public static String categoryFromQuizId(String quizId) {
        if (quizId == null) return null;
        if (!quizId.startsWith("cat-")) return null;
        return quizId.substring("cat-".length()).replace("-", " ");
    }
}
