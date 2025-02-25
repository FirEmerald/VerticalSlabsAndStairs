package com.firemerald.additionalplacements.client;

import com.firemerald.additionalplacements.block.AdditionalPlacementBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdditionalBlockColor implements BlockColor
{
	@Override
	public int getColor(BlockState state, BlockAndTintGetter tintGetter, BlockPos pos, int i)
	{
		Block block = state.getBlock();
		if (block instanceof AdditionalPlacementBlock placement) return Minecraft.getInstance().getBlockColors().getColor(placement.getModelState(state), tintGetter, pos, i);
		else return -1;
	}
}