package flash.common.dto;

import java.io.Serializable;
import java.time.LocalDate;

public record CardDTO(
        long id,
        String category,
        boolean isCustomCategory,
        String question,
        String answer,
        LocalDate created
) implements Serializable {}
