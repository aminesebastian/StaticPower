package theking530.staticpower.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;

/**
 * Base class for most static power items.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerItem extends Item {
	/**
	 * The name of this item.
	 */
	public String NAME = "";

	/**
	 * Base constructor for a static power item. Uses the default item properties
	 * and adds the item to the static power default item group.
	 * 
	 * @param name The registry name for this item sans namespace.
	 */
	public StaticPowerItem(String name) {
		this(name, new Item.Properties().group(StaticPower.CREATIVE_TAB));
	}

	/**
	 * Constructor for a static power item that takes a custom properties object.
	 * 
	 * @param name       The registry name for this item sans namespace.
	 * @param properties The properties for this item (the item group is set within
	 *                   this method, no need to set it externally).
	 */
	public StaticPowerItem(String name, Item.Properties properties) {
		super(properties.group(StaticPower.CREATIVE_TAB));
		NAME = name;
		setRegistryName(name);
	}

	/**
	 * Override of onItemUse that calls an expanded version for easier
	 * implementation.
	 */
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		return onStaticPowerItemUsed(context, context.getWorld(), context.getPos(), context.getFace(), context.getPlayer(), context.getItem());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		// Get the basic tooltips.
		List<ITextComponent> basicTooltips = new ArrayList<ITextComponent>();
		getBasicTooltip(stack, worldIn, basicTooltips);

		// Add the tooltips if any were requested.
		if (basicTooltips.size() > 0) {
			tooltip.addAll(basicTooltips);
		}

		// Get the advanced tooltips.
		List<ITextComponent> advancedToolTips = new ArrayList<ITextComponent>();
		getAdvancedTooltip(stack, worldIn, advancedToolTips);

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

	/**
	 * Expanded version of onItemUse.
	 * 
	 * @param context The context of the item's use.
	 * @param world   The world the item was used in.
	 * @param pos     The position that the item was used on.
	 * @param face    The face of the block that the item was used on.
	 * @param player  The player that used the item.
	 * @param item    The item stack that was used.
	 * @return The result of the action (SUCCESS, PASS, FAIL, CONSUME).
	 */
	protected ActionResultType onStaticPowerItemUsed(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		return ActionResultType.PASS;
	}

	/**
	 * Gets the basic tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}

	/**
	 * Gets the advanced tooltip that is displayed when hovered by the user and they
	 * are holding shift.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}
}
