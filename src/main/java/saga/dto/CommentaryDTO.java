package saga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class CommentaryDTO {
    public record Response(UUID id, String author, String content, OffsetDateTime publishedAt) {
    }

    public record Create(
            @NotBlank @Size(max = 80) String author,
            @NotBlank @Size(max = 2000) String content
    ) {
    }
}
