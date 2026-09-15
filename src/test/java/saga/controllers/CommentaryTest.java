package saga.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import saga.database.Commentary;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.database.services.CommentaryService;
import saga.database.services.PostService;
import saga.dto.CommentaryDTO;
import saga.exceptions.CommentaryNotFoundException;
import saga.exceptions.PostNotFoundException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentaryTest {

    @Mock
    private CommentaryRepository comRep;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentaryService commentaryService;

    private UUID postId;
    private Post post;
    
    private static final String AUTHOR = "Jotaro";
    private static final String CONTENT = "Star Platinum ORA ORA ORA ORA ORA";
    private static final String COMMENT = "Reaching the line just to start again";

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        post = new Post();
        post.setId(postId);
        post.setTitle("Armata Strigoi");
    }

    @Test
    void createCommentary() {
        when(postService.getById(postId)).thenReturn(post);
        when(comRep.save(any(Commentary.class))).thenAnswer(inv -> inv.getArgument(0));

        CommentaryDTO.Create request = new CommentaryDTO.Create(AUTHOR, CONTENT);
        Commentary result = commentaryService.create(postId, request);

        assertThat(result.getAuthor()).isEqualTo(AUTHOR);
        assertThat(result.getPost()).isEqualTo(post);
    }

    @Test
    void createCommentaryPostNotFound() {
        when(postService.getById(postId)).thenReturn(null);
        CommentaryDTO.Create request = new CommentaryDTO.Create(AUTHOR, CONTENT);

        assertThatThrownBy(() -> commentaryService.create(postId, request))
                .isInstanceOf(PostNotFoundException.class);

        verify(comRep, never()).save(any());
    }

    @Test
    void pageTest() {
        Commentary c = new Commentary();
        c.setId(UUID.randomUUID());
        c.setAuthor(AUTHOR);
        c.setContent(COMMENT);
        c.setPost(post);

        Pageable pageable = PageRequest.of(0, 20);
        when(comRep.findByPost_Id(postId, pageable)).thenReturn(new PageImpl<>(List.of(c), pageable, 1));

        var page = commentaryService.getByPostId(postId, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).author()).isEqualTo(AUTHOR);
    }

    @Test
    void deleteComment() {
        UUID id = UUID.randomUUID();
        commentaryService.delete(id);
        verify(comRep).deleteById(id);
    }
}