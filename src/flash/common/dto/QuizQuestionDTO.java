package flash.common.dto;

import java.io.Serializable;

public record QuizQuestionDTO(
        int index,          // 1-based
        int total,
        String questionText
) implements Serializable {}
