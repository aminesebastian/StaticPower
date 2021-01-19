package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;

public class StaticPowerItemBlock extends BlockItem implements ITooltipProvider {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerItemBlock.class);
	protected final Block OWNING_BLOCK;

	/**
	 * Creates a default BlockItem with a stack size of 64 and no chance to repair.
	 * 
	 * @param block The block this BlockItem represents.
	 * @param name  The registry name to use when registering this block item.
	 */
	public StaticPowerItemBlock(Block block) {
		this(block, new Item.Properties());
	}

	public StaticPowerItemBlock(Block block, Item.Properties properties) {
		super(block, properties.maxStackSize(64).group(StaticPower.CREATIVE_TAB));
		OWNING_BLOCK = block;
		setRegistryName(block.getRegistryName());
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		// Return early if an invalid owner is encountered.
		if (OWNING_BLOCK == null) {
			throw new RuntimeException("Invalid owning block encountered when getting the display name for a StaticPowerItemBlock.");
		}

		// Return early if the owning block is not an instance of a static power block.
		if (!(OWNING_BLOCK instanceof StaticPowerBlock)) {
			return new TranslationTextComponent(OWNING_BLOCK.getTranslationKey());
		}

		// Get the display name from the block.
		return ((StaticPowerBlock) OWNING_BLOCK).getDisplayName(stack);
	}

	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
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
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
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
