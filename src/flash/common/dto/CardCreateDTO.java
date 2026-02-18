package flash.common.dto;

import java.io.Serializable;

public record CardCreateDTO(
        String category,
        String question,
        String answer
) implements Serializable {}