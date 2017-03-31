package ftp.client.FTPClient.transfer;

/**
 * Created by alex on 2/25/2017.
 */
public class FTPRequest {

    private String command;
    private String arguments;

    public FTPRequest(String command) {
        this.command = command;
        this.arguments = "";
    }

    public FTPRequest(String command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return command + " " + arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FTPRequest that = (FTPRequest) o;

        return command != null ? command.equals(that.command) : that.command == null && (arguments != null ? arguments.equals(that.arguments) : that.arguments == null);

    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;

        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);

        return result;
    }
}
