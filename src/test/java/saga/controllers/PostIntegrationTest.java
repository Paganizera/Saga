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
import saga.database.repositories.PostRepository;
import saga.dto.PostDTO;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;

    private static final String AUTHOR = "Jotaro";
    private static final String POST_TITLE = "Stand Proud";
    private static final String CONTENT = "Star Platinum ORA ORA ORA ORA ORA";
    private static final String UPDATED_TITLE = "KONO DIO DA";
    private static final String UPDATED_CONTENT = "Reaching the line just to start again";

    @BeforeEach
    void cleanUp() {
        postRepository.deleteAll();
    }

    @Test
    void unexistentPostReturns404() throws Exception {
        mockMvc.perform(get("/posts/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPostAsAdmin() throws Exception {
        PostDTO.Create request = new PostDTO.Create (AUTHOR, POST_TITLE, CONTENT);

        mockMvc.perform(post("/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(POST_TITLE)))
                .andExpect(jsonPath("$.id", notNullValue()));

        assertEquals(1, postRepository.findAll().size());
    }

    @Test
    void createPostFailsWhenNonAdmin() throws Exception {
        PostDTO.Create  request = new PostDTO.Create (AUTHOR, POST_TITLE, CONTENT);

        mockMvc.perform(post("/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateContentAsAdmin() throws Exception {
        Post post = new Post();
        post.setAuthor(AUTHOR);
        post.setTitle(POST_TITLE);
        post.setContent(CONTENT);
        post = postRepository.save(post);

        PostDTO.Update request = new PostDTO.Update(UPDATED_TITLE, UPDATED_CONTENT);

        mockMvc.perform(put("/posts/{id}", post.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(UPDATED_TITLE)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAsAdmin() throws Exception {
        Post post = new Post();
        post.setAuthor(AUTHOR);
        post.setTitle(POST_TITLE);
        post.setContent(CONTENT);
        post = postRepository.save(post);

        mockMvc.perform(delete("/posts/{id}", post.getId()))
                .andExpect(status().isNoContent());

        assertEquals(0, postRepository.findAll().size());
    }
}