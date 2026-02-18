package flash.client.main;
import flash.common.rmi.AuthService;
import flash.common.rmi.CardService;
import flash.common.rmi.QuizService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ServerGateway {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1099;

    // RMI binding names (must match ServerMain rebind names)
    private static final String AUTH_BINDING = "AuthService";
    private static final String CARD_BINDING = "CardService";
    private static final String QUIZ_BINDING = "QuizService";

    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private static volatile AuthService auth;
    private static volatile CardService cards;
    private static volatile QuizService quiz;

    private ServerGateway() { }

    public static AuthService auth() {
        if (auth == null) {
            synchronized (ServerGateway.class) {
                if (auth == null) auth = lookup(AUTH_BINDING, AuthService.class);
            }
        }
        return auth;
    }

    public static CardService cards() {
        if (cards == null) {
            synchronized (ServerGateway.class) {
                if (cards == null) cards = lookup(CARD_BINDING, CardService.class);
            }
        }
        return cards;
    }

    public static QuizService quiz() {
        if (quiz == null) {
            synchronized (ServerGateway.class) {
                if (quiz == null) quiz = lookup(QUIZ_BINDING, QuizService.class);
            }
        }
        return quiz;
    }

    private static <T> T lookup(String binding, Class<T> type) {
        try {
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            Object obj = registry.lookup(binding);
            return type.cast(obj);
        } catch (Exception e) {
            throw new RuntimeException("RMI lookup failed for " + binding + " on " + HOST + ":" + PORT, e);
        }
    }

    // optional: call on app shutdown
    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }
}