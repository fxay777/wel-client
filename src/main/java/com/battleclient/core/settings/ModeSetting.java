package com.battleclient.core.settings;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends ModSetting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String id, String displayName, String description, int defaultIndex, String... modes) {
        super(id, displayName, description, modes[Math.max(0, Math.min(modes.length - 1, defaultIndex))]);
        this.modes = Arrays.asList(modes);
        this.index = Math.max(0, Math.min(modes.length - 1, defaultIndex));
        this.value = this.modes.get(this.index);
    }

    public ModeSetting(String id, String displayName, int defaultIndex, String... modes) {
        this(id, displayName, "", defaultIndex, modes);
    }

    public List<String> getModes() {
        return modes;
    }

    public int getIndex() {
        return index;
    }

    public void cycle() {
        index = (index + 1) % modes.size();
        value = modes.get(index);
    }

    public void cycleBack() {
        index = (index - 1 + modes.size()) % modes.size();
        value = modes.get(index);
    }

    public void setIndex(int idx) {
        if (idx >= 0 && idx < modes.size()) {
            this.index = idx;
            this.value = modes.get(idx);
        }
    }

    @Override
    public void setValue(String val) {
        int idx = modes.indexOf(val);
        if (idx != -1) {
            this.index = idx;
            this.value = val;
        }
    }

    public boolean is(String mode) {
        return value.equalsIgnoreCase(mode);
    }
}
