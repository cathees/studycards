package flash.common.rmi;

import flash.common.dto.SessionDTO;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.ValidationException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthService extends Remote {

    SessionDTO login(String email, String password)
            throws RemoteException, AuthFailedException;

    SessionDTO register(String name, String email, String password)
            throws RemoteException, ValidationException;
}