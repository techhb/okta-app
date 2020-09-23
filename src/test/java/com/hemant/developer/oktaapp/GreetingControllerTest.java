package com.hemant.developer.oktaapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes={GreetingController.class})
public class GreetingControllerTest {

    private final static String ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
            ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsIm" +
            "p0aSI6ImQzNWRmMTRkLTA5ZjYtNDhmZi04YTkzLTdjNmYwMzM5MzE1OSIsImlhdCI6MTU0M" +
            "Tk3MTU4MywiZXhwIjoxNTQxOTc1MTgzfQ.QaQOarmV8xEUYV7yvWzX3cUE_4W1luMcWCwpr" +
            "oqqUrg";


    @Autowired
    private MockMvc mvc;

    @Test
    void testGreet() throws Exception {
        OidcIdToken idToken = createOidcToken();
        this.mvc.perform(get("/greeting")
                .with(authentication(createMockOAuth2AuthenticationToken(idToken))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("Hello user@email.com"));
    }

    private OAuth2AuthenticationToken createMockOAuth2AuthenticationToken(OidcIdToken idToken) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        OidcUser user = new DefaultOidcUser(authorities, idToken);

        return new OAuth2AuthenticationToken(user, authorities, "oidc");
    }


    private OidcIdToken createOidcToken(){
        Map<String, Object> claims = new HashMap<>();
        claims.put("groups", "ROLE_USER");
        claims.put("email", "user@email.com");
        claims.put("sub", 123);
        OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(),
                Instant.now().plusSeconds(60), claims);
        return idToken;
    }
}
