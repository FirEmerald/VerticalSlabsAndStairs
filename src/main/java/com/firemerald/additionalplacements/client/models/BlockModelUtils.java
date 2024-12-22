package com.firemerald.additionalplacements.client.models;

import java.util.*;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import com.firemerald.additionalplacements.block.AdditionalPlacementBlock;
import com.firemerald.additionalplacements.util.BlockRotation;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EmptyBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

@OnlyIn(Dist.CLIENT)
public class BlockModelUtils
{
	public static final ModelProperty<BlockState> MODEL_STATE = new ModelProperty<>();

	public static BlockState getModeledState(BlockState state)
	{
		if (state.getBlock() instanceof AdditionalPlacementBlock) return ((AdditionalPlacementBlock<?>) state.getBlock()).getModelState(state);
		else return state;
	}

	public static final IBakedModel getBakedModel(BlockState state)
	{
		return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
	}

	public static final IModelData getModelData(BlockState blockState, IModelData defaultData)
	{
		return blockState.hasTileEntity() ? (blockState.getBlock()).createTileEntity(blockState, EmptyBlockReader.INSTANCE).getModelData() : defaultData;
		//TODO find a way to merge data
	}

	public static final BakedQuad retexture(BakedQuad jsonBakedQuad, TextureAtlasSprite newSprite, int newTintIndex, int vertexSize, int uvOffset)
	{
		return new BakedQuad(
				updateVertices(
						jsonBakedQuad.getVertices(),
						jsonBakedQuad.getSprite(),
						newSprite,
						vertexSize,
						uvOffset
						),
				newTintIndex,
				jsonBakedQuad.getDirection(),
				newSprite,
				jsonBakedQuad.isShade()
				);
	}

	public static final float[] ZERO_POINT = {0, 0, 0};

	public static final float getFaceSize(int[] vertices, int vertexSize, int posOffset)
	{
		float[] first = newVertex(vertices, 0, posOffset);
		float[] prev = new float[3];
		float[] cur = newVertex(vertices, vertexSize, first, posOffset);
		float size = 0;
		for (int vertexIndex = vertexSize * 2; vertexIndex < vertices.length; vertexIndex += vertexSize)
		{
			float[] tmp = prev;
			prev = cur;
			cur = getVertex(vertices, vertexIndex, first, posOffset, tmp);
			size += getArea(prev, cur);
		}
		return size;
	}

	public static float[] newVertex(int[] vertices, int vertexIndex, int posOffset)
	{
		return newVertex(vertices, vertexIndex, ZERO_POINT, posOffset);
	}

	public static float[] newVertex(int[] vertices, int vertexIndex, float[] origin, int posOffset)
	{
		return new float[] {
			Float.intBitsToFloat(vertices[vertexIndex + posOffset]) - origin[0],
			Float.intBitsToFloat(vertices[vertexIndex + posOffset + 1]) - origin[1],
			Float.intBitsToFloat(vertices[vertexIndex + posOffset + 2]) - origin[2]
		};
	}

	public static float[] getVertex(int[] vertices, int vertexIndex, int posOffset, float[] des)
	{
		return getVertex(vertices, vertexIndex, ZERO_POINT, posOffset, des);
	}

	public static float[] getVertex(int[] vertices, int vertexIndex, float[] origin, int posOffset, float[] des)
	{
		des[0] = Float.intBitsToFloat(vertices[vertexIndex + posOffset]) - origin[0];
		des[1] = Float.intBitsToFloat(vertices[vertexIndex + posOffset + 1]) - origin[1];
		des[2] = Float.intBitsToFloat(vertices[vertexIndex + posOffset + 2]) - origin[2];
		return des;
	}

	public static float getArea(float[] ab, float[] ac)
	{
		return .5f * MathHelper.sqrt(
				MathHelper.square(ab[0] * ac[1] - ab[1] * ac[0]) +
				MathHelper.square(ab[1] * ac[2] - ab[2] * ac[1]) +
				MathHelper.square(ab[2] * ac[0] - ab[0] * ac[2])
				);
	}

	public static final int[] updateVertices(int[] vertices, TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertexSize, int uvOffset)
	{
		int[] updatedVertices = vertices.clone();
		for (int vertexIndex = uvOffset; vertexIndex < vertices.length; vertexIndex += vertexSize)
		{
			updatedVertices[vertexIndex] = changeUVertexElementSprite(oldSprite, newSprite, vertices[vertexIndex]);
			updatedVertices[vertexIndex + 1] = changeVVertexElementSprite(oldSprite, newSprite, vertices[vertexIndex + 1]);
	    }
		return updatedVertices;
	}

	private static final int changeUVertexElementSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex)
	{
		return Float.floatToRawIntBits(newSprite.getU(getUOffset(oldSprite, Float.intBitsToFloat(vertex))));
	}

	private static final int changeVVertexElementSprite(TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite, int vertex)
	{
		return Float.floatToRawIntBits(newSprite.getV(getVOffset(oldSprite, Float.intBitsToFloat(vertex))));
	}
	
	public static double getUOffset(TextureAtlasSprite tex, float offset) {
		float u0 = tex.getU0();
		return (offset - u0) * 16.0 / (tex.getU1() - u0);
	}
	
	public static double getVOffset(TextureAtlasSprite tex, float offset) {
		float v0 = tex.getV0();
		return (offset - v0) * 16.0 / (tex.getV1() - v0);
	}
	
	public static int[] copyVertices(int[] originalData) {
		int[] newData = new int[originalData.length];
		System.arraycopy(originalData, 0, newData, 0, originalData.length); //direct copy
		return newData;
	}
	
	public static int[] copyVertices(int[] originalData, int vertexSize, int shiftLeft) {
		int[] newData = new int[originalData.length];
		//shiftLeft %= originalData.length / vertexSize;
		if (shiftLeft == 0) {
			System.arraycopy(originalData, 0, newData, 0, originalData.length); //direct copy
		} else {
			int lengthLeft = shiftLeft * vertexSize;
			int lengthRight = originalData.length - lengthLeft;
			System.arraycopy(originalData, lengthLeft, newData, 0, lengthRight); //copy [middle to end] to [start to middle]
			System.arraycopy(originalData, 0, newData, lengthRight, lengthLeft); //copy [start to middle] to [middle to end]
		}
		return newData;
	}
	
	public static Pair<TextureAtlasSprite, Integer> getSidedTexture(BlockState fromState, IBakedModel fromModel, Direction fromSide, Random rand, IModelData extraData, int vertexSize, int posOffset) {
		Map<Pair<TextureAtlasSprite, Integer>, Double> weights = new HashMap<>();
		List<BakedQuad> referenceQuads = fromModel.getQuads(fromState, fromSide, rand, extraData);
		if (fromSide != null && (referenceQuads.isEmpty() || referenceQuads.stream().noneMatch(quad -> quad.getDirection() == fromSide))) //no valid culled sides
			referenceQuads = fromModel.getQuads(fromState, null, rand, extraData); //all quads for this render type
		if (!referenceQuads.isEmpty()) {
			referenceQuads.forEach(referredBakedQuad -> {
				if (fromSide == null || referredBakedQuad.getDirection() == fromSide) { //only for quads facing the correct side
					Pair<TextureAtlasSprite, Integer> tex = Pair.of(referredBakedQuad.getSprite(), referredBakedQuad.getTintIndex());
					weights.merge(tex, (double) BlockModelUtils.getFaceSize(referredBakedQuad.getVertices(), vertexSize, posOffset), Double::sum);
				}
			});
			return weights.entrySet().stream().max((e1, e2) -> (int) Math.signum(e2.getValue() - e1.getValue())).map(Map.Entry::getKey).orElse(
					Pair.of(Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(MissingTextureSprite.getLocation()), -1)
					);
		}
		else return Pair.of(Minecraft.getInstance().getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(MissingTextureSprite.getLocation()), -1);
	}

	public static List<BakedQuad> retexturedQuads(BlockState modelState, IBakedModel originalModel, IBakedModel ourModel, Direction side, Random rand, IModelData modelData)
	{
		VertexFormat format = DefaultVertexFormats.BLOCK;
		int vertexSize = format.getIntegerSize();
		int posOffset = format.getOffset(format.getElements().indexOf(DefaultVertexFormats.ELEMENT_POSITION)) / 4;
		int uvOffset = format.getOffset(format.getElements().indexOf(DefaultVertexFormats.ELEMENT_UV0)) / 4;
		@SuppressWarnings("unchecked")
		Pair<TextureAtlasSprite, Integer>[] textures = new Pair[6];
		List<BakedQuad> originalQuads = ourModel.getQuads(modelState, side, rand, modelData);
		List<BakedQuad> bakedQuads = new ArrayList<>(originalQuads.size());
		for (BakedQuad originalQuad : originalQuads)
		{
			Direction modelSide = originalQuad.getDirection();
			int dirIndex = modelSide.get3DDataValue();
			Pair<TextureAtlasSprite, Integer> texture = textures[dirIndex];
			if (texture == null) texture = textures[dirIndex] = getSidedTexture(modelState, originalModel, modelSide, rand, modelData, vertexSize, posOffset);
    		bakedQuads.add(retexture(originalQuad, texture.getLeft(), texture.getRight(), vertexSize, uvOffset));
		}
		return bakedQuads;
	}

	public static List<BakedQuad> retexturedQuads(BlockState state, BlockState modelState, Function<BlockState, IBakedModel> originalModel, IBakedModel ourModel, Direction side, Random rand, IModelData extraData) {
		IModelData modelData = BlockModelUtils.getModelData(modelState, extraData);
		return retexturedQuads(modelState, originalModel.apply(modelState), ourModel, side, rand, modelData);
	}

	public static List<BakedQuad> rotatedQuads(BlockState modelState, IBakedModel model, BlockRotation rotation, boolean rotateTex, Direction side, Random rand, IModelData modelData)
	{
		VertexFormat format = DefaultVertexFormats.BLOCK;
		int vertexSize = format.getIntegerSize();
		int posOffset = format.getOffset(format.getElements().indexOf(DefaultVertexFormats.ELEMENT_POSITION)) / 4;
		int uvOffset = format.getOffset(format.getElements().indexOf(DefaultVertexFormats.ELEMENT_UV0)) / 4;
		List<BakedQuad> originalQuads = model.getQuads(modelState, rotation.unapply(side), rand, modelData);
		List<BakedQuad> bakedQuads = new ArrayList<>(originalQuads.size());
		for (BakedQuad originalQuad : originalQuads)
		{
    		bakedQuads.add(new BakedQuad(
    				rotation.applyVertices(originalQuad.getDirection(), originalQuad.getVertices(), vertexSize, posOffset, uvOffset, rotateTex, originalQuad.getSprite()),
    				originalQuad.getTintIndex(), 
    				rotation.apply(originalQuad.getDirection()), 
    				originalQuad.getSprite(), 
    				originalQuad.isShade()
    				));
		}
		return bakedQuads;
	}

	public static List<BakedQuad> rotatedQuads(BlockState modelState, Function<BlockState, IBakedModel> model, BlockRotation rotation, boolean rotateTex, Direction side, Random rand, IModelData extraData) {
		IBakedModel originalModel = model.apply(modelState);
		IModelData modelData = BlockModelUtils.getModelData(modelState, extraData);
		return rotatedQuads(modelState, originalModel, rotation, rotateTex, side, rand, modelData);
	}
}