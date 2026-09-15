package saga.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import saga.database.Post;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    Post getPostById(UUID id);
}