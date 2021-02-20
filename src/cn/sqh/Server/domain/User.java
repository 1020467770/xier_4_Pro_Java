package cn.sqh.Server.domain;


public class User {
    private int id;

    private String username;
    private String password;
    private long container;

    public User() {
    }

    public User(int id, String username, String password, long container) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.container = container;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.container = 20000000;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
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

