package flash.server.cards;

import flash.common.dto.CardDTO;

import java.util.List;

public interface CardRepository {
    void delete(long id);
    Card find(long id);
    Card create(String ownerEmail, String category, boolean isCustomCategory,String question, String answer);
    List<Card> listByOwner(String ownerEmail);
    List<CardDTO> listAll();
}
