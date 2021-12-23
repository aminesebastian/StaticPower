package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockStraightConveyor extends AbstractConveyorBlock {
	public BlockStraightConveyor(String name) {
		super(name);
	}

	@Override
	public void cacheVoxelShapes() {
		for (Direction dir : Direction.values()) {
			ENTITY_SHAPES.put(dir, Block.box(0, 0, 0, 16, 8, 16));
			INTERACTION_SHAPES.put(dir, Block.box(0, 0, 0, 16, 8, 16));
		}
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return TileEntityStraightConveyor.TYPE.create();
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslatableComponent("gui.staticpower.experience_hopper_tooltip").withStyle(ChatFormatting.GREEN));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.experience_hopper_description")).withStyle(ChatFormatting.BLUE));
	}

}
