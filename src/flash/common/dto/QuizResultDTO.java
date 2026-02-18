package flash.common.dto;

import java.io.Serializable;
import java.util.List;

public record QuizResultDTO(
        String quizTitle,
        int correctCount,
        int totalCount,
        List<QuizQuestionResultDTO> details
) implements Serializable {}
