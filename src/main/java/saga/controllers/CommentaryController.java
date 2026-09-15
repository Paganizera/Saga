package saga.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saga.database.Commentary;
import saga.database.services.CommentaryService;
import saga.dto.CommentaryDTO;

import java.util.UUID;

@RestController
public class CommentaryController {
    private final CommentaryService commentaryService;

    public CommentaryController(CommentaryService commentaryService) {
        this.commentaryService = commentaryService;
    }

    @PostMapping("/posts/{postId}/commentaries")
    public Commentary create(@PathVariable UUID postId, @RequestBody CommentaryDTO.Create request) {
        return commentaryService.create(postId, request);
    }

    @GetMapping("/posts/{postId}/commentaries")
    public Page<CommentaryDTO.Response> getByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return commentaryService.getByPostId(postId, pageable);
    }

    @GetMapping("/commentaries/{id}")
    public ResponseEntity<Commentary> getById(@PathVariable UUID id) {
        Commentary commentary = commentaryService.getById(id);
        return commentary != null ? ResponseEntity.ok(commentary) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/commentaries/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        commentaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}