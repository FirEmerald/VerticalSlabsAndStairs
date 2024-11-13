package com.firemerald.additionalplacements.client.models;

import java.util.*;
import java.util.function.Function;

import com.firemerald.additionalplacements.block.AdditionalPlacementBlock;
import com.firemerald.additionalplacements.client.models.fixed.BakedFixedModel;
import com.firemerald.additionalplacements.util.BlockRotation;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;

public class BlockModelCache {
	private static final List<Function<BakedModel, BakedModel>> UNWRAPPERS = new ArrayList<>();
	
	public static void registerUnwrapper(Function<BakedModel, BakedModel> unwrapper) {
		UNWRAPPERS.add(unwrapper);
	}
	
	public static BakedModel unwrap(BakedModel model) {
		Optional<BakedModel> next;
		while ((next = unwrapSingle(model)).isPresent()) model = next.get();
		return model;
	}
	
	private static Optional<BakedModel> unwrapSingle(BakedModel model) {
		return UNWRAPPERS.stream().map(uw -> uw.apply(model)).filter(bm -> bm != null).findFirst();
	}
	
	private static record OurModelKey(UnbakedModel model, Transformation rotation, boolean uvLocked, ResourceLocation modelLocation) {}
	
	private static final Map<OurModelKey, BakedModel> OUR_MODEL_CACHE = new HashMap<>();
	
	public static BakedModel bake(UnbakedModel model, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
		return OUR_MODEL_CACHE.computeIfAbsent(new OurModelKey(model, modelTransform.getRotation(), modelTransform.isUvLocked(), modelLocation), key -> BlockModelCache.unwrap(model.bake(baker, spriteGetter, modelTransform, modelLocation)));	
	}
	
	private static record TheirModelKey(UnbakedModel model, ResourceLocation modelLocation) {}
	
	private static final Map<TheirModelKey, BakedModel> THEIR_MODEL_CACHE = new HashMap<>();
	
	public static BakedModel bake(UnbakedModel model, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation) {
		return THEIR_MODEL_CACHE.computeIfAbsent(new TheirModelKey(model, modelLocation), key -> BlockModelCache.unwrap(model.bake(baker, spriteGetter, BlockModelRotation.X0_Y0, modelLocation)));
	}
	
	private static record ModelKey(AdditionalPlacementBlock<?> block, BakedModel ourModel, BakedModel theirModel, BlockRotation modelRotation) {}
	
	private static final Map<ModelKey, BakedFixedModel> MODEL_CACHE = new HashMap<>();
	
	public static BakedFixedModel bake(AdditionalPlacementBlock<?> block, BakedModel ourModel, BakedModel theirModel, BlockRotation modelRotation) {
		return MODEL_CACHE.computeIfAbsent(new ModelKey(block, ourModel, theirModel, modelRotation), key -> new BakedFixedModel(block, ourModel, theirModel, modelRotation));
	}
	
	public static void clearCache() {
		OUR_MODEL_CACHE.clear();
		THEIR_MODEL_CACHE.clear();
		MODEL_CACHE.clear();
	}
}