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
    public CommentaryDTO.Response create(@PathVariable UUID postId, @RequestBody CommentaryDTO.Create request) {
        Commentary saved = commentaryService.create(postId, request);
        return new CommentaryDTO.Response(saved.getId(), saved.getAuthor(), saved.getContent(), saved.getPublishedAt());
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
    public ResponseEntity<CommentaryDTO.Response> getById(@PathVariable UUID id) {
        Commentary commentary = commentaryService.getById(id);
        if (commentary == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(
                new CommentaryDTO.Response(
                        commentary.getId(),
                        commentary.getAuthor(),
                        commentary.getContent(),
                        commentary.getPublishedAt()
                ));
    }

    @DeleteMapping("/commentaries/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        commentaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}