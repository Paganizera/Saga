package saga.database.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import saga.database.Commentary;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.dto.CommentaryDTO;
import saga.exceptions.CommentaryNotFoundException;

import java.util.UUID;

@Service
public class CommentaryService {
    private final CommentaryRepository comRep;
    private final PostService postService;

    public CommentaryService(CommentaryRepository comRep, PostService postService) {
        this.comRep = comRep;
        this.postService = postService;
    }

    public Commentary create(UUID postId, CommentaryDTO.Create request) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new CommentaryNotFoundException("Post not found: " + postId);
        }
        Commentary commentary = new Commentary();
        commentary.setAuthor(request.author());
        commentary.setContent(request.content());
        commentary.setPost(post);
        return comRep.save(commentary);
    }

    public Commentary getById(UUID id) {
        return comRep.getCommentaryById(id);
    }

    public Page<CommentaryDTO.Response> getByPostId(UUID postId, Pageable pageable) {
        return comRep.findByPost_Id(postId, pageable)
                .map(c -> new CommentaryDTO.Response(c.getId(), c.getAuthor(), c.getContent(), c.getPublishedAt()));
    }

    public void delete(UUID id) {
        comRep.deleteById(id);
    }
}