package ca.etsmtl.log792.pdavid.sketch.network;

/**
 * Created by philippe on 02/12/13.
 */

import android.net.Uri;

import ca.etsmtl.log792.pdavid.sketch.ApplicationManager;

/**
 * @author Philippe David
 */
public class GetRequest<T> extends AbsDataRequest {

    private final String url;

    /**
     * Constructor
     *
     * @param clazz
     * @param url
     */
    public GetRequest(Class<Object> clazz, String url) {
        super(clazz, url);
        this.url = url;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {

        // With Uri.Builder class we can build our url is a safe manner
        final Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        final String url = uriBuilder.build().toString();
        Object forObject;

        forObject = getRestTemplate().getForObject(url, getResultType());


        return forObject;
    }

}