package com.firemerald.additionalplacements.client.models.dynamic;

import java.util.List;
import java.util.function.Function;

import com.firemerald.additionalplacements.client.models.BlockModelCache;
import com.firemerald.additionalplacements.client.models.IUnbakedGeometry;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.resources.model.UnbakedModel.Resolver;
import net.minecraft.resources.ResourceLocation;

public class UnbakedDynamicModel implements IUnbakedGeometry<UnbakedDynamicModel>
{
	public final ResourceLocation ourModelLocation;
	private UnbakedModel ourModel;

	public UnbakedDynamicModel(ResourceLocation ourModelLocation)
	{
		this.ourModelLocation = ourModelLocation;
	}

	@Override
	public void resolveDependencies(Resolver modelGetter, BlockModel context)
	{
		ourModel = modelGetter.resolve(ourModelLocation);
	}

	@Override
	public BakedModel bake(BlockModel context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, List<ItemOverride> overrides)
	{
		return new BakedDynamicModel(BlockModelCache.bake(this.ourModel, bakery, spriteGetter, modelTransform));
	}
}
