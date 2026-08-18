package model;

import java.io.Serializable;


public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String name;
    private String username;
    private String password;
    protected String role;
    public User(String userId, String name, String username, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public User(String username, String password, String role) {
        this("", "", username, password, role);
    }


    public boolean authenticate(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }


    public abstract String getDisplayInfo();


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role + ": " + name + " (" + username + ")";
    }
}
