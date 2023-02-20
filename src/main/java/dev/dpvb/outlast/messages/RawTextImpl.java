package dev.dpvb.outlast.messages;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ClassCanBeRecord")
class RawTextImpl implements RawText {
    private final String rawText;

    RawTextImpl(@NotNull String rawText) {
        this.rawText = rawText;
    }

    @Override
    public @NotNull String getRawText() {
        return rawText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawTextImpl rawText1 = (RawTextImpl) o;

        return rawText.equals(rawText1.rawText);
    }

    @Override
    public int hashCode() {
        return rawText.hashCode();
    }

    @Override
    public String toString() {
        return "RawTextImpl{" +
                "rawText='" + rawText + '\'' +
                '}';
    }
}
