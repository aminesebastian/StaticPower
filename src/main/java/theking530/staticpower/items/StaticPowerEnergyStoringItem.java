package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.item.ItemStackStaticPowerEnergyCapability;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.StaticPowerConfig;

/**
 * Light class for any static power items that require the ability to store
 * energy.
 * 
 * @author Amine Sebastian
 *
 */
public abstract class StaticPowerEnergyStoringItem extends StaticPowerItem {
	/**
	 * Creates a default energy storing item.
	 * 
	 * @param name     The registry name of the item sans namespace.
	 * @param capacity The amount of energy that can be stored by this item.
	 */
	public StaticPowerEnergyStoringItem() {
		super(new Item.Properties().stacksTo(1).setNoRepair());
	}

	/**
	 * Add the energy storage capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		if (StaticPowerConfig.SERVER_SPEC.isLoaded()) {
			double capacity = getCapacity();
			return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackStaticPowerEnergyCapability("default", stack, capacity, getInputVoltageRange(),
					getMaximumInputPower(), getOutputVoltage(), getMaximumOutputPower(), canAcceptExternalPower(), canOutputExternalPower()));
		}
		return null;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setStoredPower(output, Double.MAX_VALUE);
		return output;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		// Only show the animation if the stored power is the same (didn't change).
		// This is so we don't SPAM the animation on charge or discharge.
		return super.shouldCauseBlockBreakReset(oldStack, newStack)
				&& EnergyHandlerItemStackUtilities.getStoredPower(newStack) == EnergyHandlerItemStackUtilities.getStoredPower(oldStack);
	}

	public abstract double getCapacity();

	public abstract StaticVoltageRange getInputVoltageRange();

	public abstract double getMaximumInputPower();

	public abstract StaticPowerVoltage getOutputVoltage();

	public abstract double getMaximumOutputPower();

	public boolean canAcceptExternalPower() {
		return true;
	}

	public boolean canOutputExternalPower() {
		return false;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return EnergyHandlerItemStackUtilities.getStoredPower(stack) > 0;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return EnergyHandlerItemStackUtilities.getRGBDurabilityForDisplay(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		// Get the energy handler.
		ItemStackStaticPowerEnergyCapability handler = EnergyHandlerItemStackUtilities.getEnergyContainer(stack).orElse(null);
		if (handler == null) {
			return 0;
		}

		// Get the power ratio.
		return (int) (handler.getStoredPower() / (float) handler.getCapacity() * 13);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		double remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		double capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(PowerTextFormatting.formatPowerToString(remainingCharge, capacity));
		tooltip.add(PowerTextFormatting.formatVoltageRangeToString(EnergyHandlerItemStackUtilities.getInputVoltageRange(stack)));
	}

	public static class EnergyItemJEIInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack itemStack, UidContext context) {
			return ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString() + EnergyHandlerItemStackUtilities.getCapacity(itemStack) + " "
					+ EnergyHandlerItemStackUtilities.getStoredPower(itemStack);
		}
	}

}
