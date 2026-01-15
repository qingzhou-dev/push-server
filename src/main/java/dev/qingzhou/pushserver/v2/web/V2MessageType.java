package dev.qingzhou.pushserver.v2.web;

public enum V2MessageType {
    TEXT("text"),
    TEXT_CARD("textcard"),
    MARKDOWN("markdown");

    private final String value;

    V2MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
