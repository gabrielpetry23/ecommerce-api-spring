package io.github.gabrielpetry23.ecommerceapi.security;

import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.security.CustomAuthentication;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginSocialSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String DEFAULT_PASSWORD = "123";

    private final PasswordEncoder encoder;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = auth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        User user = userService.findByEmail(email);
        String nome = oAuth2User.getAttribute("name");

        if (user == null) {
            user = registerUser(email);
        }

        authentication = new CustomAuthentication(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println(authentication);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User registerUser(String email) {
        User user = new User();
        user.setName(findNameByEmail(email));
        user.setEmail(email);
        user.setPassword(encoder.encode(DEFAULT_PASSWORD));
        user.setRole("USER");

        userService.save(user);
        return user;
    }

    private String findNameByEmail(String email) {
        String nome = email.substring(0, email.indexOf("@"));
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }
}

