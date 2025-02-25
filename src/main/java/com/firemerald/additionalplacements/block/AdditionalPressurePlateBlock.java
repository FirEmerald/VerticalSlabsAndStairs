package com.firemerald.additionalplacements.block;

import com.firemerald.additionalplacements.block.interfaces.IPressurePlateBlock;
import com.firemerald.additionalplacements.client.models.definitions.PressurePlateModels;
import com.firemerald.additionalplacements.client.models.definitions.StateModelDefinition;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AdditionalPressurePlateBlock extends AdditionalBasePressurePlateBlock<PressurePlateBlock> implements IPressurePlateBlock<PressurePlateBlock>
{
	public static AdditionalPressurePlateBlock of(PressurePlateBlock plate, ResourceKey<Block> id)
	{
		return new AdditionalPressurePlateBlock(plate, id);
	}

	private AdditionalPressurePlateBlock(PressurePlateBlock plate, ResourceKey<Block> id)
	{
		super(plate, id);
	}

	@Override
	protected int getSignalStrength(Level level, BlockPos pos)
	{
		Class<? extends Entity> oclass1;
		switch (this.parentBlock.type.pressurePlateSensitivity()) {
		case EVERYTHING:
			oclass1 = Entity.class;
			break;
		case MOBS:
			oclass1 = LivingEntity.class;
			break;
		default:
			throw new IncompatibleClassChangeError();
		}

		Class<? extends Entity> oclass = oclass1;
		return BasePressurePlateBlock.getEntityCount(level, TOUCH_AABBS[level.getBlockState(pos).getValue(AdditionalFloorBlock.PLACING).ordinal() - 1].move(pos), oclass) > 0 ? 15 : 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public StateModelDefinition getModelDefinition(BlockState state) {
		return PressurePlateModels.getPressurePlateModel(state);
	}
}