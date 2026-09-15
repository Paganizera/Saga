package saga.controllers;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.database.repositories.PostRepository;
import saga.database.services.PostService;
import saga.dto.PostDTO;
import saga.exceptions.PostNotFoundException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostTest {

    @Mock
    private PostRepository postRep;

    @Mock
    private CommentaryRepository comRep;

    @InjectMocks
    private PostService postService;

    private Post samplePost;
    private UUID postId;

    private static final String AUTHOR = "Jotaro";
    private static final String TITLE = "MADE IN HEAVEN";
    private static final String CONTENT = "Star Platinum ORA ORA ORA ORA ORA";
    private static final String UPDATED_TITLE = "Watashi no na wa Kira Yoshikage";
    private static final String UPDATED_CONTENT = " Nenre san-juu-san sai. Jitaku wa Morioh-cho hokuto bu no bessou chitai;";
    
    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        samplePost = new Post();
        samplePost.setId(postId);
        samplePost.setAuthor(AUTHOR);
        samplePost.setTitle(TITLE);
        samplePost.setContent(CONTENT);
        samplePost.setPublishedAt(OffsetDateTime.now());
    }

    @Test
    void createPost() {
        PostDTO.Create request = new PostDTO.Create(AUTHOR, TITLE, CONTENT);
        when(postRep.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.create(request);

        assertThat(result.getAuthor()).isEqualTo(AUTHOR);
        assertThat(result.getTitle()).isEqualTo(TITLE);
        verify(postRep).save(any(Post.class));
    }

    @Test
    void getDetail() {
        when(postRep.getPostById(postId)).thenReturn(samplePost);

        PostDTO.Detail detail = postService.getDetailById(postId);

        assertThat(detail.id()).isEqualTo(postId);
        assertThat(detail.title()).isEqualTo(TITLE);
    }

    @Test
    void getDetailNoPost() {
        when(postRep.getPostById(postId)).thenReturn(null);

        assertThatThrownBy(() -> postService.getDetailById(postId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(postId.toString());
    }

    @Test
    void summaryIncludesCommentaryCount() {
        when(postRep.findAll()).thenReturn(List.of(samplePost));
        when(comRep.countByPost_Id(postId)).thenReturn(3L);

        List<PostDTO.Summary> summaries = postService.getAllSummaries();

        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).commentaryCount()).isEqualTo(3L);
    }

    @Test
    void updatePost() {
        when(postRep.getPostById(postId)).thenReturn(samplePost);
        when(postRep.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        PostDTO.Update request = new PostDTO.Update(UPDATED_TITLE, UPDATED_CONTENT);
        Post result = postService.update(postId, request);

        assertThat(result.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(result.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(result.getAuthor()).isEqualTo(AUTHOR); // não deve mudar
    }

    @Test
    void updatePostNotFound() {
        when(postRep.getPostById(postId)).thenReturn(null);
        PostDTO.Update request = new PostDTO.Update("X", "Y");

        assertThatThrownBy(() -> postService.update(postId, request))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deletePost() {
        postService.delete(postId);
        verify(postRep).deleteById(postId);
    }
}