package flash.common.dto;

import java.io.Serializable;

public record AnswerResultDTO(
        boolean correct,
        boolean finished
) implements Serializable {}
