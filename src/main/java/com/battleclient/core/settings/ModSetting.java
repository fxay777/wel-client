package com.battleclient.core.settings;

public abstract class ModSetting<T> {
    private final String id;
    private final String displayName;
    private final String description;
    protected T value;
    protected final T defaultValue;

    public ModSetting(String id, String displayName, String description, T defaultValue) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        this.value = defaultValue;
    }
}
