package dev.graffa.springsecurityjpa;

import dev.graffa.springsecurityjpa.authority.JpaAuthority;
import dev.graffa.springsecurityjpa.authority.Role;
import dev.graffa.springsecurityjpa.user.JpaUser;
import dev.graffa.springsecurityjpa.user.JpaUserService;
import dev.graffa.springsecurityjpa.user.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class UserDetailsServiceTest {
    @Autowired
    protected JpaUserService userDetailsService;
    @Autowired
    protected AuthenticationManager authenticationManager;


    @Test
    void assertFailDuplicateUserCreation() {
        userDetailsService.createUser(JpaUser.builder().username("admin").password("pwd").build());
        assertThrows(IllegalArgumentException.class,
                () -> userDetailsService.createUser(JpaUser.builder().username("admin").password("pwd").build()));
        userDetailsService.deleteUser("admin");
    }

    @Test
    void assertFailMissingPasswordCreation() {
        assertThrows(NullPointerException.class,
                () -> userDetailsService.createUser(JpaUser.builder().username("randomUser").build()));
    }

    @Test
    void assertCreateDeleteUser() {
        userDetailsService.createUser(JpaUser.builder().username("randomUser").password("pwd").build());
        userDetailsService.deleteUser("randomUser");
    }

    @Test
    void assertFailOnDeleteNotExistingUser() {
        assertThrows(UserNotFoundException.class, () -> userDetailsService.deleteUser("randomUser"));
    }

    @Test
    void assertUpdateUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = "randomUser", pwd = "pwd";

        userDetailsService.createUser(JpaUser.builder().username(username).password(pwd).build());
        JpaUser userDetails = (JpaUser) userDetailsService.loadUserByUsername(username);
        userDetails.setEnabled(false);


        userDetails.getAuthorities().add(JpaAuthority.builder().authority(Role.ADMIN.name).build());
        userDetailsService.updateUser(userDetails);

        assertThrows(DisabledException.class, () -> context.setAuthentication(authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, pwd))));

        userDetails.setEnabled(true);
        userDetails.setAccountNonLocked(false);
        userDetailsService.updateUser(userDetails);

        assertThrows(LockedException.class, () -> context.setAuthentication(authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, pwd))));

        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(false);
        userDetails.getAuthorities().add(JpaAuthority.builder().authority(Role.USER.name).build());
        userDetailsService.updateUser(userDetails);

        assertThrows(BadCredentialsException.class, () -> context.setAuthentication(authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, pwd))));

        userDetailsService.deleteUser(username);
    }

    @Test
    void assertChangePassword() {
        SecurityContext context = SecurityContextHolder.getContext();
        String username = "randomUser", oldPassword = "pwd", newPassword = "pwd2";
        userDetailsService.createUser(JpaUser.builder().username(username).password(oldPassword).build());

        context.setAuthentication(authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword)));

        assertThrows(BadCredentialsException.class, () -> userDetailsService.changePassword(newPassword, newPassword));
        userDetailsService.changePassword(oldPassword, newPassword);

        context.setAuthentication(authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, newPassword)));

        assertThrows(AuthenticationException.class, () -> authenticationManager
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword)));

        userDetailsService.deleteUser(username);
    }

}
