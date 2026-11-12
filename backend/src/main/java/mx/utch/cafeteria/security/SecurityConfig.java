package mx.utch.cafeteria.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.utch.cafeteria.dto.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Control de acceso por rol (RNF1). Cada regla corresponde a una fila de la tabla
 * de endpoints del Ejercicio 1 (seccion 4.3).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(JwtService jwtService, UserDetailsService userDetailsService,
                          ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                       // API stateless con JWT
            .cors(Customizer.withDefaults())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))  // permite la consola H2
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Publico
                .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/auth/login")).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(antMatcher("/error")).permitAll()
                // Productos
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/productos/**")).authenticated()
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/productos/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PUT, "/api/productos/**")).hasRole("ADMIN")
                .requestMatchers(antMatcher(HttpMethod.PATCH, "/api/productos/**")).hasRole("ADMIN")
                // Pedidos
                .requestMatchers(antMatcher(HttpMethod.POST, "/api/pedidos")).hasRole("CLIENTE")
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/pedidos/mis-pedidos")).hasRole("CLIENTE")
                .requestMatchers(antMatcher(HttpMethod.PATCH, "/api/pedidos/*/estado")).hasRole("PERSONAL")
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/pedidos/*")).hasAnyRole("CLIENTE", "PERSONAL")
                .requestMatchers(antMatcher(HttpMethod.GET, "/api/pedidos")).hasRole("PERSONAL")
                // Todo lo demas requiere sesion
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) ->
                        escribirError(req, res, 401, "Unauthorized", "Token ausente, invalido o expirado"))
                .accessDeniedHandler((req, res, e) ->
                        escribirError(req, res, 403, "Forbidden", "Tu rol no tiene permiso para esta operacion")))
            .addFilterBefore(new JwtAuthFilter(jwtService, userDetailsService),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(allowedOrigin));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    private void escribirError(HttpServletRequest req, HttpServletResponse res, int status,
                               String error, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(res.getOutputStream(),
                ErrorResponse.of(status, error, mensaje, req.getRequestURI()));
    }
}
