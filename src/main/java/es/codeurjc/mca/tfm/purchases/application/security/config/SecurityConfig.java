package es.codeurjc.mca.tfm.purchases.application.security.config;

import es.codeurjc.mca.tfm.purchases.application.security.filters.JwtAuthorizationFilter;
import es.codeurjc.mca.tfm.purchases.application.security.filters.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Rest application security config.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * JWT token provider.
   */
  private JwtTokenProvider jwtTokenProvider;

  /**
   * Constructor.
   *
   * @param jwtTokenProvider JWT token provider.
   */
  public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * CORS configuration source bean.
   *
   * @return CorsConfigurationSource instance.
   */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

  /**
   * Configure application security.
   *
   * @param httpSecurity http security bean.
   * @throws Exception if something is wrong.
   */
  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .cors().and()
        .csrf().disable()
        .authorizeRequests()
        // application urls
        .anyRequest().authenticated().and()
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.jwtTokenProvider));
  }

}
