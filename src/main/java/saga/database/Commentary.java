package saga.database;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "commentary")
public class Commentary {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    public String author;

    @Column(nullable = false, updatable = false)
    String content;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        // TODO: Dynamic timezone sounds good
        this.publishedAt = OffsetDateTime.now( ZoneId.of("America/Sao_Paulo"));
    }
}
