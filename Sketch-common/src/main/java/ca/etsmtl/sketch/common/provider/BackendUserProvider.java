package ca.etsmtl.sketch.common.provider;

import org.json.JSONException;
import org.json.JSONObject;

import ca.etsmtl.sketch.common.request.RequestFactory;


public class BackendUserProvider implements DrawingProvider {
    private String url;

    public BackendUserProvider(String url) {
        this.url = url;
    }

    @Override
    public AccessToken getToken(String drawingID, String account, String password) {
        JSONObject response = RequestFactory.newPost(url)
                .action("api/can_access")
                .addParameter("email", account)
                .addParameter("password", password)
                .addParameter("drawingID", drawingID)
                .executeForAnswer();

        try {
            return new AccessToken(Integer.parseInt(response.get("ID").toString()));
        } catch (JSONException e) {
            return DrawingProvider.NULL_TOKEN;
        }
    }
}
