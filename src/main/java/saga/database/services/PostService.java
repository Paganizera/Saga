package saga.database.services;

import org.springframework.stereotype.Service;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.database.repositories.PostRepository;
import saga.dto.PostDTO;
import saga.exceptions.PostNotFoundException;

import java.util.List;
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

    public Post create(PostDTO.Create request) {
        Post post = new Post();
        post.setAuthor(request.author());
        post.setTitle(request.title());
        post.setContent(request.content());
        return postRep.save(post);
    }

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
        Post post = postRep.getPostById(id);
        if (post == null) {
            throw new PostNotFoundException("Post not found: " + id);
        }
        return toDetail(post);
    }

    public List<PostDTO.Summary> getAllSummaries() {
        return postRep.findAll().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        postRep.deleteById(id);
    }

    private PostDTO.Summary toSummary(Post post) {
        long count = comRep.countByPost_Id(post.getId());
        return new PostDTO.Summary(
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getPublishedAt(),
                count
        );
    }

    private PostDTO.Detail toDetail(Post post) {
        return new PostDTO.Detail(
                post.getId(),
                post.getAuthor(),
                post.getTitle(),
                post.getContent(),
                post.getPublishedAt()
        );
    }
}