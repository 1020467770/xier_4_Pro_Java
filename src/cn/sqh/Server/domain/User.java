package cn.sqh.Server.domain;


import java.util.Objects;

public class User {
    private int id;

    private String username;
    private String password;
    private long currentContain;
    private long container;

    public long getCurrentContain() {
        return currentContain;
    }

    public void setCurrentContain(long currentContain) {
        this.currentContain = currentContain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        User user = (User) o;
        return id == user.id &&
                currentContain == user.currentContain &&
                container == user.container &&
                username.equals(user.username) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, currentContain, container);
    }

    public User() {
    }

    public User(int id, String username, String password, long container) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.container = container;
    }

    public User(String username, String password, long currentContain) {
        this.username = username;
        this.password = password;
        this.currentContain = currentContain;
        this.container = 1024 * 1024 * 1024;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", currentContain=" + currentContain +
                ", container=" + container +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getContainer() {
        return container;
    }

    public void setContainer(long container) {
        this.container = container;
    }
}

