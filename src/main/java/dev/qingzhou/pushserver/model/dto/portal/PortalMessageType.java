package dev.qingzhou.pushserver.model.dto.portal;

public enum PortalMessageType {
    TEXT("text"),
    TEXT_CARD("textcard"),
    MARKDOWN("markdown");

    private final String value;

    PortalMessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
