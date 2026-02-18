package flash.server.quiz;

import flash.common.dto.*;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.NotFoundException;
import flash.common.exceptions.ValidationException;
import flash.common.rmi.QuizService;
import flash.server.auth.SessionManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;
import flash.server.cards.CardRepository;

public class QuizServiceImpl extends UnicastRemoteObject implements QuizService {

    private final SessionManager sessionManager;
    private final QuizSessionRepository sessionRepo;
    private final CardRepository cardRepo;

    public QuizServiceImpl(SessionManager sessionManager, QuizSessionRepository sessionRepo, CardRepository cardRepo)
            throws RemoteException {
        this.sessionManager = sessionManager;
        this.sessionRepo = sessionRepo;
        this.cardRepo = cardRepo;
    }

    @Override
    public List<QuizInfoDTO> listQuizzes(String sessionId)
            throws RemoteException, AuthFailedException {

        sessionManager.requireUserEmail(sessionId);

        List<CardDTO> cards = cardRepo.listAll();

        // Group by category and create one quiz per category
        return cards.stream()
                .filter(c -> c.category() != null && !c.category().isBlank())
                .collect(java.util.stream.Collectors.groupingBy(c -> c.category().trim()))
                .entrySet()
                .stream()
                .filter(e -> !e.getValue().isEmpty())
                .map(e -> {
                    String category = e.getKey();
                    int count = e.getValue().size();

                    String quizId = QuizIdUtil.quizIdFromCategory(category);
                    String title = category + " Quiz";

                    String level = (count >= 12) ? "Schwer" : (count >= 6) ? "Mittel" : "Einfach";
                    String duration = "~" + Math.max(3, (int) Math.ceil(count * 1.2)) + " Min";
                    String color = pickColor(category); // optional helper (see below)

                    return new QuizInfoDTO(
                            quizId,
                            category,
                            level,
                            title,
                            "Quiz basierend auf Ihren Lernkarten (" + count + " Fragen)",
                            count,
                            duration,
                            color
                    );
                })
                .sorted(java.util.Comparator.comparing(QuizInfoDTO::category))
                .toList();
    }

    private static String pickColor(String key) {
        String[] COLORS = {"#1a56ff", "#16a34a", "#a855f7", "#ea580c", "#0ea5e9", "#ef4444"};
        int idx = Math.abs((key == null ? 0 : key.hashCode())) % COLORS.length;
        return COLORS[idx];
    }


    @Override
    public QuizStartDTO startQuiz(String sessionId, String quizId)
            throws RemoteException, AuthFailedException, NotFoundException {

        String email = sessionManager.requireUserEmail(sessionId);

        List<CardDTO> all = cardRepo.listAll();

        // Find the category belonging to this quizId
        String category = all.stream()
                .map(CardDTO::category)
                .filter(c -> c != null && !c.isBlank())
                .map(String::trim)
                .filter(c -> QuizIdUtil.quizIdFromCategory(c).equals(quizId))
                .findFirst()
                .orElse(null);

        if (category == null) {
            throw new NotFoundException("Quiz nicht gefunden.");
        }

        List<CardDTO> cardsInCategory = all.stream()
                .filter(c -> c.category() != null && c.category().trim().equalsIgnoreCase(category))
                .toList();

        if (cardsInCategory.isEmpty()) {
            throw new NotFoundException("Quiz hat keine Fragen.");
        }

        // Build questions from the cards
        List<QuizQuestion> questions = cardsInCategory.stream()
                .map(c -> new QuizQuestion(c.question(), c.answer()))
                .toList();

        // Build definition dynamically
        int count = questions.size();
        String level = (count >= 12) ? "Schwer" : (count >= 6) ? "Mittel" : "Einfach";
        String duration = "~" + Math.max(3, (int) Math.ceil(count * 1.2)) + " Min";
        String title = category + " Quiz";

        QuizDefinition quizDef = new QuizDefinition(
                quizId,
                category,
                level,
                title,
                "Quiz basierend auf Ihren Lernkarten (" + count + " Fragen)",
                count,
                duration,
                pickColor(category)
        );

        String quizSessionId = UUID.randomUUID().toString();
        sessionRepo.put(new QuizSession(quizSessionId, email, quizDef, questions));

        return new QuizStartDTO(quizSessionId, quizDef.title());
    }


    @Override
    public QuizQuestionDTO getCurrentQuestion(String sessionId, String quizSessionId)
            throws RemoteException, AuthFailedException, NotFoundException {

        String email = sessionManager.requireUserEmail(sessionId);

        QuizSession s = sessionRepo.get(quizSessionId);
        if (s == null || !s.ownerEmail().equals(email)) throw new NotFoundException("Quiz-Session nicht gefunden.");

        if (s.finished()) throw new NotFoundException("Quiz ist bereits beendet.");

        QuizQuestion q = s.current();
        assert q != null;
        return new QuizQuestionDTO(s.index() + 1, s.total(), q.getQuestionText());
    }

    @Override
    public AnswerResultDTO submitAnswer(String sessionId, String quizSessionId, String answer)
            throws RemoteException, AuthFailedException, NotFoundException, ValidationException {

        String email = sessionManager.requireUserEmail(sessionId);

        String a = answer == null ? "" : answer.trim();
        if (a.isEmpty()) throw new ValidationException("Antwort darf nicht leer sein.");

        QuizSession s = sessionRepo.get(quizSessionId);
        if (s == null || !s.ownerEmail().equals(email)) throw new NotFoundException("Quiz-Session nicht gefunden.");
        if (s.finished()) throw new NotFoundException("Quiz ist bereits beendet.");

        QuizQuestion q = s.current();
        assert q != null;
        q.setGivenAnswer(a);
        boolean correct = AnswerMatcher.isCorrect(a, q.getCorrectAnswer());
        s.register(correct);
        return new AnswerResultDTO(correct, s.finished());
    }

    @Override
    public QuizResultDTO getResult(String sessionId, String quizSessionId)
            throws RemoteException, AuthFailedException, NotFoundException {

        String email = sessionManager.requireUserEmail(sessionId);

        QuizSession s = sessionRepo.get(quizSessionId);
        if (s == null || !s.ownerEmail().equals(email)) {
            throw new NotFoundException("Quiz-Session nicht gefunden.");
        }

        int total = s.total();
        int correct = s.correctCount();

        List<QuizQuestionResultDTO> details = new java.util.ArrayList<>();
        for (int i = 0; i < total; i++) {
            QuizQuestion q = s.questions().get(i);

            String given = q.getGivenAnswer();
            String corr  = q.getCorrectAnswer();

            boolean isCorrect = AnswerMatcher.isCorrect(q.getGivenAnswer(), q.getCorrectAnswer());

            details.add(new QuizQuestionResultDTO(
                    i + 1,
                    q.getQuestionText(),
                    given,
                    corr,
                    isCorrect
            ));
        }

        return new QuizResultDTO(s.quiz().title(), correct, total, details);
    }

}
