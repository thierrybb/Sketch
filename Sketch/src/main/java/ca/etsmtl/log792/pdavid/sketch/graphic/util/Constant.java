package ca.etsmtl.log792.pdavid.sketch.graphic.util;

public class Constant {
    public static final String PROGRAM_NAME = "Simple Network Whiteboard";

    public static final int INITIAL_WINDOW_WIDTH = 128; // in pixels
    public static final int INITIAL_WINDOW_HEIGHT = 128; // in pixels

    public static final int BUTTON_WIDTH = 86; // in pixels
    public static final int BUTTON_HEIGHT = 60; // in pixels

    public static final float INK_THICKNESS_IN_WORLD_SPACE_UNITS = 5.0f;

    public static final int NUM_USERS = 1; // should be at least 1

    public static final String PNG_STRING = "png";
    public static final String SVG_STRING = "svg";

    public static final String subdir = "Android/data/";
    public static final String defaultRecipientEmailAddress = "test4stage@gmail.com";

    public static final int PORT = 1967; // anything above 1024 should be fine
    public static final String MULTICAST_ADDRESS = "232.232.232.232"; // TODO

    public static final int MULTICAST_SOCKET_PORT = 4321;
    public static final String MULTICAST_INITIAL_REQUEST_FROM_CLIENT = "Will you accept me as a client?";
    public static final String MULTICAST_REPLY_FROM_SERVER = "Yes the server accepts!";

    public static final String MESSAGE_PREFIX_TO_ADD_STROKE = "a";
    public static final String MESSAGE_PREFIX_TO_REMOVE_STROKE = "r";

    // the NM_ prefix means "Network Mode"
    public static final int NM_NONE = 0;
    public static final int NM_SERVER = 1;
    public static final int NM_CLIENT = 2;

    public static final boolean autoFrameWhenUpdatingOverNetwork = false;

    // These are in pixels.
    public static final int TEXT_HEIGHT = 10;

}
