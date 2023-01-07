package com.bogdan.todouser.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "authorization")
public class AuthorizationProperties {

    private Integer hours;

    private String algorithmSecret;

    private String issuer;

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getAlgorithmSecret() {
        return algorithmSecret;
    }

    public void setAlgorithmSecret(String algorithmSecret) {
        this.algorithmSecret = algorithmSecret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
