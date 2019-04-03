package com.natixis.natixisresearch.app.network.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Created by Thibaud on 15/04/2017.
 */
public class User {

        @JsonProperty("FirstName")
        private String firstname;
    @JsonProperty("LastName")
    private String lastname;

    @JsonProperty("Id")
    private int id;


    @JsonProperty("Login")
    private String login;

    @JsonProperty("Rights")
    private String[] privileges;


    @JsonProperty("Token")
    private String token;

        @JsonProperty("Email")
        private String email;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String[] privileges) {
        this.privileges = privileges;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return getFirstname()+" "+getLastname();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (firstname != null ? !firstname.equals(user.firstname) : user.firstname != null)
            return false;
        if (lastname != null ? !lastname.equals(user.lastname) : user.lastname != null)
            return false;
        if (login != null ? !login.equals(user.login) : user.login != null) return false;
        if (!Arrays.equals(privileges, user.privileges)) return false;
        if (token != null ? !token.equals(user.token) : user.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstname != null ? firstname.hashCode() : 0;
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (privileges != null ? Arrays.hashCode(privileges) : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
