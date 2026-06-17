package com.realestatebackend.security;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter implements Filter {

    private final com.github.benmanes.caffeine.cache.LoadingCache<String, Bucket> buckets =
            Caffeine.newBuilder()
                    .expireAfterAccess(Duration.ofMinutes(30))
                    .build(this::newBucket);

    private Bucket newBucket(String key) {
        // v8-style builder API (avoids deprecated Refill)
        Bandwidth limit = Bandwidth.builder()
                .capacity(20)                              // bucket size
                .refillGreedy(20, Duration.ofMinutes(1))   // 20 tokens/min
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String key(HttpServletRequest req){
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null) ip = req.getRemoteAddr();
        return req.getRequestURI() + ":" + ip;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;

        if (req.getRequestURI().startsWith("/api/v1/auth")) {
            Bucket b = buckets.get(key(req));
            if (!b.tryConsume(1)) {
                res.setStatus(429);
                res.getWriter().write("Too Many Requests");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
