package saga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class PostDTO {
    public record Create(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 50000) String content
    ) {
    }

    public record Detail(UUID id, String author, String title, String content, OffsetDateTime publishedAt) {
    }

    public record Summary(UUID id, String author, String title, String excerpt, OffsetDateTime publishedAt,
                          long commentaryCount) {
    }

    public record Update(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(max = 50000) String content
    ) {
    }
}
