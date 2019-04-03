package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Thibaud on 15/04/2017.
 */
public class UserLogin {

    @JsonProperty("Login")
    private String login;
    @JsonProperty("Password")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLogin userLogin = (UserLogin) o;

        if (login != null ? !login.equals(userLogin.login) : userLogin.login != null) return false;
        if (password != null ? !password.equals(userLogin.password) : userLogin.password != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
