package net.quantumfusion.dashloader.model.components;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import net.gudenau.lib.unsafe.Unsafe;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.util.Identifier;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.ModelOverrideListAccessor;

public class DashModelOverrideList {
    @Serialize(order = 0)
    public final DashModelOverrideListBakedOverride[] overrides;
    @Serialize(order = 1)
    public final Integer[] conditionTypes; //identifiers

    ModelOverrideList toApply;

    public DashModelOverrideList(@Deserialize("overrides") DashModelOverrideListBakedOverride[] overrides,
                                 @Deserialize("conditionTypes") Integer[] conditionTypes) {
        this.overrides = overrides;
        this.conditionTypes = conditionTypes;
    }

    public DashModelOverrideList(ModelOverrideList modelOverrideList, DashRegistry registry) {
        final ModelOverrideList.BakedOverride[] overrides = ((ModelOverrideListAccessor) modelOverrideList).getOverrides();
        this.overrides = new DashModelOverrideListBakedOverride[overrides.length];
        for (int i = 0; i < overrides.length; i++) {
            this.overrides[i] = new DashModelOverrideListBakedOverride(overrides[i], registry);
        }
        final Identifier[] conditionTypes = ((ModelOverrideListAccessor) modelOverrideList).getConditionTypes();
        this.conditionTypes = new Integer[conditionTypes.length];
        for (int i = 0; i < conditionTypes.length; i++) {
            this.conditionTypes[i] = registry.createIdentifierPointer(conditionTypes[i]);
        }
    }

    public ModelOverrideList toUndash(DashRegistry registry) {
        toApply = Unsafe.allocateInstance(ModelOverrideList.class);
        final int length = conditionTypes.length;
        final Identifier[] identifiers = new Identifier[length];
        for (int i = 0; i < length; i++) {
            identifiers[i] = registry.getIdentifier(conditionTypes[i]);
        }
        ((ModelOverrideListAccessor) toApply).setConditionTypes(identifiers);
        return toApply;
    }

    public void applyOverrides(DashRegistry registry) {
        final int length = this.overrides.length;
        final ModelOverrideList.BakedOverride[] overrides = new ModelOverrideList.BakedOverride[length];
        for (int i = 0; i < length; i++) {
            overrides[i] = this.overrides[i].toUndash(registry);
        }
        ((ModelOverrideListAccessor) toApply).setOverrides(overrides);
    }
}
