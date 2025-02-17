package org.example;

public class RequestUserData {
    private String email;
    private String password;
    private String name;

    public RequestUserData(
            String email,
            String password,
            String name
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
