package ftp.client.FTPClient.transfer;

/**
 * Created by alex on 2/25/2017.
 */
public class FTPRequest {

    private String command;
    private String arguments;

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
}
