package saga.database.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import saga.database.Commentary;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CommentaryRepository extends JpaRepository<Commentary, UUID> {
    Commentary getCommentaryById(UUID id);

    long countByPost_Id(UUID postId);

    Page<Commentary> findByPost_Id(UUID postId, Pageable pageable);

    interface PostCount {
        UUID getPostId();

        long getTotal();
    }

    @Query("select c.post.id as postId, count(c) as total from Commentary c " +
            "where c.post.id in :ids group by c.post.id")
    List<PostCount> countByPostIds(@Param("ids") Collection<UUID> ids);
}
