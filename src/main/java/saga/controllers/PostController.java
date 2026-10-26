package saga.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import saga.database.Post;
import saga.database.services.PostService;
import saga.dto.PostDTO;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Page<PostDTO.Summary> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "publishedAt"));
        return postService.getSummaries(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.Detail> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getDetailById(id));
    }

    @PostMapping
    public PostDTO.Detail create(@Valid @RequestBody PostDTO.Create request, Authentication authentication) {
        Post saved = postService.create(authentication.getName(), request);
        return toDetail(saved);
    }

    @PutMapping("/{id}")
    public PostDTO.Detail update(@PathVariable UUID id, @Valid @RequestBody PostDTO.Update request) {
        return toDetail(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PostDTO.Detail toDetail(Post p) {
        return new PostDTO.Detail(p.getId(), p.getAuthor(), p.getTitle(), p.getContent(), p.getPublishedAt());
    }
}
