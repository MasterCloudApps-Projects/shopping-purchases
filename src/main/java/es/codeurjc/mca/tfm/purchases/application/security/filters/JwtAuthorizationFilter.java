package es.codeurjc.mca.tfm.purchases.application.security.filters;

import io.jsonwebtoken.JwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * JWT authorization filter.
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private static final String NO_TOKEN_PROVIDED_MSG = "{\n\t\"error\": \"No token provided.\"\n}";

  private static final String INVALID_TOKEN_MSG =
      "{\n\t\"error\": \"Invalid or expired token.\"\n}";


  /**
   * JWT token provider.
   */
  private JwtTokenProvider jwtTokenProvider;

  /**
   * Constructor.
   *
   * @param authManager      authentication manager.
   * @param jwtTokenProvider JWT token provider.
   */
  public JwtAuthorizationFilter(AuthenticationManager authManager,
      JwtTokenProvider jwtTokenProvider) {
    super(authManager);
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Filter method to check JWT token.
   *
   * @param req   http request.
   * @param res   http response.
   * @param chain filter chain.
   * @throws IOException      if an input/output error happens.
   * @throws ServletException when the servlet needs to throw an exception.l
   */
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain)
      throws IOException, ServletException {
    String token = this.jwtTokenProvider.getToken(req);

    if (token == null) {
      SecurityContextHolder.clearContext();
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      res.setContentType(MediaType.APPLICATION_JSON_VALUE);
      res.getWriter().write(NO_TOKEN_PROVIDED_MSG);
      res.getWriter().flush();
      return;
    }

    try {
      UsernamePasswordAuthenticationToken authentication = this.jwtTokenProvider.getAuthentication(
          token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (JwtException ex) {
      SecurityContextHolder.clearContext();
      res.setStatus(HttpServletResponse.SC_FORBIDDEN);
      res.setContentType(MediaType.APPLICATION_JSON_VALUE);
      res.getWriter().write(INVALID_TOKEN_MSG);
      res.getWriter().flush();
      return;
    }

    chain.doFilter(req, res);
  }

}
