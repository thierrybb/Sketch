package ca.etsmtl.sketch.ui.activity.provider;

import android.content.Context;
import android.view.ActionProvider;
import android.view.View;

/**
 * Created by Phil on 13/11/13.
 */
public class ColorPickerProvider extends ActionProvider {

    private Context mContext;

    /**
     * Creates a new instance. ActionProvider classes should always implement a
     * constructor that takes a single Context parameter for inflating from menu XML.
     *
     * @param context Context for accessing resources.
     */
    public ColorPickerProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }
}
