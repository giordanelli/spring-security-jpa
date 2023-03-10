package dev.graffa.springsecurityjpa;

import dev.graffa.springsecurityjpa.authority.AuthorityNotFoundException;
import dev.graffa.springsecurityjpa.authority.JpaAuthority;
import dev.graffa.springsecurityjpa.authority.JpaAuthorityService;
import dev.graffa.springsecurityjpa.authority.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class AuthorityServiceTest {
    @Autowired
    protected JpaAuthorityService authorityService;


    @Test
    void assertFailDuplicateAuthorityCreation() {
        authorityService.createAuthority(Role.ADMIN.name);
        assertThrows(IllegalArgumentException.class, () -> authorityService.createAuthority(Role.ADMIN.name));
        authorityService.deleteAuthority(Role.ADMIN.name);
    }

    @Test
    void assertFailMissingAuthorityName() {
        assertThrows(IllegalArgumentException.class, () -> authorityService.createAuthority(null));
        assertThrows(IllegalArgumentException.class, () -> authorityService.createAuthority(""));
    }


    @Test
    void assertFailOnDeleteNotExistingAuthority() {
        assertThrows(AuthorityNotFoundException.class, () -> authorityService.deleteAuthority("randomAuthority"));
    }

    @Test
    void assertUpdateUser() {
        String authority = "randomAuthority", newAuthorityName = "changedRandomAuthority";

        JpaAuthority jpaAuthority = authorityService.createAuthority(authority);
        jpaAuthority.setAuthority(newAuthorityName);
        authorityService.updateAuthority(authority, jpaAuthority);

        jpaAuthority = authorityService.getByName(newAuthorityName);

        assertNotNull(jpaAuthority);

        authorityService.deleteAuthority(jpaAuthority.getAuthority());
    }

}
