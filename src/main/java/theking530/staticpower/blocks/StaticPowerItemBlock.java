package theking530.staticpower.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

public class StaticPowerItemBlock extends BlockItem {
	protected final Block OWNING_BLOCK;

	/**
	 * Creates a default BlockItem with a stack size of 64 and no chance to repair.
	 * 
	 * @param block The block this BlockItem represents.
	 * @param name  The registry name to use when registering this block item.
	 */
	public StaticPowerItemBlock(Block block) {
		super(block, new Item.Properties().maxStackSize(64).group(StaticPower.CREATIVE_TAB));
		OWNING_BLOCK = block;
		setRegistryName(block.getRegistryName());
	}

	/**
	 * Adds tooltips to the itemblock based on the owning block's tooltips.
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		// Return early if an invalid owner is encountered.
		if (OWNING_BLOCK == null) {
			StaticPower.LOGGER.info("Invalid owning block encountered when attempting to generate tooltips for StaticPowerItemBlock.");
			return;
		}

		// Return early if the owning block is not an instance of a static power block.
		if (!(OWNING_BLOCK instanceof StaticPowerBlock)) {
			return;
		}

		// Perform a cast if it is.
		StaticPowerBlock spBlock = (StaticPowerBlock) OWNING_BLOCK;

		// Get the basic tooltips.
		List<ITextComponent> basicTooltips = new ArrayList<ITextComponent>();
		spBlock.getBasicTooltip(stack, worldIn, basicTooltips);

		// Add the tooltips if any were requested.
		if (basicTooltips.size() > 0) {
			tooltip.addAll(basicTooltips);
		}

		// Get the advanced tooltips.
		List<ITextComponent> advancedToolTips = new ArrayList<ITextComponent>();
		spBlock.getAdvancedTooltip(stack, worldIn, advancedToolTips);

		// Add the advanced tooltips if any were requested.
		if (advancedToolTips.size() > 0) {
			// If shift is not held, indicate that the user should hold shift, otherwise add
			// the advanced tooltips.
			if (flagIn.isAdvanced()) {
				tooltip.addAll(advancedToolTips);
			} else {
				tooltip.add(new StringTextComponent("Hold Shift").applyTextStyle(TextFormatting.ITALIC));
			}
		}
	}
}
