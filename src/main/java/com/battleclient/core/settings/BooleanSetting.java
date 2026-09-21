package com.battleclient.core.settings;

public class BooleanSetting extends ModSetting<Boolean> {

    public BooleanSetting(String id, String displayName, String description, boolean defaultValue) {
        super(id, displayName, description, defaultValue);
    }

    public BooleanSetting(String id, String displayName, boolean defaultValue) {
        this(id, displayName, "", defaultValue);
    }

    public void toggle() {
        this.value = !this.value;
    }

    public boolean isEnabled() {
        return this.value;
    }
}
