package flash.server.cards;

import flash.common.dto.CardDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class InMemoryCardRepository implements CardRepository {

    private final AtomicLong nextId = new AtomicLong(1);
    private final Map<Long, Card> byId = new ConcurrentHashMap<>();

    @Override
    public List<CardDTO> listAll() {
        return byId.values().stream()
                .map(c -> new CardDTO(
                        c.id(),
                        c.category(),
                        c.isCustomCategory(),
                        c.question(),
                        c.answer(),
                        c.created()
                ))
                .toList();
    }

    public List<Card> listByOwner(String ownerEmail) {
        return byId.values().stream()
                .filter(c -> c.ownerEmail().equals(ownerEmail))
                .sorted((a, b) -> Long.compare(b.id(), a.id()))
                .toList();
    }

    @Override
    public Card create(String ownerEmail, String category, boolean isCustomCategory,  String question, String answer) {
        long id = nextId.getAndIncrement();
        Card c = new Card(id, ownerEmail, isCustomCategory, category, question, answer, LocalDate.now());
        byId.put(id, c);
        return c;
    }

    @Override
    public Card find(long id) {
        return byId.get(id);
    }

    @Override
    public void delete(long id) {
        byId.remove(id);
    }
}
