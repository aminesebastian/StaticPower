package theking530.staticcore.block;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.ITooltipProvider;

public class StaticCoreItemBlock extends BlockItem implements ITooltipProvider {
	protected final Block OWNING_BLOCK;

	public StaticCoreItemBlock(Block block, Item.Properties properties) {
		super(block, properties);
		OWNING_BLOCK = block;
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult superResult = super.place(context);
		if (superResult != InteractionResult.FAIL) {
			BlockPos blockpos = context.getClickedPos();
			Level level = context.getLevel();
			BlockState state = level.getBlockState(blockpos);
			Block block = state.getBlock();
			if (block instanceof StaticCoreBlock) {
				((StaticCoreBlock) block).onPlacedInWorld(context, level, blockpos, state, context.getPlayer(), context.getItemInHand());
			}
		}
		return superResult;
	}

	@Override
	public Component getName(ItemStack stack) {
		// Return early if an invalid owner is encountered.
		if (OWNING_BLOCK == null) {
			throw new RuntimeException("Invalid owning block encountered when getting the display name for a StaticPowerItemBlock.");
		}

		// Return early if the owning block is not an instance of a static power block.
		if (!(OWNING_BLOCK instanceof StaticCoreBlock)) {
			return Component.translatable(OWNING_BLOCK.getDescriptionId());
		}

		// Get the display name from the block.
		return ((StaticCoreBlock) OWNING_BLOCK).getDisplayName(stack);
	}

	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		// Return early if an invalid owner is encountered.
		if (OWNING_BLOCK == null) {
			throw new RuntimeException("Invalid owning block encountered when attempting to generate tooltips for StaticPowerItemBlock.");
		}

		// Return early if the owning block is not an instance of a static power block.
		if (!(OWNING_BLOCK instanceof ITooltipProvider)) {
			return;
		}

		// Get the block as a provider.
		ITooltipProvider provider = (ITooltipProvider) OWNING_BLOCK;
		provider.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		// Return early if an invalid owner is encountered.
		if (OWNING_BLOCK == null) {
			throw new RuntimeException("Invalid owning block encountered when attempting to generate tooltips for StaticPowerItemBlock.");
		}

		// Return early if the owning block is not an instance of a static power block.
		if (!(OWNING_BLOCK instanceof ITooltipProvider)) {
			return;
		}

		// Get the block as a provider.
		ITooltipProvider provider = (ITooltipProvider) OWNING_BLOCK;
		provider.getAdvancedTooltip(stack, worldIn, tooltip);
	}
}
