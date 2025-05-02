package io.github.gabrielpetry23.ecommerceapi.security;

import io.github.gabrielpetry23.ecommerceapi.model.User;
import io.github.gabrielpetry23.ecommerceapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String senhaDigitada = authentication.getCredentials().toString();

        User userFound = userService.findByEmail(login);

        if (userFound == null) {
            throw new UsernameNotFoundException("User or password incorrect!");
        }

        String encryptedPassword = userFound.getPassword();

        boolean passwordMatches = passwordEncoder.matches(senhaDigitada, encryptedPassword);

        if (passwordMatches) {
            return new CustomAuthentication(userFound);
        }

        throw new UsernameNotFoundException("User or password incorrect!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
