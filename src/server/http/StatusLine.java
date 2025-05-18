package server.http;

public class StatusLine {
    private String httpVersion;
    private int statusCode;
    private String reasonPhrase;

    public StatusLine() {
        this.httpVersion = "HTTP/1.1";
        this.statusCode = 200;
        this.reasonPhrase = "OK";
    }

    public StatusLine(String httpVersion, int statusCode, String reasonPhrase) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public String getHttpVersion() { return httpVersion; }
    public void setHttpVersion(String httpVersion) { this.httpVersion = httpVersion; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getReasonPhrase() { return reasonPhrase; }
    public void setReasonPhrase(String reasonPhrase) { this.reasonPhrase = reasonPhrase; }

    @Override
    public String toString() { return String.format("%s %d %s", httpVersion, statusCode, reasonPhrase); }
}