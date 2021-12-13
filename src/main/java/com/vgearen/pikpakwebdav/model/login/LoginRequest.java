
package com.vgearen.pikpakwebdav.model.login;


public class LoginRequest {

    private String captcha_token;
    private String client_id;
    private String client_secret;
    private String password;
    private String username;

    public String getCaptcha_token() {
        return captcha_token;
    }

    public void setCaptcha_token(String captcha_token) {
        this.captcha_token = captcha_token;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
