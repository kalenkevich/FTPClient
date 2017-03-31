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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FTPResponse response = (FTPResponse) o;

        return statusCode == response.statusCode
                && isError == response.isError
                && (data != null ? data.equals(response.data) :
                response.data == null
                        && (errorMessage != null ? errorMessage.equals(response.errorMessage) : response.errorMessage == null));

    }

    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (isError ? 1 : 0);
        return result;
    }
}
