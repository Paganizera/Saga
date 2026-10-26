package saga.dto;

import jakarta.validation.constraints.NotBlank;

public final class LoginDTO {
    public record Request(@NotBlank String username, @NotBlank String password) {
    }

    public record Response(String token) {
    }
}
