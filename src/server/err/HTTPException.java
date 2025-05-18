package server.err;

public class HTTPException extends Exception {
    private final int status;

    public HTTPException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}