package theking530.staticpower.blockentities.nonpowered.conveyors.rampdown;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockRampDownConveyor extends AbstractConveyorBlock {
	public BlockRampDownConveyor(ResourceLocation tier) {
		super(tier, Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).noOcclusion());
	}

	@Override
	public void cacheVoxelShapes() {
		if (INTERACTION_SHAPES.isEmpty() || ENTITY_SHAPES.isEmpty()) {
			for (Direction dir : Direction.values()) {
				INTERACTION_SHAPES.put(dir, generateSlantedBoundingBox(dir, 0.15f, 16f, -4.3f, 3.5f, 45f, false));
				ENTITY_SHAPES.put(dir, generateSlantedBoundingBox(dir, 1.0f, 16f, -3.5f, 3.5f, 45f, false));
			}
		}
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityRampDownConveyor.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityRampDownConveyor.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityRampDownConveyor.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityRampDownConveyor.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityRampDownConveyor.TYPE_LUMUM.create(pos, state);
		}
		return null;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		super.getAdvancedTooltip(stack, worldIn, tooltip);
	}
}