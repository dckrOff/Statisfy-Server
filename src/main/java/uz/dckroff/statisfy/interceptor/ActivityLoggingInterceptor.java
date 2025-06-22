package uz.dckroff.statisfy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.service.AnalyticsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityLoggingInterceptor implements HandlerInterceptor {

    private final AnalyticsService analyticsService;
    
    private static final String[] EXCLUDED_PATHS = {
        "/api/actuator",
        "/api/analytics",
        "/api/error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (shouldLog(request, handler)) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = null;
                
                if (authentication != null && authentication.getPrincipal() instanceof User) {
                    user = (User) authentication.getPrincipal();
                }
                
                String activityType = "API_" + request.getMethod();
                String path = request.getRequestURI();
                
                analyticsService.logActivity(
                    user,
                    activityType,
                    "API_ENDPOINT",
                    null,
                    "Access to " + path,
                    request
                );
                
            } catch (Exception e) {
                log.error("Error logging activity: {}", e.getMessage());
                // Don't block the request if logging fails
            }
        }
        
        return true;
    }
    
    private boolean shouldLog(HttpServletRequest request, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }
        
        String path = request.getRequestURI();
        
        // Skip excluded paths
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                return false;
            }
        }
        
        // Skip OPTIONS requests (CORS preflight)
        if ("OPTIONS".equals(request.getMethod())) {
            return false;
        }
        
        return true;
    }
} 