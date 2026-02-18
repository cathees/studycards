package flash.server.cards;

import java.time.LocalDate;

public record Card(
        long id,
        String ownerEmail,
        boolean isCustomCategory,
        String category,
        String question,
        String answer,
        LocalDate created
) {}