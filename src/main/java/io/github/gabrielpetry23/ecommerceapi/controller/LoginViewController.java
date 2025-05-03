package io.github.gabrielpetry23.ecommerceapi.controller;

import io.github.gabrielpetry23.ecommerceapi.security.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String paginaLogin() {
        return "login";
    }

    @GetMapping
    @ResponseBody
    public String paginaHome(Authentication authentication) {

        if (authentication instanceof CustomAuthentication customAuth) {
            System.out.println(customAuth.getUser());
        }
        return "Hello " + authentication.getName() + ", you are logged in!";
    }

    @GetMapping("/authorized")
    @ResponseBody
    public String getAuthorizationCode(@RequestParam("code") String code) {
        return "Authorization code: " + code;
    }
}
