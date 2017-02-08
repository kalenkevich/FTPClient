package sample.FTPClient.user;

/**
 * Created by alex on 1/30/2017.
 */
public class User {
    private String hostName;
    private int port;
    private String name;
    private String password;

    public User(String hostName, int port, String name, String password) {
        this.hostName = hostName;
        this.port = port;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}