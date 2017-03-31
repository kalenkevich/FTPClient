package ftp.client.FTPClient.transfer;

/**
 * Created by alex on 2/25/2017.
 */
public class FTPResponse {
    private int statusCode;
    private String data;
    private String errorMessage;
    private boolean isError;

    public FTPResponse(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public FTPResponse(int statusCode, String data, String errorMessage) {
        this.statusCode = statusCode;
        this.data = data;
        this.isError = true;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

}
