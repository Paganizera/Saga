package saga.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saga.database.Post;
import saga.database.services.PostService;
import saga.dto.PostDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDTO.Summary> getAll() {
        return postService.getAllSummaries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO.Detail> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getDetailById(id));

    }

    @PostMapping
    public PostDTO.Detail create(@RequestBody PostDTO.Create request) {
        Post saved = postService.create(request);
        return new PostDTO.Detail(
                saved.getId(),
                saved.getAuthor(),
                saved.getTitle(),
                saved.getContent(),
                saved.getPublishedAt()
        );
    }

    @PutMapping("/{id}")
    public PostDTO.Detail update(@PathVariable UUID id, @RequestBody PostDTO.Update request) {
        Post updated = postService.update(id, request);
        return new PostDTO.Detail(
                updated.getId(),
                updated.getAuthor(),
                updated.getTitle(),
                updated.getContent(),
                updated.getPublishedAt()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}