package flash.server.auth;

import flash.common.dto.SessionDTO;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.ValidationException;
import flash.common.rmi.AuthService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Locale;
import java.util.UUID;

public class AuthServiceImpl extends UnicastRemoteObject
        implements AuthService {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;

    public AuthServiceImpl(UserRepository userRepository,
                           SessionManager sessionManager)
            throws RemoteException {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    @Override
    public SessionDTO login(String email, String password)
            throws RemoteException, AuthFailedException {

        User user = UserRepository.findByEmail(email);

        if (user == null || !PasswordUtil.matches(password, PasswordUtil.hash(password))) {
            throw new AuthFailedException("E-Mail oder Passwort falsch.");
        }

        String sessionId = UUID.randomUUID().toString();
        sessionManager.add(sessionId, user.getEmail());

        return new SessionDTO(sessionId, user.getEmail(), user.getName());
    }

    @Override
    public SessionDTO register(String name, String email, String password)
            throws RemoteException, ValidationException {

        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String pw = password == null ? "" : password;

        if (cleanName.isEmpty()) throw new ValidationException("Name darf nicht leer sein.");
        if (cleanEmail.isEmpty()) throw new ValidationException("E-Mail darf nicht leer sein.");
        if (!cleanEmail.contains("@")) throw new ValidationException("E-Mail ist ungültig.");
        if (pw.length() < 6) throw new ValidationException("Passwort muss mindestens 6 Zeichen haben.");

        if (UserRepository.exists(cleanEmail)) {
            throw new ValidationException("E-Mail ist bereits registriert.");
        }

        String hash = PasswordUtil.hash(pw);
        UserRepository.addUser(new User(cleanName, cleanEmail, hash, User.Role.ADMIN));


        String sessionId = UUID.randomUUID().toString();
        sessionManager.add(sessionId, cleanEmail);
        return new SessionDTO(sessionId, cleanEmail, cleanName);
    }
}
