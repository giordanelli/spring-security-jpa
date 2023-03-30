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

package dev.graffa.springsecurityjpa.authority;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.graffa.springsecurityjpa.user.JpaUser;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * <p>
 * Provides persistence for a {@link org.springframework.security.core.GrantedAuthority} in the application
 * {@link javax.sql.DataSource}.
 * </p>
 * <p>
 * In order to use the class, it is necessary to include it in the entity scan package classes, or extend it within
 * the base application package. In the latter case, it is possible to add new fields and save them in the
 * datasource.
 * In order to save new fields of specific implementations, it is also mandatory to extend the
 * {@link dev.graffa.springsecurityjpa.user.JpaUserService} to make it compliant with the new schema.
 * Entities of different implementations of this class will be saved in the Datasource with a different DTYPE, in
 * order to differentiate them from <i>Basic</i> JPA Users.
 * </p>
 *
 * <p>
 * See Also:
 * GrantedAuthority, JpaAuthorityService, JpaAuthorityRepository
 * </p>
 *
 * <p><b>Implementation example</b></p>
 *
 * <pre>
 * <code>import jakarta.persistence.Entity;</code>
 * <code>import org.springframework.security.core.jpa.JpaAuthority;</code>
 *
 * <code>@Entity</code>
 * <code>public class Authority extends JpaAuthority {</code>
 * <code>}</code>
 *
 * </pre>
 *
 * @author Raffaele Giordanelli
 */
@Entity(name = "BasicAuthority")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
@Table(name = "authority")
public class JpaAuthority implements GrantedAuthority {

    @Id
    @Column(length = 50, nullable = false, unique = true)
    protected String authority;
    @ManyToMany(mappedBy = "authorities")
    @JsonBackReference(value = "user-authority")
    protected Collection<JpaUser> users;

}
