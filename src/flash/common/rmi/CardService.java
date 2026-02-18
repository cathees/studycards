package flash.common.rmi;

import flash.common.dto.CardCreateDTO;
import flash.common.dto.CardDTO;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.ValidationException;
import flash.common.exceptions.NotFoundException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CardService extends Remote {

    List<CardDTO> listCards(String sessionId)
            throws RemoteException, AuthFailedException;

    CardDTO createCard(String sessionId, CardCreateDTO dto)
            throws RemoteException, AuthFailedException, ValidationException;

    void deleteCard(String sessionId, long cardId)
            throws RemoteException, AuthFailedException, NotFoundException;
}
