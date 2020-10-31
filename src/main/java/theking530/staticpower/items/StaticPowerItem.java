package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;

/**
 * Base class for most static power items.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerItem extends Item implements ITooltipProvider {
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

	@Override
	@Nullable
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT output = stack.serializeNBT();
		return output;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		stack.deserializeNBT(nbt);
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
		return super.getHarvestLevel(stack, tool, player, blockState);
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
	 * Gets the tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack             The item stack hovered by the user.
	 * @param worldIn           The world the player was in when hovering the item.
	 * @param tooltip           The list of {@link ITextComponent} to add to the
	 *                          tooltip.
	 * @param isShowingAdvanced True if advanced tooltips are requested.
	 */
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}

	@SuppressWarnings("deprecation")
	public boolean removeEnchantment(ItemStack stack, Enchantment enchantment) {
		stack.getOrCreateTag();
		if (!stack.getTag().contains("Enchantments", 9)) {
			stack.getTag().put("Enchantments", new ListNBT());
		}

		ListNBT listnbt = stack.getTag().getList("Enchantments", 10);
		boolean removedFlag = false;
		for (int i = listnbt.size() - 1; i >= 0; i--) {
			CompoundNBT enchantmentNbt = (CompoundNBT) listnbt.get(i);
			if (enchantmentNbt.getString("id").equals(Registry.ENCHANTMENT.getKey(enchantment).toString())) {
				listnbt.remove(i);
				removedFlag = true;
			}
		}
		return removedFlag;
	}
}
