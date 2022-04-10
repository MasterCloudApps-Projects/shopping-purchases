package es.codeurjc.mca.tfm.purchases.application.security.filters;

import io.jsonwebtoken.Jwts;
import java.util.ArrayList;
import java.util.Base64;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * JWT token provider.
 */
@Component
public class JwtTokenProvider {

  private static final String HEADER_AUTHORIZATION_KEY = "Authorization";

  private static final String TOKEN_BEARER_PREFIX = "Bearer ";

  /**
   * Secret key to validate signature.
   */
  @Value("${security.jwt.token.secret-key}")
  private String secretKey;

  /**
   * Post construct method to encode secret key in base64.
   */
  @PostConstruct
  protected void init() {
    this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
  }


  /**
   * Get token from request.
   *
   * @param req request from which get the token.
   * @return token.
   */
  public String getToken(HttpServletRequest req) {
    String token = null;
    String authorizationHeader = req.getHeader(HEADER_AUTHORIZATION_KEY);
    if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_BEARER_PREFIX)) {
      token = authorizationHeader.replace(TOKEN_BEARER_PREFIX, "");
    }
    return token;
  }


  /**
   * Get authentication from token.
   *
   * @param token with user details to authenticate.
   * @return UsernamePasswordAuthenticationToken instance.
   */
  public UsernamePasswordAuthenticationToken getAuthentication(String token) {
    UsernamePasswordAuthenticationToken authentication = null;
    String user = String.valueOf(Jwts.parser()
        .setSigningKey(this.secretKey)
        .parseClaimsJws(token)
        .getBody()
        .get("id"));

    if (user != null) {
      authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
    return authentication;
  }

}
