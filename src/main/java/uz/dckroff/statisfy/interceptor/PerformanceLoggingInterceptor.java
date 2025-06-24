package uz.dckroff.statisfy.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;

@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceLoggingInterceptor implements HandlerInterceptor {

    @Value("${performance.logging.slow-request-threshold-ms:200}")
    private long slowRequestThresholdMs;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Ничего не делаем здесь, все измерения будут сделаны в afterCompletion
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = method.getName();
            String uri = request.getRequestURI();
            String httpMethod = request.getMethod();

            // Логируем все запросы с уровнем DEBUG
            log.debug("Request execution time: {} ms, URI: {}, Method: {}, Controller: {}.{}",
                    executionTime, uri, httpMethod, controllerName, methodName);

            // Если запрос был медленным, логируем с уровнем WARN
            if (executionTime > slowRequestThresholdMs) {
                log.warn("SLOW REQUEST: {} ms, URI: {}, Method: {}, Controller: {}.{}",
                        executionTime, uri, httpMethod, controllerName, methodName);
            }
        }
    }
} 