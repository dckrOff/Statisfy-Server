package uz.dckroff.statisfy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${security.headers.content-security-policy:default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'}")
    private String contentSecurityPolicy;

    @Value("${security.headers.strict-transport-security:max-age=31536000; includeSubDomains}")
    private String strictTransportSecurity;

    @Value("${security.headers.x-content-type-options:nosniff}")
    private String xContentTypeOptions;

    @Value("${security.headers.x-frame-options:DENY}")
    private String xFrameOptions;

    @Value("${security.headers.x-xss-protection:1; mode=block}")
    private String xXssProtection;

    @Value("${security.headers.referrer-policy:no-referrer}")
    private String referrerPolicy;

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**"
    };

    /**
     * Фильтр для добавления безопасных HTTP заголовков
     */
    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                
                // Content Security Policy
                response.setHeader("Content-Security-Policy", contentSecurityPolicy);
                
                // HTTP Strict Transport Security - требует HTTPS
                response.setHeader("Strict-Transport-Security", strictTransportSecurity);
                
                // X-Content-Type-Options - предотвращает MIME sniffing
                response.setHeader("X-Content-Type-Options", xContentTypeOptions);
                
                // X-Frame-Options - защита от clickjacking
                response.setHeader("X-Frame-Options", xFrameOptions);
                
                // X-XSS-Protection - защита от XSS атак
                response.setHeader("X-XSS-Protection", xXssProtection);
                
                // Referrer-Policy - контролирует передачу referer header
                response.setHeader("Referrer-Policy", referrerPolicy);
                
                // Cache-Control - отключаем кэширование для чувствительных страниц
                if (request.getRequestURI().startsWith("/api/auth") || 
                    request.getRequestURI().startsWith("/api/user") ||
                    request.getRequestURI().startsWith("/api/admin")) {
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }
                
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(SWAGGER_WHITELIST)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN"))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
} 