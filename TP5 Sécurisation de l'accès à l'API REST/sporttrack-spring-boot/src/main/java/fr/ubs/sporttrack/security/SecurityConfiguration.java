package fr.ubs.sporttrack.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Value("${security.user.username}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    /**
     * Configure un utilisateur en mémoire avec identifiant/mot de passe stockés dans application.properties.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User
                .withUsername(username)
                .password(password)
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Configure les autorisations sur les URL et active HTTP Basic.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/openapi/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Public pour Swagger
                        .requestMatchers(HttpMethod.GET, "/activities/**").authenticated() // GET authentifié
                        .requestMatchers(HttpMethod.POST, "/activities/").authenticated() // POST global
                        .requestMatchers(HttpMethod.POST, "/activities/{description}").authenticated() // Ajouté pour POST sur {description}
                        .requestMatchers(HttpMethod.DELETE, "/activities/**").authenticated() // DELETE autorisé
                        .anyRequest().denyAll() // Bloquer les autres
                )
                .httpBasic()
                .and()
                .csrf().disable(); // Désactiver CSRF pour simplifier les tests

        return http.build();
    }

    /**
     * Ajoute un encodeur de mots de passe BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure Swagger avec une authentification HTTP Basic.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}