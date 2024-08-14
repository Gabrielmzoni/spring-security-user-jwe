package br.com.gmsoft.userjwe.security;


import br.com.gmsoft.userjwe.service.exception.InvalidJweException;
import br.com.gmsoft.userjwe.service.exception.JweExpiredException;
import br.com.gmsoft.userjwe.service.JwtService;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    Gson gson = new Gson();

    public AuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {
        try {
            String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (jws != null) {
                var user = jwtService.getAuthUser(request);
                var loggedUserDetails = gson.fromJson(user, LoggedUserDetails.class);
                Authentication authentication = new JwtAuthentication(loggedUserDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }


            filterChain.doFilter(request, response);

        } catch (JweExpiredException | InvalidJweException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            var errorJson = String.format("{\"error\": \"%s\"}", e.getMessage());
            response.getWriter().write(errorJson);
        }
    }
}