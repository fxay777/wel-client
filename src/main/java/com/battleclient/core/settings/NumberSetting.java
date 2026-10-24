package com.battleclient.core.settings;

public class NumberSetting extends ModSetting<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String id, String displayName, String description, double defaultValue, double min, double max, double increment) {
        super(id, displayName, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public NumberSetting(String id, String displayName, double defaultValue, double min, double max, double increment) {
        this(id, displayName, "", defaultValue, min, max, increment);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public int getIntValue() {
        return (int) Math.round(value);
    }

    public float getFloatValue() {
        return value.floatValue();
    }

    @Override
    public void setValue(Double val) {
        double clamped = Math.max(min, Math.min(max, val));
        double precision = 1.0 / increment;
        this.value = Math.round(clamped * precision) / precision;
    }
}
