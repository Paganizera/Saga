package saga.dto;

public final class LoginDTO {
    public record Request(String username, String password) {
    }

    public record Response(String token) {
    }
}
