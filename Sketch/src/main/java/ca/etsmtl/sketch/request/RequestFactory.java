package ca.etsmtl.sketch.request;

import android.content.Context;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.request.BackendRequest;

public class RequestFactory {
    public static BackendRequest newGet(Context context) {
        String baseUrl = context.getString(R.string.backend_base_url);
        return ca.etsmtl.sketch.common.request.RequestFactory.newGet(baseUrl);
    }

    public static BackendRequest newPost(Context context) {
        String baseUrl = context.getString(R.string.backend_base_url);
        return ca.etsmtl.sketch.common.request.RequestFactory.newPost(baseUrl);
    }
}
