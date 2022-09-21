package theking530.staticpower.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import theking530.staticcore.utilities.SDMath;

public class JuiceBottleItem extends StaticPowerItem {
	public final int drinkDuration;
	public final int foodAmount;
	public final float saturation;

	public JuiceBottleItem(int drinkDuration, int foodAmount, float saturation) {
		super(new Item.Properties().stacksTo(16));
		this.drinkDuration = drinkDuration;
		this.foodAmount = foodAmount;
		this.saturation = saturation;
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not
	 * called when the player stops using the Item before the action is complete.
	 */
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
		super.finishUsingItem(stack, worldIn, entityLiving);

		if (entityLiving instanceof ServerPlayer) {
			ServerPlayer serverplayerentity = (ServerPlayer) entityLiving;
			CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
			serverplayerentity.awardStat(Stats.ITEM_USED.get(this));

			int newFoodAmount = SDMath.clamp(0, serverplayerentity.getFoodData().getFoodLevel() + foodAmount, 20);
			serverplayerentity.getFoodData().setFoodLevel(newFoodAmount);

			float newSaturationAmount = SDMath.clamp(0.0f, serverplayerentity.getFoodData().getSaturationLevel() + saturation, 20.0f);
			serverplayerentity.getFoodData().setSaturation(newSaturationAmount);
		}

		if (stack.isEmpty()) {
			return new ItemStack(Items.GLASS_BOTTLE);
		} else {
			if (entityLiving instanceof Player && !((Player) entityLiving).getAbilities().instabuild) {
				ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
				Player playerentity = (Player) entityLiving;
				if (!playerentity.getInventory().add(itemstack)) {
					playerentity.drop(itemstack, false);
				}
			}

			return stack;
		}
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getUseDuration(ItemStack stack) {
		return drinkDuration;
	}

	/**
	 * returns the action that specifies what animation to play when the items is
	 * being used
	 */
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	public SoundEvent getDrinkingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	public SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when
	 * this item is used on a Block, see {@link #onItemUse}.
	 */
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		playerIn.startUsingItem(handIn);
		return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
	}
}