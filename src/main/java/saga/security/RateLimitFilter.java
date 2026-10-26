package saga.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private static final long WINDOW_MS = 60_000;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();
    private final int commentLimit;
    private final int loginLimit;

    public RateLimitFilter(
            @Value("${ratelimit.comments-per-minute:5}") int commentLimit,
            @Value("${ratelimit.logins-per-minute:10}") int loginLimit) {
        this.commentLimit = commentLimit;
        this.loginLimit = loginLimit;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {
        String bucket = bucketFor(request);
        if (bucket != null) {
            int limit = bucket.equals("login") ? loginLimit : commentLimit;
            if (!allow(bucket + ":" + request.getRemoteAddr(), limit)) {
                response.setStatus(429);
                response.setHeader("Retry-After", "60");
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Too many requests. Try again in a minute.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String bucketFor(HttpServletRequest req) {
        if (!"POST".equals(req.getMethod())) return null;
        String path = req.getRequestURI();
        if (path.equals("/auth/login")) return "login";
        if (MATCHER.match("/posts/*/commentaries", path)) return "comment";
        return null;
    }

    private boolean allow(String key, int limit) {
        long now = System.currentTimeMillis();
        if (hits.size() > 10_000) hits.clear();
        Deque<Long> q = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) {
            while (!q.isEmpty() && now - q.peekFirst() > WINDOW_MS) q.pollFirst();
            if (q.size() >= limit) return false;
            q.addLast(now);
            return true;
        }
    }
}
