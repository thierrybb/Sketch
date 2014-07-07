package ca.etsmtl.sketch.ui.activity.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.ui.activity.FullscreenActivity;
import ca.etsmtl.sketch.ui.fragment.MenuItemListFragment;
import ca.etsmtl.sketch.ui.fragment.SkercherGridFragment;
import ca.etsmtl.sketch.ui.fragment.SketchesGridFragment;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> BOOKS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add 4 Menu items.
        addItem(new DummyItem("1", "My Sketches", R.string.menu_1, SketchesGridFragment.class));
//        addItem(new DummyItem("2", "Favorites", R.string.menu_2, SketchesGridFragment.class));
        addItem(new DummyItem("3", "Nearby Sketchers", R.string.menu_3, SkercherGridFragment.class));
        addItem(new DummyItem("4", "Sketch !!", R.string.menu_4, FullscreenActivity.class));

        // Add 4 Book items.
        addBook(new DummyItem("1", "My Sketches", R.string.menu_1, SketchesGridFragment.class));
        addBook(new DummyItem("2", "Private Sketch", R.string.menu_2, MenuItemListFragment.class));
        addBook(new DummyItem("3", "Public Sketch", R.string.menu_3, MenuItemListFragment.class));
        addBook(new DummyItem("4", "Quick Join a Random Public Sketch", R.string.menu_4, FullscreenActivity.class));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static void addBook(DummyItem item) {
        BOOKS.add(item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;
        public int title;
        private Class fragment;
        private String tag;

        public DummyItem(String id, String content, int title, Class f) {
            this.id = id;
            this.content = content;
            this.title = title;
            this.fragment = f;
        }

        @Override
        public String toString() {
            return content;
        }

        public Class getFragment() {
            return fragment;
        }

        public String getTag() {
            return fragment.getName();
        }
    }
}
