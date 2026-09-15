package saga.database.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import saga.database.Commentary;

import java.util.UUID;

public interface CommentaryRepository extends JpaRepository<Commentary, UUID> {
    Commentary getCommentaryById(UUID id);
    long countByPost_Id(UUID postId);
    Page<Commentary> findByPost_Id(UUID postId, Pageable pageable);
}