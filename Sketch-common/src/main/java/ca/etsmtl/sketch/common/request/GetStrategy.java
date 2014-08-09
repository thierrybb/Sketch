package ca.etsmtl.sketch.common.request;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

class GetStrategy implements RequestStrategy {
    private String baseUrl;

    public GetStrategy(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public JSONObject execute(String action, Map<String, String> parameter) {
        try {

            String parameters = "";

            for (Map.Entry<String, String> stringStringEntry : parameter.entrySet()) {
                parameters += (parameters == "") ? "" : "&"
                        + URLEncoder.encode((stringStringEntry.getKey()) + "="
                        + URLEncoder.encode(stringStringEntry.getValue()));
            }

            String fullUrl = baseUrl + action + "?" + parameters;

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(fullUrl), Session.instance.getContext());
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                return new JSONObject(responseString);
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            return new JSONObject();
        }
    }
}
