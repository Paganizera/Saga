package saga.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saga.database.Post;
import saga.database.repositories.PostRepository;
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
        try {
            return ResponseEntity.ok(postService.getDetailById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Post create(@RequestBody PostDTO.Create request) {
        return postService.create(request);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable UUID id, @RequestBody PostDTO.Update request) {
        return postService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }
}