package dev.dpvb.outlast.messages;

import org.jetbrains.annotations.PropertyKey;

import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Messages {
    private static final Messages INSTANCE = new Messages();
    private ResourceBundle consoleBundle;

    private Messages() {}

    /**
     * Gets a message from the console bundle.
     * <p>
     * Messages to console are stored in {@code lang/console.properties}.
     *
     * @param key the key of the message
     * @return a new Message object
     */
    public static RawText console(@PropertyKey(resourceBundle = "lang.console") String key) {
        return RawText.of(INSTANCE.consoleBundle.getString(key));
    }

    public static void initBundles() throws MissingResourceException {
        INSTANCE.consoleBundle = PropertyResourceBundle.getBundle("lang/console");
    }
}
