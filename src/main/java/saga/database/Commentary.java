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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, updatable = false)
    public String author;

    @Column(nullable = false, updatable = false)
    String content;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Post post;

    @PrePersist
    protected void onCreate() {
        // TODO: Dynamic timezone sounds good
        this.publishedAt = OffsetDateTime.now( ZoneId.of("America/Sao_Paulo"));
    }
}
