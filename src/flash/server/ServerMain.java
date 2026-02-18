package flash.server;

import flash.common.rmi.AuthService;
import flash.common.rmi.CardService;
import flash.common.rmi.QuizService;
import flash.server.auth.AuthServiceImpl;
import flash.server.auth.SessionManager;
import flash.server.auth.UserRepository;
import flash.server.cards.CardRepository;
import flash.server.cards.CardServiceImpl;
import flash.server.cards.InMemoryCardRepository;
import flash.server.quiz.QuizServiceImpl;
import flash.server.quiz.QuizSessionRepository;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class ServerMain {

    private static final int PORT = 1099;

    private ServerMain() {}

    static void main() {
        try {
            UserRepository userRepo = new UserRepository();
            SessionManager sessionManager = new SessionManager();

            AuthService authService = new AuthServiceImpl(userRepo, sessionManager);
            CardRepository cardRepo = new InMemoryCardRepository();
            CardService cardService = new CardServiceImpl(sessionManager, cardRepo);
            QuizSessionRepository quizSessionRepo = new QuizSessionRepository();
            QuizService quizService = new QuizServiceImpl(sessionManager, quizSessionRepo, cardRepo);


            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("AuthService", authService);
            registry.rebind("CardService", cardService);
            registry.rebind("QuizService", quizService);

            System.out.println("FLASH server running on port " + PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
