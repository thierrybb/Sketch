package ca.etsmtl.sketch.network;

/**
 * Created by philippe on 02/12/13.
 */

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.security.NoSuchAlgorithmException;


public abstract class AbsDataRequest extends SpringAndroidSpiceRequest<Object> {

    final String url;

    AbsDataRequest(Class<Object> clazz, String url) {
        super(clazz);
        this.url = url;
    }

    public String createCacheKey() {
        try {
            return "MANETS_URL_" + Utils.md5(url.getBytes());
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}