package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.mixin.accessor.BuiltinBakedModelAccessor;
import net.quantumfusion.dashloader.model.components.DashModelOverrideList;
import net.quantumfusion.dashloader.model.components.DashModelTransformation;

public class DashBuiltinBakedModel implements DashModel {
    @Serialize(order = 0)
    @SerializeNullable
    public DashModelTransformation transformation;
    @Serialize(order = 1)
    public DashModelOverrideList itemPropertyOverrides;
    @Serialize(order = 2)
    public Integer spritePointer;
    @Serialize(order = 3)
    public boolean sideLit;

    public DashBuiltinBakedModel(
            @Deserialize("transformation") DashModelTransformation transformation,
            @Deserialize("itemPropertyOverrides") DashModelOverrideList itemPropertyOverrides,
            @Deserialize("spritePointer") Integer spritePointer,
            @Deserialize("sideLit") boolean sideLit) {
        this.transformation = transformation;
        this.itemPropertyOverrides = itemPropertyOverrides;
        this.spritePointer = spritePointer;
        this.sideLit = sideLit;
    }

    public DashBuiltinBakedModel() {
    }

    public DashBuiltinBakedModel(BuiltinBakedModel builtinBakedModel, DashRegistry registry) {
        BuiltinBakedModelAccessor access = ((BuiltinBakedModelAccessor) builtinBakedModel);
        final ModelTransformation transformation = access.getTransformation();
        this.transformation = transformation == ModelTransformation.NONE ? null : DashModelTransformation.createDashModelTransformation(transformation);
        itemPropertyOverrides = new DashModelOverrideList(access.getItemPropertyOverrides(), registry);
        spritePointer = registry.createSpritePointer(access.getSprite());
        sideLit = access.getSideLit();
    }


    @Override
    public BakedModel toUndash(DashRegistry registry) {
        Sprite sprite = registry.getSprite(spritePointer);
        return new BuiltinBakedModel(transformation == null ? ModelTransformation.NONE : transformation.toUndash(), itemPropertyOverrides.toUndash(registry), sprite, sideLit);
    }

    @Override
    public void apply(DashRegistry registry) {
        itemPropertyOverrides.applyOverrides(registry);
    }


    @Override
    public int getStage() {
        return 0;
    }
}
