package saga.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import saga.database.Post;
import saga.database.repositories.CommentaryRepository;
import saga.database.repositories.PostRepository;
import saga.dto.CommentaryDTO;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommentaryIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentaryRepository commentaryRepository;

    private Post post;

    private static final String AUTHOR = "Jotaro";
    private static final String POST_TITLE = "Stand Proud";
    private static final String CONTENT = "Star Platinum ORA ORA ORA ORA ORA";
    private static final String COMMENT_AUTHOR = "KONO DIO DA";
    private static final String COMMENT_CONTENT = "Reaching the line just to start again";

    @BeforeEach
    void setUp() {
        commentaryRepository.deleteAll();
        postRepository.deleteAll();

        post = new Post();
        post.setAuthor(AUTHOR);
        post.setTitle(POST_TITLE);
        post.setContent(CONTENT);
        post = postRepository.save(post);
    }

    @Test
    void allowAnonymousCommentaries() throws Exception {
        CommentaryDTO.Create request = new CommentaryDTO.Create(COMMENT_AUTHOR, COMMENT_CONTENT);

        mockMvc.perform(post("/posts/{postId}/commentaries", post.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", is(COMMENT_AUTHOR)));
    }

    @Test
    void commentaryReturns4xxError() throws Exception {
        CommentaryDTO.Create request = new CommentaryDTO.Create(COMMENT_AUTHOR, COMMENT_CONTENT);

        mockMvc.perform(post("/posts/{postId}/commentaries", UUID.randomUUID())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void postShowPaginatedCommentaries() throws Exception {
        CommentaryDTO.Create request = new CommentaryDTO.Create(COMMENT_AUTHOR, COMMENT_CONTENT);
        mockMvc.perform(post("/posts/{postId}/commentaries", post.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/posts/{postId}/commentaries", post.getId())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].author", is(COMMENT_AUTHOR)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void commentaryAdmDelete() throws Exception {
        String response = createCommentary(COMMENT_CONTENT);
        String commentaryId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/commentaries/{id}", commentaryId))
                .andExpect(status().isNoContent());
    }

    @Test
    void commentaryDeletePrevention() throws Exception {
        String response = createCommentary(COMMENT_CONTENT);
        String commentaryId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/commentaries/{id}", commentaryId))
                .andExpect(status().is4xxClientError());
    }

    private String createCommentary(String content) throws Exception {
        CommentaryDTO.Create request = new CommentaryDTO.Create(COMMENT_AUTHOR, content);
        return mockMvc.perform(post("/posts/{postId}/commentaries", post.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();
    }
}