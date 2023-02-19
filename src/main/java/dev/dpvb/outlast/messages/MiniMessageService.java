package dev.dpvb.outlast.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class MiniMessageService {
    private static final MiniMessageService INSTANCE = new MiniMessageService();

    private MiniMessage miniMessage;

    private MiniMessageService() {}

    static Component build(@NotNull String message) {
        return INSTANCE.miniMessage.deserialize(message);
    }

    static Component build(@NotNull String message, @NotNull TagResolver resolver) {
        return INSTANCE.miniMessage.deserialize(message, resolver);
    }

    static Component build(@NotNull String message, @NotNull TagResolver... resolvers) {
        return INSTANCE.miniMessage.deserialize(message, resolvers);
    }

    public static void builderSetup(Consumer<MiniMessage.Builder> consumer) {
        final var builder = MiniMessage.builder();
        consumer.accept(builder);
        INSTANCE.miniMessage = builder.build();
    }

    public static @NotNull MiniMessage getMiniMessage() {
        return Objects.requireNonNull(INSTANCE.miniMessage, "MiniMessage is not initialized");
    }
}
