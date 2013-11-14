package ca.etsmtl.log792.pdavid.sketch.ui.activity.provider;

import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import ca.etsmtl.log792.pdavid.sketch.R;

/**
 * Created by Phil on 13/11/13.
 */
public class SaveActionProvider extends ActionProvider implements MenuItem.OnMenuItemClickListener {
    private Context mContext;
    private int[] saveType = new int[]{R.string.save_png, R.string.save_svg};

    /**
     * Creates a new instance. ActionProvider classes should always implement a
     * constructor that takes a single Context parameter for inflating from menu XML.
     *
     * @param context Context for accessing resources.
     */
    public SaveActionProvider(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }


    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();

        for (int i = 0; i < saveType.length; i++) {
            subMenu.add(0, i, i, saveType[i])
                    .setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return false;
    }
}
