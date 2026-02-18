import flash.common.dto.CardCreateDTO;
import flash.common.dto.CardDTO;
import flash.server.auth.SessionManager;
import flash.server.cards.CardRepository;
import flash.server.cards.InMemoryCardRepository;
import flash.server.cards.CardServiceImpl;
import flash.server.quiz.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerStateIntegrationTest {

    @Test
    void twoSessions_shareSameServerState_cardsVisibleAfterCreate() throws Exception {
        // Arrange: one server instance
        SessionManager sessionManager = new SessionManager();
        CardRepository cardRepo = new InMemoryCardRepository();

        CardServiceImpl cardService = new CardServiceImpl(sessionManager, cardRepo);

        String s1 = "test1";
        sessionManager.add(s1,"a@test.com");
        String s2 = "test2";
        sessionManager.add(s2,"b@test.com");

        // Act: client 1 creates a card
        cardService.createCard(s1, new CardCreateDTO("Geographie", "Hauptstadt von AT?", "Wien"));

        // Assert: client 1 can list it
        List<CardDTO> aCards = cardService.listCards(s1);
        assertEquals(1, aCards.size());

        // Assert: client 2 cannot see client 1's cards if your design is per-owner
        List<CardDTO> bCards = cardService.listCards(s2);
        assertEquals(0, bCards.size());
    }
}
