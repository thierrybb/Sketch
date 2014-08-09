package ca.etsmtl.sketch.common.request;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class Session {
    public final static Session instance = new Session();

    private final HttpContext localContext;

    private Session() {
        CookieStore cookieStore = new BasicCookieStore();
        // Create local HTTP context
        localContext = new BasicHttpContext();
        // Bind custom cookie store to the local context
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public HttpContext getContext() {
        return localContext;
    }
}
