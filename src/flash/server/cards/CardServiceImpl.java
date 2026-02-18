package flash.server.cards;

import flash.common.dto.CardCreateDTO;
import flash.common.dto.CardDTO;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.NotFoundException;
import flash.common.exceptions.ValidationException;
import flash.common.rmi.CardService;
import flash.server.auth.SessionManager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;

public class CardServiceImpl extends UnicastRemoteObject implements CardService {

    private final SessionManager sessionManager;
    private final CardRepository cardRepo;

    public CardServiceImpl(SessionManager sessionManager, CardRepository cardRepo) throws RemoteException {
        this.sessionManager = sessionManager;
        this.cardRepo = cardRepo;
    }

    @Override
    public List<CardDTO> listCards(String sessionId) throws RemoteException, AuthFailedException {
        String email = sessionManager.requireUserEmail(sessionId);
        return cardRepo.listByOwner(email).stream().map(CardServiceImpl::toDTO).toList();
    }

    @Override
    public CardDTO createCard(String sessionId, CardCreateDTO dto)
            throws RemoteException, AuthFailedException, ValidationException {

        String email = sessionManager.requireUserEmail(sessionId);
        String[] categories = {"Geographie", "Geschichte", "Mathematik", "Physik",
                "Chemie", "Biologie", "Literatur",
                "Sprachen", "Informatik", "Kunst"};

        String cat = dto.category() == null ? "" : dto.category().trim();
        boolean isCustomCategory = Arrays.stream(categories).noneMatch(c -> c.equalsIgnoreCase(cat));
        System.out.println(isCustomCategory);
        String q = dto.question() == null ? "" : dto.question().trim();
        String a = dto.answer() == null ? "" : dto.answer().trim();

        if (cat.isEmpty()) throw new ValidationException("Kategorie darf nicht leer sein.");
        if (q.isEmpty()) throw new ValidationException("Frage darf nicht leer sein.");
        if (a.isEmpty()) throw new ValidationException("Antwort darf nicht leer sein.");

        return toDTO(cardRepo.create(email, cat, isCustomCategory, q, a));
    }

    @Override
    public void deleteCard(String sessionId, long cardId)
            throws RemoteException, AuthFailedException, NotFoundException {

        String email = sessionManager.requireUserEmail(sessionId);

        Card existing = cardRepo.find(cardId);
        if (existing == null) throw new NotFoundException("Karte nicht gefunden.");
        if (!existing.ownerEmail().equals(email)) throw new NotFoundException("Karte nicht gefunden."); // no info leak

        cardRepo.delete(cardId);
    }

    private static CardDTO toDTO(Card c) {
        return new CardDTO(c.id(), c.category(), c.isCustomCategory(), c.question(), c.answer(), c.created());
    }
}
