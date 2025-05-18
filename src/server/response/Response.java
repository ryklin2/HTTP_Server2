package server.response;

import server.http.Headers;
import server.http.StatusLine;
import server.http.Body;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class Response {
    protected final String documentRoot;
    protected StatusLine statusLine;
    protected Headers headers;
    protected Body body;

    public Response(String documentRoot, int statusCode, String reasonPhrase) {
        this.documentRoot = documentRoot;
        this.statusLine = new StatusLine("HTTP/1.1", statusCode, reasonPhrase);
        this.headers = new Headers();
        this.body = new Body();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        headers.set("Date", sdf.format(new Date()));
    }

    public abstract void send(OutputStream outputStream) throws IOException;
}