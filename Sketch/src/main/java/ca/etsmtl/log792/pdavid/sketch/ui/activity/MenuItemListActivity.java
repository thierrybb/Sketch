package ca.etsmtl.log792.pdavid.sketch.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ca.etsmtl.log792.pdavid.sketch.R;
import ca.etsmtl.log792.pdavid.sketch.ui.activity.dummy.DummyContent;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.BaseGridFragment;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment;
import ca.etsmtl.log792.pdavid.sketch.ui.fragment.SketchesGridFragment;


/**
 * An activity representing a list of MenuItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MenuItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment} and the item details
 * (if present) is a {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ca.etsmtl.log792.pdavid.sketch.ui.fragment.MenuItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MenuItemListActivity extends FragmentActivity
        implements MenuItemListFragment.Callbacks, BaseGridFragment.GridCallbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private int mDrawerTitle = R.string.title_menuitem_list;
    private boolean backMustZoomOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        /**
         * Select between Two Pane Layout or 1 pane + Nav Drawer
         */
        if (findViewById(R.id.menuitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((MenuItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.menuitem_list))
                    .setActivateOnItemClick(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);

            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<DummyContent.DummyItem>(this,
                    R.layout.drawer_list_item, DummyContent.ITEMS));
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {
                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);


            // Set Menu Item 0 as Default
            if (savedInstanceState == null) {
                selectMenuItem(0);
            }
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (!mTwoPane)
            mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!mTwoPane)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (!mTwoPane)
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method from {@link MenuItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onListItemSelected(int id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

            Fragment fragment = null;
            try {
                DummyContent.DummyItem dummyItem = DummyContent.ITEMS.get(id);
                Object instance = dummyItem.getFragment().newInstance();
                if (instance instanceof Fragment) {
                    fragment = (Fragment) instance;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.menuitem_detail_container, fragment, dummyItem.getTag()).addToBackStack(null)
                            .commit();

                } else {
                    selectMenuItem(id);
                }
            } catch (InstantiationException e) {

            } catch (IllegalAccessException e) {
            }
        }
    }

    @Override
    public void onGridItemClick(int id) {
        backMustZoomOut = true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectMenuItem(position);
        }
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectMenuItem(int position) {
        // Creates a new instance of a fragment, if the item doesn't contain
        // a fragment, start the activity
        final DummyContent.DummyItem item = DummyContent.ITEMS.get(position);
        final Class aClass = item.getFragment();

        if (aClass.toString().contains("Fragment")) {

            Fragment fragment = null;
            try {
                fragment = (Fragment) aClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            Bundle args = new Bundle();
            args.putInt("id", position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, item.getTag()).addToBackStack(null)
                    .commit();


            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            setTitle(item.title);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            startActivityForResult(new Intent(getApplicationContext(), FullscreenActivity.class), 0);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                final FragmentManager fragmentManager = getFragmentManager();
                final Fragment fragmentByTag = fragmentManager.findFragmentByTag(SketchesGridFragment.TAG);
                SketchesGridFragment sketchesGridFragment = (SketchesGridFragment) fragmentByTag;

                assert sketchesGridFragment != null;
                sketchesGridFragment.zoomOut();

                return !backMustZoomOut;
        }
        return super.onKeyDown(keyCode, event);
    }
}
