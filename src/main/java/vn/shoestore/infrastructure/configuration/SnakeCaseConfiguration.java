package vn.shoestore.infrastructure.configuration;

import com.google.common.base.CaseFormat;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableAspectJAutoProxy
public class SnakeCaseConfiguration {
  @Bean
  public Filter snakeConverter() {
    return new OncePerRequestFilter() {

      @Override
      protected void doFilterInternal(
          HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
        final Map<String, String[]> formattedParams = new ConcurrentHashMap<>();

        for (String param : request.getParameterMap().keySet()) {
          String formattedParam = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, param);
          formattedParams.put(formattedParam, request.getParameterValues(param));
        }

        filterChain.doFilter(
            new HttpServletRequestWrapper(request) {
              @Override
              public String getParameter(String name) {
                String[] names = formattedParams.get(name);
                return names != null ? names[0] : null;
              }

              @Override
              public Enumeration<String> getParameterNames() {
                return Collections.enumeration(formattedParams.keySet());
              }

              @Override
              public String[] getParameterValues(String name) {
                return formattedParams.get(name);
              }

              @Override
              public Map<String, String[]> getParameterMap() {
                return formattedParams;
              }
            },
            response);
      }
    };
  }
}
