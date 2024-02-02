package BCSD.MusicStream.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/member/sign-up").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/member/sign-in").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll()
                .requestMatchers("/member/email-exists/{userEmail}").permitAll()
                .requestMatchers("/member").hasAnyAuthority("Admin", "User")
                .requestMatchers("/music").hasAnyAuthority("Admin", "User")
                .requestMatchers("/music/like").hasAnyAuthority("Admin", "User")
                .requestMatchers("/music/dislike").hasAnyAuthority("Admin", "User")
                .requestMatchers("/music/upload").hasAnyAuthority("Admin")
                .requestMatchers("/music/modify").hasAnyAuthority("Admin")
                .requestMatchers("/music/delete").hasAnyAuthority("Admin")
                .requestMatchers("/playlist").permitAll()
                .requestMatchers("/playlistMusics").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
