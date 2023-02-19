package dev.dpvb.outlast.messages;

import org.jetbrains.annotations.PropertyKey;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
    private static final Messages INSTANCE = new Messages();
    private ResourceBundle gameBundle;
    private ResourceBundle consoleBundle;

    private Messages() {}

    /**
     * Gets a message from the game bundle.
     * <p>
     * Game messages are stored in {@code lang/game.properties}.
     *
     * @param key the key of the message
     * @return a new RawText object
     */
    public static RawText game(@PropertyKey(resourceBundle = "lang.game") String key) {
        return RawText.of(INSTANCE.gameBundle.getString(key));
    }

    /**
     * Gets a message from the console bundle.
     * <p>
     * Messages to console are stored in {@code lang/console.properties}.
     *
     * @param key the key of the message
     * @return a new RawText object
     */
    public static RawText console(@PropertyKey(resourceBundle = "lang.console") String key) {
        return RawText.of(INSTANCE.consoleBundle.getString(key));
    }

    public static void initBundles() throws MissingResourceException {
        INSTANCE.gameBundle = PropertyResourceBundle.getBundle("lang/game");
        INSTANCE.consoleBundle = PropertyResourceBundle.getBundle("lang/console");
    }
}
