package flash.common.dto;

import java.io.Serializable;

public record QuizStartDTO(
        String quizSessionId,
        String quizTitle
) implements Serializable {}
