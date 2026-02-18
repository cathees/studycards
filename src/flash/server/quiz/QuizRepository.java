package flash.server.quiz;

import java.util.List;
import java.util.Map;

public final class QuizRepository {

    private static final List<QuizDefinition> QUIZZES = List.of(
            new QuizDefinition("geo-basic", "Geographie", "Einfach",
                    "Geographie Grundlagen",
                    "Teste dein Wissen über Hauptstädte, Länder und Kontinente",
                    5, "~5 Min", "#1a56ff"),

            new QuizDefinition("science-mix", "Wissenschaft", "Mittel",
                    "Naturwissenschaften Mix",
                    "Fragen aus Physik, Chemie und Biologie",
                    8, "~10 Min", "#16a34a"),

            new QuizDefinition("history-de", "Geschichte", "Mittel",
                    "Deutsche Geschichte",
                    "Wichtige Ereignisse der deutschen Geschichte",
                    6, "~8 Min", "#a855f7"),

            new QuizDefinition("literature", "Literatur", "Schwer",
                    "Literatur Klassiker",
                    "Berühmte Autoren und ihre Werke",
                    5, "~7 Min", "#ea580c")
    );

    // ===== QUESTION DATA (NEW) =====

    private static final Map<String, List<QuizQuestion>> QUESTIONS = Map.of(
            "geo-basic", List.of(
                    new QuizQuestion("Was ist die Hauptstadt von Deutschland?", "Berlin"),
                    new QuizQuestion("Wie heißt die Hauptstadt von Österreich?", "Wien"),
                    new QuizQuestion("Welcher Kontinent ist der größte?", "Asien"),
                    new QuizQuestion("Durch welches Land fließt die Seine?", "Frankreich"),
                    new QuizQuestion("Wie heißt die Hauptstadt von Italien?", "Rom")
            ),

            "science-mix", List.of(
                    new QuizQuestion("Wofür steht H₂O?", "Wasser"),
                    new QuizQuestion("Wie viele Planeten hat unser Sonnensystem?", "8"),
                    new QuizQuestion("Welche Einheit hat elektrische Spannung?", "Volt"),
                    new QuizQuestion("Welches Gas brauchen wir zum Atmen?", "Sauerstoff"),
                    new QuizQuestion("Wie heißt der Prozess der Pflanzen zur Energiegewinnung aus Licht?", "Photosynthese"),
                    new QuizQuestion("Wie heißt die kleinste Einheit eines chemischen Elements?", "Atom"),
                    new QuizQuestion("Welche Kraft zieht Körper zur Erde?", "Gravitation"),
                    new QuizQuestion("Welches Organ pumpt Blut durch den Körper?", "Herz")
            ),

            "history-de", List.of(
                    new QuizQuestion("In welchem Jahr fiel die Berliner Mauer?", "1989"),
                    new QuizQuestion("Wer war der erste Bundeskanzler der BRD?", "Konrad Adenauer"),
                    new QuizQuestion("Wie hieß die deutsche Wiedervereinigung offiziell?", "Deutsche Einheit"),
                    new QuizQuestion("Welche Stadt war Hauptstadt der DDR?", "Ost-Berlin"),
                    new QuizQuestion("Wie hieß der deutsche Kaiser 1871?", "Wilhelm I"),
                    new QuizQuestion("In welchem Jahr begann der Erste Weltkrieg?", "1914")
            ),

            "literature", List.of(
                    new QuizQuestion("Wer schrieb 'Faust'?", "Goethe"),
                    new QuizQuestion("Wer schrieb 'Die Leiden des jungen Werther'?", "Goethe"),
                    new QuizQuestion("Wer schrieb 'Der Prozess'?", "Kafka"),
                    new QuizQuestion("Wer schrieb 'Effi Briest'?", "Fontane"),
                    new QuizQuestion("Wer schrieb 'Nathan der Weise'?", "Lessing")
            )
    );

    private QuizRepository() {}

    public static List<QuizDefinition> findAll() {
        return QUIZZES;
    }

    public static QuizDefinition findById(String id) {
        return QUIZZES.stream()
                .filter(q -> q.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static List<QuizQuestion> loadQuestionsFor(String quizId) {
        List<QuizQuestion> q = QUESTIONS.get(quizId);
        return (q != null) ? q : List.of();
    }
}