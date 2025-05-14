package security.jwt;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class TestJwtUtils extends JwtUtils {

    @Override
    public String generateJwtToken(Authentication authentication) {
        return "test-token";
    }

    @Override
    public String getUsernameFromJwtToken(String token) {
        return "test-user";
    }

    @Override
    public boolean validateJwtToken(String authToken) {
        return true;
    }
} 