/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.authserver.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ixortalk.authserver.config.Constants;
import com.ixortalk.authserver.domain.Authority;
import com.ixortalk.authserver.domain.User;
import com.ixortalk.authserver.repository.AuthorityRepository;
import com.ixortalk.authserver.repository.UserRepository;
import com.ixortalk.authserver.security.AuthoritiesConstants;
import com.ixortalk.authserver.service.MailService;
import com.ixortalk.authserver.service.UserService;
import com.ixortalk.authserver.web.rest.dto.ManagedUserDTO;
import com.ixortalk.authserver.web.rest.util.HeaderUtil;
import com.ixortalk.authserver.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.springframework.http.ResponseEntity.badRequest;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private MailService mailService;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /users  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     * </p>
     *
     * @param managedUserDTO the user to create
     * @return the ResponseEntity with status 201 (Created) and with body the new user, or with status 400 (Bad Request) if the login or email is already in use
     * @throws URISyntaxException if the Location URI syntaxt is incorrect
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> createUser(@RequestBody ManagedUserDTO managedUserDTO) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserDTO);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
            return badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use"))
                .body(null);
        } else if (userRepository.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
            return badRequest()
                .headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "Email already in use"))
                .body(null);
        } else {
            User newUser = userService.createUser(managedUserDTO);
            mailService.sendCreationEmail(newUser, ofNullable(managedUserDTO.getEmailActivationBaseUrl()));
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert( "userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * PUT  /users : Updates an existing User.
     *
     * @param managedUserDTO the user to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated user,
     * or with status 400 (Bad Request) if the login or email is already in use,
     * or with status 500 (Internal Server Error) if the user couldnt be updated
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserDTO> updateUser(@RequestBody ManagedUserDTO managedUserDTO) {
        log.debug("REST request to update User : {}", managedUserDTO);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use")).body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use")).body(null);
        }
        return userRepository
            .findOneById(managedUserDTO.getId())
            .map(user -> {
                user.setLogin(managedUserDTO.getLogin());
                user.setFirstName(managedUserDTO.getFirstName());
                user.setLastName(managedUserDTO.getLastName());
                user.setEmail(managedUserDTO.getEmail());
                user.setActivated(managedUserDTO.isActivated());
                user.setLangKey(managedUserDTO.getLangKey());
                Set<Authority> authorities = user.getAuthorities();
                authorities.clear();
                managedUserDTO.getAuthorities().stream().forEach(
                    authority -> authorities.add(authorityRepository.findOne(authority))
                );
                return ResponseEntity.ok()
                    .headers(HeaderUtil.createAlert("userManagement.updated", managedUserDTO.getLogin()))
                    .body(new ManagedUserDTO(userRepository.findOne(managedUserDTO.getId()), userService.constructProfilePictureUrl(user)));
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST /users/import : Either registers or updates the user
     *
     * @param managedUserDTO the user to create
     * @param request the HTTP request
     * @return the ResponseEntity with status 201 (Created), 200 (Updated), or with status 400 (Bad Request) or 500 (Internal server error) if something goes wrong
     * @throws URISyntaxException if the Location URI syntaxt is incorrect
     */
    @RequestMapping(value = "/users/import",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    @Transactional
    public ResponseEntity<?> importUser(@RequestBody ManagedUserDTO managedUserDTO, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to import User : {}", managedUserDTO);

        Optional<User> existingUser = userRepository.findOneByEmail(managedUserDTO.getEmail());
        if (existingUser.isPresent() && (managedUserDTO.getId() != null && !existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "emailexists", "E-mail already in use")).body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (managedUserDTO.getId() != null && !existingUser.get().getId().equals(managedUserDTO.getId()))) {
            return badRequest().headers(HeaderUtil.createFailureAlert("userManagement", "userexists", "Login already in use")).body(null);
        }

        return existingUser
                .map(user -> {
                    managedUserDTO.setId(user.getId());
                    return updateUser(managedUserDTO);
                })
                .orElse((ResponseEntity<ManagedUserDTO>) createUser(managedUserDTO));
    }

    /**
     * GET  /users : get all users.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body all users
     * @throws URISyntaxException if the pagination headers couldnt be generated
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<ManagedUserDTO>> getAllUsers(Pageable pageable)
        throws URISyntaxException {
        Page<User> page = userRepository.findAll(pageable);
        List<ManagedUserDTO> managedUserDTOs = page.getContent().stream()
            .map(user -> new ManagedUserDTO(user, userService.constructProfilePictureUrl(user)))
            .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(managedUserDTOs, headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login : get the "login" user.
     *
     * @param login the login of the user to find
     * @return the ResponseEntity with status 200 (OK) and with body the "login" user, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/users/{login:" + Constants.LOGIN_REGEX + "}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(user -> new ManagedUserDTO(user, userService.constructProfilePictureUrl(user)))
                .map(managedUserDTO -> new ResponseEntity<>(managedUserDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  USER :login : delete the "login" User.
     *
     * @param login the login of the user to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/users/{login:" + Constants.LOGIN_REGEX + "}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        userService.deleteUserInformation(login);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "userManagement.deleted", login)).build();
    }
}
