package dev.dpvb.outlast.messages;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a message with terminal ops and intermediate transform options.
 */
@FunctionalInterface
public interface Message extends ComponentLike {
    /**
     * Sends this message to an audience.
     *
     * @param audience an audience
     * @see Player
     */
    default void send(@NotNull Audience audience) {
        audience.sendMessage(this);
    }

    /**
     * Sends this message to all online members of a given team.
     */
    default void sendTeam(@NotNull String teamName) {
        // no-op for now, TODO
    }

    /**
     * Sends this message to the console.
     */
    default void sendConsole() {
        Bukkit.getConsoleSender().sendMessage(this);
    }

    /**
     * Replaces all occurrences of a string with another string.
     * <p>
     * The replacement always happens after {@link #asComponent()} is called.
     *
     * @param match a string to match
     * @param replacement a string to replace the match with
     */
    default Message replaceText(@NotNull String match, @NotNull String replacement) {
        return () -> this.asComponent().replaceText(b -> b.matchLiteral(match).replacement(replacement));
    }

    /**
     * Creates a new message from a component.
     *
     * @param component a component
     * @return a new message with the component as its source
     */
    static Message from(@NotNull Component component) {
        return () -> component;
    }

    /**
     * Creates a message from a MiniMessage string.
     * <p>
     * The returned message supports {@link RawText} operations.
     *
     * @param message a MiniMessage string
     * @return a new message with the MiniMessage string as its source
     * @see RawText#of(String)
     */
    static RawText mini(@NotNull String message) {
        return RawText.of(message);
    }

    /**
     * Creates a message from a MiniMessage string with custom tags.
     *
     * @param message a MiniMessage string
     * @param resolver a tag resolver for custom tags
     * @return a new message with the MiniMessage string as its source
     */
    static Message mini(@NotNull String message, @NotNull TagResolver resolver) {
        return from(MiniMessageService.build(message, resolver));
    }

    /**
     * Creates a message from a MiniMessage string with custom tags.
     *
     * @param message a MiniMessage string
     * @param resolvers tag resolvers for custom tags
     * @return a new message with the MiniMessage string as its source
     */
    static Message mini(@NotNull String message, @NotNull TagResolver... resolvers) {
        return from(MiniMessageService.build(message, resolvers));
    }
}
