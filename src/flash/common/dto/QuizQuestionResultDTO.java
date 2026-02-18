package flash.common.dto;

import java.io.Serializable;

public record QuizQuestionResultDTO(
        int index,                 // 1-based
        String questionText,
        String givenAnswer,
        String correctAnswer,
        boolean correct
) implements Serializable {}
