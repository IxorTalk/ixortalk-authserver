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
package com.ixortalk.authserver.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ixortalk.authserver.config.Constants;
import org.hibernate.validator.constraints.Email;

import static java.util.UUID.randomUUID;

/**
 * A user.
 */
@Entity
@Table(name = "jhi_user")
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash",length = 60)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotNull
    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date", nullable = true)
    private ZonedDateTime resetDate = null;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "jhi_user_authority",
        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    private Set<Authority> authorities = new HashSet<>();

    @Column
    private String profilePictureKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    //Lowercase the login before saving it in database
    public void setLogin(String login) {
        this.login = login.toLowerCase(Locale.ENGLISH);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public ZonedDateTime getResetDate() {
       return resetDate;
    }

    public void setResetDate(ZonedDateTime resetDate) {
       this.resetDate = resetDate;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getProfilePictureKey() {
        return profilePictureKey;
    }

    public User generateProfilePictureKey() {
        if (this.profilePictureKey == null) {
            this.profilePictureKey = randomUUID().toString();
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}";
    }
}
