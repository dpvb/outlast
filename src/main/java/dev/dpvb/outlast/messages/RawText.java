package dev.dpvb.outlast.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a raw MiniMessage-formatted string.
 *
 * @author ms5984
 */
public interface RawText extends Message {
    /**
     * Gets the raw MiniMessage-formatted string.
     *
     * @return the raw MiniMessage-formatted string
     */
    @NotNull String getRawText();

    /**
     * Replaces all occurrences of a string with another string.
     *
     * @param match a string to match
     * @param replacement a string to replace the match with
     * @return a new RawText
     */
    default @NotNull RawText replaceRaw(@NotNull String match, @NotNull String replacement) {
        return of(getRawText().replace(match, replacement));
    }

    /**
     * Creates a message using a tag resolver and this raw text.
     *
     * @param resolver a tag resolver
     * @return a new message
     */
    default @NotNull Message resolve(@NotNull TagResolver resolver) {
        return () -> MiniMessageService.build(getRawText(), resolver);
    }

    /**
     * Creates a message using tag resolvers and this raw text.
     *
     * @param resolvers tag resolvers
     * @return a new message
     */
    default @NotNull Message resolve(@NotNull TagResolver... resolvers) {
        return () -> MiniMessageService.build(getRawText(), resolvers);
    }

    @Override
    default @NotNull Component asComponent() {
        return MiniMessageService.build(getRawText());
    }

    /**
     * Creates a new RawText from a MiniMessage-formatted string.
     *
     * @param rawText a MiniMessage-formatted string
     * @return a new RawText
     */
    static RawText of(@NotNull String rawText) {
        return new RawTextImpl(rawText);
    }
}
