package saga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class CommentaryDTO {
    public record Response(
            UUID id,
            String author,
            String content,
            OffsetDateTime publishedAt
    ) {}

    public record Create(
            String author,
            String content
    ) {}
}
