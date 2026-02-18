package flash.common.rmi;

import flash.common.dto.*;
import flash.common.exceptions.AuthFailedException;
import flash.common.exceptions.NotFoundException;
import flash.common.exceptions.ValidationException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface QuizService extends Remote {

    List<QuizInfoDTO> listQuizzes(String sessionId)
            throws RemoteException, AuthFailedException;

    QuizStartDTO startQuiz(String sessionId, String quizId)
            throws RemoteException, AuthFailedException, NotFoundException;

    QuizQuestionDTO getCurrentQuestion(String sessionId, String quizSessionId)
            throws RemoteException, AuthFailedException, NotFoundException;

    AnswerResultDTO submitAnswer(String sessionId, String quizSessionId, String answer)
            throws RemoteException, AuthFailedException, NotFoundException, ValidationException;

    QuizResultDTO getResult(String sessionId, String quizSessionId)
            throws RemoteException, AuthFailedException, NotFoundException;
}
