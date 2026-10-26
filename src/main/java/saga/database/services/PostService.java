package saga.database.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.database.repositories.PostRepository;
import saga.dto.PostDTO;
import saga.exceptions.PostNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRep;
    private final CommentaryRepository comRep;

    public PostService(PostRepository postRep, CommentaryRepository comRep) {
        this.postRep = postRep;
        this.comRep = comRep;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Post create(String author, PostDTO.Create request) {
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(request.title());
        post.setContent(request.content());
        return postRep.save(post);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Post update(UUID id, PostDTO.Update request) {
        Post existing = postRep.getPostById(id);
        if (existing == null) {
            throw new PostNotFoundException("Post not found: " + id);
        }
        existing.setTitle(request.title());
        existing.setContent(request.content());
        return postRep.save(existing);
    }

    public Post getById(UUID id) {
        return postRep.getPostById(id);
    }

    public PostDTO.Detail getDetailById(UUID id) {
        Post p = postRep.getPostById(id);
        if (p == null) {
            throw new PostNotFoundException("Post not found: " + id);
        }
        return new PostDTO.Detail(p.getId(), p.getAuthor(), p.getTitle(), p.getContent(), p.getPublishedAt());
    }

    public Page<PostDTO.Summary> getSummaries(Pageable pageable) {
        Page<Post> page = postRep.findAll(pageable);
        List<UUID> ids = page.getContent().stream().map(Post::getId).toList();
        Map<UUID, Long> counts = ids.isEmpty() ? Map.of() :
                comRep.countByPostIds(ids).stream()
                        .collect(Collectors.toMap(CommentaryRepository.PostCount::getPostId,
                                CommentaryRepository.PostCount::getTotal));
        return page.map(p -> new PostDTO.Summary(
                p.getId(), p.getAuthor(), p.getTitle(), excerpt(p.getContent()), p.getPublishedAt(),
                counts.getOrDefault(p.getId(), 0L)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        if (!postRep.existsById(id)) {
            throw new PostNotFoundException("Post not found: " + id);
        }
        postRep.deleteById(id);
    }

    private static String excerpt(String markdown) {
        String t = markdown
                .replaceAll("(?s)```.*?```", " ")
                .replaceAll("!?\\[([^\\]]*)\\]\\([^)]*\\)", "$1")
                .replaceAll("(?m)^\\s*(#{1,6}|>|[-*+]|\\d+\\.)\\s+", "")
                .replaceAll("[*_~`|]+", "")
                .replaceAll("\\s+", " ")
                .trim();
        if (t.length() <= 180) return t;
        int cut = t.lastIndexOf(' ', 180);
        return t.substring(0, cut > 0 ? cut : 180) + "…";
    }
}
