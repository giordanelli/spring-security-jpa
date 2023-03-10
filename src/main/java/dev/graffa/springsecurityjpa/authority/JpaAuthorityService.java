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

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>Provides an Authority Service, based on JPA Persistence.</p>
 * <p>
 * In order to use the class, it is necessary to include it in the entity scan package classes, or extend it within
 * the base application package. In the latter case, it is possible to add new fields and save them in the datasource.
 * In order to save new fields of specific implementations, it is possible to extend this class to make it compliant
 * with the new schema. In that case, it would be necessary to override <b>createAuthority</b> and
 * <b>updateAuthority</b> methods.
 * Entities of different implementations of this class will be saved in the Datasource with a different DTYPE, in
 * order to differentiate them from <i>Basic</i> JPA Authorities.
 * </p>
 *
 * <p>
 * See Also:
 * JpaAuthority, JpaAuthorityRepository
 * </p>
 *
 * <p><b>Implementation example</b></p>
 *
 * <pre>
 *
 * <code>@Service</code>
 * <code>public class AuthorityService extends JpaAuthorityService {</code>
 * <code>    public AuthorityService(JpaAuthorityRepository authorityRepository) {</code>
 * <code>        super(authorityRepository);</code>
 * <code>    }</code>
 * <code>}</code>
 * </pre>
 *
 * @author Raffaele Giordanelli
 */
@Service
public class JpaAuthorityService {
    protected final JpaAuthorityRepository authorityRepository;

    public JpaAuthorityService(JpaAuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public JpaAuthority createAuthority(String authority) {
        JpaAuthority jpaAuthority = JpaAuthority.builder().authority(authority).build();
        Optional<JpaAuthority> optionalAuthority = authorityRepository.findById(jpaAuthority.getAuthority());
        if (optionalAuthority.isPresent()) throw new IllegalArgumentException("Authority already present");
        return authorityRepository.save(jpaAuthority);
    }

    public void deleteAuthority(String authority) {
        authorityRepository.deleteById(authority);
    }

    public void updateAuthority(String name, JpaAuthority authority) {
        Optional<JpaAuthority> optionalAuthority = authorityRepository.findById(name);
        if (optionalAuthority.isEmpty()) throw new AuthorityNotFoundException(name);
        JpaAuthority toUpdate = optionalAuthority.get();
        toUpdate.setAuthority(authority.getAuthority());
        toUpdate.setUsers(authority.getUsers());
        authorityRepository.save(toUpdate);
    }

    public JpaAuthority getByName(String authority) {
        Optional<JpaAuthority> optionalJpaAuthority = authorityRepository.findById(authority);
        if (optionalJpaAuthority.isPresent())
            return optionalJpaAuthority.get();
        return null;
    }

}
