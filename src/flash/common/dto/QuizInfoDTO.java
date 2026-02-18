package flash.common.dto;

import java.io.Serializable;

public record QuizInfoDTO(
        String quizId,
        String category,
        String level,
        String title,
        String description,
        int questions,
        String duration,
        String color
) implements Serializable {}