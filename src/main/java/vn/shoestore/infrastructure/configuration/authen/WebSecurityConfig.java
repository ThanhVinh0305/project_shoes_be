package vn.shoestore.infrastructure.configuration.authen;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import vn.shoestore.domain.service.UserService;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
  private final UserService userService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
      .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
      .cors(
        cors ->
          cors.configurationSource(
            request -> {
              CorsConfiguration configuration = new CorsConfiguration();
              configuration.setAllowedMethods(List.of("*"));
              configuration.setAllowedHeaders(List.of("*"));
              configuration.setAllowedOrigins(
                List.of("http://localhost:4200", "http://localhost:*"));
              configuration.setAllowCredentials(true);
              return configuration;
            }))
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(
          (authorize) ->
                authorize
                  // Cho phép tracking behavior không cần xác thực cho cả v1 và v2
                  .requestMatchers("/api/v1/behavior/**").permitAll()
                  .requestMatchers("/v2/behavior/**").permitAll()
                  .requestMatchers("/v2/user-behaviors/**").permitAll()
                  // .requestMatchers("/v2/user-behaviors/**").permitAll() // Bỏ permitAll, yêu cầu xác thực
                  .requestMatchers("/api/v1/**")
                  .authenticated()
                  .requestMatchers("/api/**")
                  .authenticated()
                  .requestMatchers("/open-api/**")
                  .permitAll()
                  .requestMatchers("/auth/**")
                  .permitAll()
                  .requestMatchers("/swagger-ui/**")
                  .permitAll()
                  .requestMatchers("/v3/api-docs/**")
                  .permitAll()
                  .requestMatchers("/actuator/**")
                  .permitAll()
      )
      .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
    }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public JwtAuthFilter jwtAuthenticationFilter() {
    return new JwtAuthFilter();
  }
}
