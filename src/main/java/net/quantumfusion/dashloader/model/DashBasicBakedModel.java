package net.quantumfusion.dashloader.model;

import io.activej.serializer.annotations.Deserialize;
import io.activej.serializer.annotations.Serialize;
import io.activej.serializer.annotations.SerializeNullable;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.quantumfusion.dashloader.DashRegistry;
import net.quantumfusion.dashloader.data.DashDirection;
import net.quantumfusion.dashloader.mixin.accessor.BasicBakedModelAccessor;
import net.quantumfusion.dashloader.model.components.DashBakedQuad;
import net.quantumfusion.dashloader.model.components.DashModelOverrideList;
import net.quantumfusion.dashloader.model.components.DashModelTransformation;
import net.quantumfusion.dashloader.util.PairMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBasicBakedModel implements DashModel {
    @Serialize(order = 0)
    public List<DashBakedQuad> quads;
    @Serialize(order = 1)
    public PairMap<DashDirection, List<DashBakedQuad>> faceQuads;
    @Serialize(order = 2)
    public boolean usesAo;
    @Serialize(order = 3)
    public boolean hasDepth;
    @Serialize(order = 4)
    public boolean isSideLit;
    @Serialize(order = 5)
    @SerializeNullable
    public DashModelTransformation transformation;
    @Serialize(order = 6)
    public DashModelOverrideList itemPropertyOverrides;
    @Serialize(order = 7)
    public Integer spritePointer;

    public DashBasicBakedModel() {
    }

    public DashBasicBakedModel(@Deserialize("quads") List<DashBakedQuad> quads,
                               @Deserialize("faceQuads") PairMap<DashDirection, List<DashBakedQuad>> faceQuads,
                               @Deserialize("usesAo") boolean usesAo,
                               @Deserialize("hasDepth") boolean hasDepth,
                               @Deserialize("isSideLit") boolean isSideLit,
                               @Deserialize("transformation") DashModelTransformation transformation,
                               @Deserialize("itemPropertyOverrides") DashModelOverrideList itemPropertyOverrides,
                               @Deserialize("spritePointer") Integer spritePointer) {
        this.quads = quads;
        this.faceQuads = faceQuads;
        this.usesAo = usesAo;
        this.hasDepth = hasDepth;
        this.isSideLit = isSideLit;
        this.transformation = transformation;
        this.itemPropertyOverrides = itemPropertyOverrides;
        this.spritePointer = spritePointer;
    }

    public DashBasicBakedModel(BasicBakedModel basicBakedModel,
                               DashRegistry registry) {
        BasicBakedModelAccessor access = ((BasicBakedModelAccessor) basicBakedModel);
        quads = new ArrayList<>();
        access.getQuads().forEach(bakedQuad -> quads.add(new DashBakedQuad(bakedQuad)));
        final Map<Direction, List<BakedQuad>> faceQuads = access.getFaceQuads();
        this.faceQuads = new PairMap<>(faceQuads.size());
        faceQuads.forEach((direction, bakedQuads) -> {
            List<DashBakedQuad> out = new ArrayList<>();
            bakedQuads.forEach(bakedQuad -> out.add(new DashBakedQuad(bakedQuad)));
            this.faceQuads.put(new DashDirection(direction), out);
        });
        itemPropertyOverrides = new DashModelOverrideList(access.getItemPropertyOverrides(), registry);
        usesAo = access.getUsesAo();
        hasDepth = access.getHasDepth();
        isSideLit = access.getIsSideLit();
        final ModelTransformation transformation = access.getTransformation();
        this.transformation = transformation == ModelTransformation.NONE ? null : DashModelTransformation.createDashModelTransformation(transformation);
        spritePointer = registry.createSpritePointer(access.getSprite());
    }


    @Override
    public BakedModel toUndash(final DashRegistry registry) {
        final Sprite sprite = registry.getSprite(spritePointer);
        final List<BakedQuad> quadsOut = new ArrayList<>();
        final Map<Direction, List<BakedQuad>> faceQuadsOut = new HashMap<>();
        quads.forEach(dashBakedQuad -> quadsOut.add(dashBakedQuad.toUndash(sprite, registry)));
        faceQuads.forEach((dashDirection, dashBakedQuads) -> {
            List<BakedQuad> out = new ArrayList<>();
            dashBakedQuads.forEach(dashBakedQuad -> out.add(dashBakedQuad.toUndash(sprite, registry)));
            faceQuadsOut.put(dashDirection.toUndash(registry), out);
        });
        return new BasicBakedModel(quadsOut, faceQuadsOut, usesAo, isSideLit, hasDepth, sprite, transformation == null ? ModelTransformation.NONE : transformation.toUndash(), itemPropertyOverrides.toUndash(registry));
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
