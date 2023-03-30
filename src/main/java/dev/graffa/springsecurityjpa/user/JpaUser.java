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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.graffa.springsecurityjpa.authority.JpaAuthority;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


/**
 * <p>Provides core user information, as outlined in {@link org.springframework.security.core.userdetails.User}.</p>
 * <p>
 * In order to use the class, it is necessary to include it in the entity scan package classes, or extend it within
 * the base application package. In the latter case, it is possible to add new fields and save them in the datasource.
 * In order to save new fields of specific implementations, it is also mandatory to extend the
 * {@link JpaUserService} to make it compliant with the new schema.
 * Entities of different implementations of this class will be saved in the Datasource with a different DTYPE, in
 * order to differentiate them from <i>Basic</i> JPA Users.
 * </p>
 *
 * <p>
 * See Also:
 * UserDetails, JpaUserService, JpaUserRepository
 * </p>
 *
 * <p><b>Implementation example</b></p>
 *
 * <pre>
 * <code>import jakarta.persistence.Column;</code>
 * <code>import jakarta.persistence.Entity;</code>
 * <code>import lombok.*;</code>
 * <code>import lombok.experimental.SuperBuilder;</code>
 * <code>import org.springframework.security.core.jpa.JpaUser;</code>
 *
 * <code>@Entity</code>
 * <code>@Getter</code>
 * <code>@Setter</code>
 * <code>@ToString</code>
 * <code>@SuperBuilder</code>
 * <code>@NoArgsConstructor</code>
 * <code>@AllArgsConstructor</code>
 * <code>public class User extends JpaUser {</code>
 * <code>    @Column(length = 60)</code>
 * <code>    protected String email;</code>
 * <code>}</code>
 * </pre>
 *
 * @author Raffaele Giordanelli
 */
@Entity(name = "BasicUser")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor

@ToString
// User is reserved to SQL
@Table(name = "users")
public class JpaUser implements UserDetails {
    @Id
    @NonNull
    @Column(length = 20, nullable = false, unique = true)
    protected String username;

    @Column(length = 60, nullable = false)
    @NonNull
    protected String password;
    @Builder.Default
    protected boolean enabled = true;
    @Builder.Default
    protected boolean credentialsNonExpired = true;
    @Builder.Default
    protected boolean accountNonLocked = true;
    @Builder.Default
    protected boolean accountNonExpired = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    protected Collection<JpaAuthority> authorities = List.of();

}
