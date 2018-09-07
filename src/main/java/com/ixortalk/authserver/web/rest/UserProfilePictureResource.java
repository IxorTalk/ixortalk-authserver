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

import com.amazonaws.services.s3.model.S3Object;
import com.codahale.metrics.annotation.Timed;
import com.ixortalk.authserver.config.Constants;
import com.ixortalk.authserver.domain.User;
import com.ixortalk.authserver.repository.UserRepository;
import com.ixortalk.aws.s3.library.config.AwsS3Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import java.util.Optional;

import static com.amazonaws.util.IOUtils.toByteArray;
import static org.springframework.http.MediaType.valueOf;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

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
@ConditionalOnProperty({"com.ixortalk.s3.default-bucket"})
public class UserProfilePictureResource {

    private final Logger log = LoggerFactory.getLogger(UserProfilePictureResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private AwsS3Template awsS3Template;

    @RequestMapping(value = "/users/{login:" + Constants.LOGIN_REGEX + "}/profile-picture", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> getProfilePictureByLogin(@PathVariable String login) {
        log.debug("REST request to get User Profile Picture by login : {}", login);
        return getProfilePicture(userRepository.findOneByLogin(login));
    }

    @RequestMapping(value = "/profile-pictures/{profilePictureKey}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> getProfilePictureByKey(@PathVariable String profilePictureKey) {
        log.debug("REST request to get User Profile Picture by key : {}", profilePictureKey);
        return getProfilePicture(userRepository.findOneByProfilePictureKey(profilePictureKey));
    }

    private ResponseEntity<?> getProfilePicture(Optional<User> optionalUser) {
        return optionalUser
            .map(user -> {
                try {
                    S3Object s3Object = awsS3Template.get(user.getProfilePictureKey());
                    return ok()
                        .contentType(valueOf(s3Object.getObjectMetadata().getContentType()))
                        .body(toByteArray(s3Object.getObjectContent()));
                } catch (Exception e) {
                    log.error("Error retrieving profile picture: " + e.getMessage(), e);
                    return notFound().build();
                }
            })
            .orElse(notFound().build());
    }


    @RequestMapping(value = "/users/{login:" + Constants.LOGIN_REGEX + "}/profile-picture", method = RequestMethod.POST)
    @Timed
    @PreAuthorize("hasRole('ROLE_ADMIN') or  T(org.apache.commons.lang.StringUtils).equalsIgnoreCase(#login, authentication.name)")
    @Transactional
    public ResponseEntity<?> postProfilePicture(@PathVariable String login, @RequestParam("file") MultipartFile uploadedFileRef) {
        return userRepository.findOneByLogin(login)
            .map(User::generateProfilePictureKey)
            .map(user -> {
                try {
                    awsS3Template.save(user.getProfilePictureKey(), uploadedFileRef);
                } catch (Exception e) {
                    log.error("Error setting profile picture: " + e.getMessage(), e);
                    return notFound().build();
                }
                return ok().build();
            })
            .orElse(notFound().build());
    }
}
