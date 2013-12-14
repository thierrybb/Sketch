package ca.etsmtl.log792.pdavid.sketch.network;

/**
 * Created by philippe on 02/12/13.
 */

import android.content.Context;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;


/**
 * Singleton. Manage all content data of the application.
 *
 * @author Philippe David
 * @version 1.0
 */
public class DataManager {
    private static final String TAG = DataManager.class.getSimpleName();

    private static DataManager instance;

    private DataManager() {
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public static SpiceManager prepareSpiceManager(Context context) {
        final SpiceManager contentManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

        contentManager.start(context);
        return contentManager;
    }

    /**
     * Start a network request and return true if the call started
     *
     * @param contentManager
     * @param context
     * @param request
     * @param listener
     * @param cacheExpiryDuration
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static boolean performRequest(SpiceManager contentManager, Context context, AbsDataRequest request,
                                         RequestListener listener, long cacheExpiryDuration) {

        if (contentManager != null) {

            final String requestCacheKey = request.createCacheKey();

//            if(!request.url.equals("/state")){
//                Log.w(TAG, request.url);
//            }

//            Log.d(TAG, "requestCacheKey=" + requestCacheKey + " | cacheExpiryDuration=" + cacheExpiryDuration);

            if (!Utils.isNetworkAvailable(context)) {
                contentManager.getFromCache(request.getResultType(), requestCacheKey, DurationInMillis.ALWAYS_RETURNED,
                        listener);
//                Log.v(TAG, "no network > try to use cache");

            } else {

                contentManager.execute(request, requestCacheKey, cacheExpiryDuration, listener);
            }
            return true;
        }
        return false;
    }

    /**
     * Create a Debug String of the network progression state
     *
     * @param progress
     * @return
     */
    public static String convertProgressToString(RequestProgress progress) {
        String status;
        switch (progress.getStatus()) {
            case READING_FROM_CACHE:
                status = "? cache -->";
                break;
            case LOADING_FROM_NETWORK:
                status = "^ network ^";
                break;
            case WRITING_TO_CACHE:
                status = "--> cache";
                break;
            case COMPLETE:
                status = "x complete x";
                break;
            case PENDING:
                status = "x pending x";
                break;

            default:
                status = "";
                break;

        }
        return status;
    }

}
