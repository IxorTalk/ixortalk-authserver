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
package com.ixortalk.authserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@ConfigurationProperties("ixortalk")
public class IxorTalkProperties {

    private List<String> roles = newArrayList();

    private OAuth oauth = new OAuth();

    private LoadbalancerConfig loadbalancer = new LoadbalancerConfig();

    private Map<String, Microservice> server = newHashMap();

    private Logout logout = new Logout();

    private ProfilePicture profilePicture = new ProfilePicture();

    public Logout getLogout() {
        return logout;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public OAuth getOauth() {
        return oauth;
    }

    public void setOauth(OAuth oauth) {
        this.oauth = oauth;
    }

    public LoadbalancerConfig getLoadbalancer() {
        return loadbalancer;
    }

    public Map<String, Microservice> getServer() {
        return server;
    }

    public void setLoadbalancer(LoadbalancerConfig loadbalancer) {
        this.loadbalancer = loadbalancer;
    }

    public Microservice getMicroservice(String name) {
        return getServer().get(name);
    }

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public static class OAuth {

        private Clients clients = new Clients();

        public Clients getClients() {
            return clients;
        }

        public void setClients(Clients clients) {
            this.clients = clients;
        }
    }

    public static class Clients {
        private boolean useJdbc = false;

        public boolean isUseJdbc() {
            return useJdbc;
        }

        public void setUseJdbc(boolean useJdbc) {
            this.useJdbc = useJdbc;
        }

    }

    public static class LoadbalancerConfig {

        private Loadbalancer internal = new Loadbalancer();
        private Loadbalancer external = new Loadbalancer();

        public Loadbalancer getInternal() {
            return internal;
        }

        public void setInternal(Loadbalancer internal) {
            this.internal = internal;
        }

        public Loadbalancer getExternal() {
            return external;
        }

        public void setExternal(Loadbalancer external) {
            this.external = external;
        }
    }

    public static class Loadbalancer {

        private String url;
        private String protocol;
        private String host;
        private int port;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUrlWithoutStandardPorts() {
            String urlWithoutStandardPorts = this.protocol + "://" + this.host;
            if (!isDefaultPort(this.protocol, this.port)) {
                urlWithoutStandardPorts += ":" + this.port;
            }
            return urlWithoutStandardPorts;
        }

        public static boolean isDefaultPort(String protocol, int port) {
            return ("http".equals(protocol) && port==80) || ("https".equals(protocol) && port==443);
        }
    }

    public static class Microservice {

        private int port;
        private String contextPath;
        private String url;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getContextPath() {
            return contextPath;
        }

        public void setContextPath(String contextPath) {
            this.contextPath = contextPath;
        }


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Logout {

        private String defaultRedirectUri = "/login";
        private String redirectUriParamName;

        public void setDefaultRedirectUri(String defaultRedirectUri) {
            this.defaultRedirectUri = defaultRedirectUri;
        }

        public String getDefaultRedirectUri() {
            return defaultRedirectUri;
        }

        public String getRedirectUriParamName() {
            return redirectUriParamName;
        }

        public void setRedirectUriParamName(String redirectUriParamName) {
            this.redirectUriParamName = redirectUriParamName;
        }
    }

    public static class ProfilePicture {

        private String s3Bucket;

        public String getS3Bucket() {
            return s3Bucket;
        }

        public void setS3Bucket(String s3Bucket) {
            this.s3Bucket = s3Bucket;
        }
    }
}
