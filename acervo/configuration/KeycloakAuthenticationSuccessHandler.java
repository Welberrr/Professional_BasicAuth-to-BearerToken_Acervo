package br.jus.stf.acervo.configuration.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KeycloakAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${client-id}")
    private String clientId;

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String usuarioAutenticado;

    public KeycloakAuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

//    @SuppressWarnings("unchecked")
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        var persistedAuth = (OAuth2AuthenticationToken) authentication;

        var user = new DefaultOAuth2User(persistedAuth.getAuthorities(), persistedAuth.getPrincipal().getAttributes(), "name");

        usuarioAutenticado = user.getName();

        OAuth2AccessToken accessToken = this.authorizedClientService.loadAuthorizedClient(persistedAuth.getAuthorizedClientRegistrationId(), persistedAuth.getName()).getAccessToken();

        SecurityContextHolder.getContext()
                .setAuthentication(new OAuth2AuthenticationToken(persistedAuth.getPrincipal(), mapAuthorities(accessToken.getTokenValue()), persistedAuth
                        .getAuthorizedClientRegistrationId()));

        var redirectUri = requestCache.getRequest(request, response).getRedirectUrl();

        redirectStrategy.sendRedirect(request, response, redirectUri);
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(String accessToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(accessToken);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            Map<String, Object> claims = claimsSet.getClaims();

            var resourceRoles = new ArrayList<>();
            var resourceAccess = (Map<String, List<String>>) claims.get("resource_access");
            if (resourceAccess.containsKey(clientId)) {
                resourceRoles.addAll(((Map<String, List<String>>) resourceAccess.get(clientId)).get("roles"));
            }

            return resourceRoles.isEmpty() ? Collections.emptySet() : resourceRoles.stream().map(r -> new SimpleGrantedAuthority(String.valueOf(r))).collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public String getUsuarioAutenticado() { return this.usuarioAutenticado; }

}