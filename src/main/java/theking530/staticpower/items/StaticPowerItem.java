package theking530.staticpower.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

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
	 * Override of onItemRickClick that calls expanded version
	 * {@link #onStaticPowerItemRightClicked(World, PlayerEntity, Hand, ItemStack)}
	 * for easier implementation.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		return onStaticPowerItemRightClicked(worldIn, playerIn, handIn, playerIn.getHeldItem(handIn));
	}

	/**
	 * Override of onItemUse that calls expanded version
	 * {@link #onStaticPowerItemUsedOnBlock(ItemUseContext, World, BlockPos, Direction, PlayerEntity, ItemStack)}
	 * for easier implementation.
	 */
	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		super.onItemUse(context);
		return onStaticPowerItemUsedOnBlock(context, context.getWorld(), context.getPos(), context.getFace(), context.getPlayer(), context.getItem());
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return EnergyHandlerItemStackUtilities.getRGBDurabilityForDisplay(stack);
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
			// If sneak is not held, indicate that the user should hold sneak, otherwise add
			// the advanced tooltips.
			if (Screen.hasShiftDown()) {
				tooltip.addAll(advancedToolTips);
			} else {
				tooltip.add(new StringTextComponent("Hold Shift").mergeStyle(TextFormatting.ITALIC));
			}
		}
	}

	/**
	 * Expanded version of onItemRightClick.
	 * 
	 * @param world  The world the item was right clicked in.
	 * @param player The player holding the item.
	 * @param hand   The hand the item was held in.
	 * @param item   The {@link ItemStack}.
	 * @return The result of the action.
	 */
	protected ActionResult<ItemStack> onStaticPowerItemRightClicked(World world, PlayerEntity player, Hand hand, ItemStack item) {
		return ActionResult.resultPass(item);
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
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
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
