package flash.server.quiz;

import java.util.List;

public record QuizDefinition(
        String id,
        String category,
        String level,
        String title,
        String description,
        int questions,
        String duration,
        String color
) {}
