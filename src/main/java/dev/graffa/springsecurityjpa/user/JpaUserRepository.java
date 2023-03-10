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

import org.springframework.data.jpa.repository.JpaRepository;
/**
 * <p>
 * Provides a JPA Repository of {@link JpaUser} entities.
 * </p>
 * <p>
 * In order to use the class, it is necessary to include it in the component scan package classes, or extend it within
 * the base application package.
 * </p>
 *
 * <p>
 * See Also:
 * JpaUser, JpaUserService
 * </p>
 *
 * <p><b>Implementation example</b></p>
 *
 * <pre>
 * <code>import org.springframework.security.core.jpa.JpaUserRepository;</code>
 *
 * <code>public interface AuthorityRepository extends JpaUserRepository {</code>
 * <code>}</code>
 *
 * </pre>
 *
 * @author Raffaele Giordanelli
 */
public interface JpaUserRepository extends JpaRepository<JpaUser, String> {

}
