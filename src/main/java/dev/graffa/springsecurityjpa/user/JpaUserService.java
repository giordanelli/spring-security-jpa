/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.graffa.springsecurityjpa.user;

import dev.graffa.springsecurityjpa.authority.JpaAuthority;
import dev.graffa.springsecurityjpa.authority.JpaAuthorityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>Provides a {@link UserDetailsManager} implementation, based on JPA Persistence.</p>
 * <p>
 * In order to use the class, it is necessary to include it in the entity scan package classes, or extend it within
 * the base application package. In the latter case, it is possible to add new fields and save them in the datasource.
 * In order to save new fields of specific implementations, it is possible to extend this class to make it compliant
 * with the new schema. In that case, it would be necessary to override <b>createUser</b> and <b>updateUser</b> methods.
 * Entities of different implementations of this class will be saved in the Datasource with a different DTYPE, in
 * order to differentiate them from <i>Basic</i> JPA Users.
 * </p>
 *
 * <p>It also exposes a {@link AuthenticationManager} Bean that uses this service to authenticate users.</p>
 *
 * <p>
 * See Also:
 * UserDetailsManager, JpaUser, JpaUserRepository
 * </p>
 *
 * <p><b>Implementation example</b></p>
 *
 * <pre>
 *
 * <code>@Service</code>
 * <code>public class UserService extends JpaUserService {</code>
 *
 * <code>    public UserService(JpaUserRepository userRepository, JpaAuthorityRepository authorityRepository,</code>
 * <code>                       PasswordEncoder passwordEncoder) {</code>
 * <code>         super(userRepository, authorityRepository, passwordEncoder);</code>
 * <code>     }</code>
 *
 * <code>     @Override</code>
 * <code>     public void createUser(UserDetails userdetails) {</code>
 * <code>         if (userdetails.getClass().equals(JpaUser.class)) {</code>
 * <code>             super.createUser(userdetails);</code>
 * <code>         } else if (userdetails.getClass().equals(User.class)) {</code>
 * <code>             User castedUser = (User) userdetails;</code>
 * <code>             String username = castedUser.getUsername();</code>
 * <code>             if (userExists(username))</code>
 * <code>                 throw new IllegalArgumentException("Username " + username + " already present");</code>
 *
 * <code>             User newUser = userRepository.save(</code>
 * <code>                     User.builder().username(username).password(passwordEncoder.encode(castedUser.getPassword()))</code>
 * <code>                             .enabled(castedUser.isEnabled()).accountNonExpired(castedUser.isAccountNonExpired())</code>
 * <code>                             .credentialsNonExpired(castedUser.isCredentialsNonExpired()).email(castedUser.getEmail())</code>
 * <code>                             .build());</code>
 * <code>             List&lt;JpaAuthority&gt; authorities = castedUser.getAuthorities().stream()</code>
 * <code>                     .map(auth -> authorityRepository.findById(auth.getAuthority())</code>
 * <code>                             .orElse(authorityRepository.save(JpaAuthority.builder().authority(auth.getAuthority()).build()))</code>
 * <code>                     ).toList();</code>
 * <code>             newUser.setAuthorities(authorities);</code>
 * <code>             userRepository.save(newUser);</code>
 * <code>         } else throw new IllegalArgumentException("Unsupported class " + userdetails.getClass().getName());</code>
 * <code>     }</code>
 * <code> }</code>
 * </pre>
 *
 * @author Raffaele Giordanelli
 */
@Service
public class JpaUserService implements UserDetailsManager {
    protected final JpaUserRepository userRepository;
    protected final JpaAuthorityRepository authorityRepository;
    protected final PasswordEncoder passwordEncoder;

    public JpaUserService(JpaUserRepository userRepository, JpaAuthorityRepository authorityRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Override
    public void createUser(UserDetails userdetails) {
        String username = userdetails.getUsername();
        if (userExists(username))
            throw new IllegalArgumentException("Username " + username + " already present");

        JpaUser jpaUser = userRepository.save(
                JpaUser.builder().username(username).password(passwordEncoder.encode(userdetails.getPassword()))
                        .enabled(userdetails.isEnabled()).accountNonExpired(userdetails.isAccountNonExpired())
                        .credentialsNonExpired(userdetails.isCredentialsNonExpired())/*.authorities(authorities)*/
                        .build());
        List<JpaAuthority> authorities = userdetails.getAuthorities().stream()
                .map(auth -> authorityRepository.findById(auth.getAuthority())
                        .orElse(authorityRepository.save(JpaAuthority.builder().authority(auth.getAuthority()).build()))
                ).toList();
        jpaUser.setAuthorities(authorities);
        userRepository.save(jpaUser);
    }

    @Override
    public void updateUser(UserDetails user) {
        Optional<JpaUser> optionalJpaUser = userRepository.findById(user.getUsername());
        if (optionalJpaUser.isEmpty()) throw new UserNotFoundException(user.getUsername());
        JpaUser jpaUser = optionalJpaUser.get();
        jpaUser.setEnabled(user.isEnabled());
        jpaUser.setPassword(passwordEncoder.encode(user.getPassword()));
        jpaUser.setAccountNonExpired(user.isAccountNonExpired());
        jpaUser.setCredentialsNonExpired(user.isCredentialsNonExpired());
        jpaUser.setAccountNonLocked(user.isAccountNonLocked());

        List<JpaAuthority> authorities = user.getAuthorities().stream()
                .map(auth -> authorityRepository.findById(auth.getAuthority())
                        .orElse(authorityRepository.save(JpaAuthority.builder().authority(auth.getAuthority()).build()))).toList();
        jpaUser.setAuthorities(authorities);
        userRepository.save(jpaUser);
    }

    @Override
    public void deleteUser(String username) {
        Optional<JpaUser> jpaUser = userRepository.findById(username);
        if (jpaUser.isEmpty()) throw new UserNotFoundException(username);
        userRepository.delete(jpaUser.get());
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context for current user.");
        }
        String username = currentUser.getName();

        authenticationManager()
                .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, oldPassword));

        Optional<JpaUser> optionalJpaUser = userRepository.findById(username);
        if (optionalJpaUser.isEmpty()) throw new UserNotFoundException(username);
        JpaUser jpaUser = optionalJpaUser.get();
        jpaUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(jpaUser);
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findById(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<JpaUser> userDetails = userRepository.findById(username);
        if (userDetails.isEmpty()) throw new UserNotFoundException(username);
        return userDetails.get();
    }
}
