package net.quantumfusion.dashloader.blockstate.property.value;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.quantumfusion.dashloader.DashRegistry;

public class DashBooleanValue implements DashPropertyValue {
    @Serialize(order = 0)
    public Boolean value;

    public DashBooleanValue(@Deserialize("value") Boolean value) {
        this.value = value;
    }

    @Override
    public Comparable toUndash(DashRegistry registry) {
        return value;
    }
}
