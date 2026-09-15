package saga.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class PostDTO {
    public record Create(
            String author,
            String title,
            String content
    ) {
    }

    public record Detail(
            UUID id,
            String author,
            String title,
            String content,
            OffsetDateTime publishedAt
    ) {
    }

    public record Summary(
            UUID id,
            String author,
            String title,
            OffsetDateTime publishedAt,
            long commentaryCount
    ) {
    }

    public record Update(
            String title,
            String content
    ) {
    }
}
