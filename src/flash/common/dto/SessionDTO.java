package flash.common.dto;

import java.io.Serializable;

public record SessionDTO(
        String sessionId,
        String email,
        String name
) implements Serializable {}