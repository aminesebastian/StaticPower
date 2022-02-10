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
import theking530.api.power.IStaticVoltHandler;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

/**
 * Light class for any static power items that require the ability to store
 * energy.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerEnergyStoringItem extends StaticPowerItem {
	/** The INITIAL maximum amount of energy that can be stored by this item. */
	private int capacity;

	/**
	 * Creates a default energy storing item.
	 * 
	 * @param name     The registry name of the item sans namespace.
	 * @param capacity The amount of energy that can be stored by this item.
	 */
	public StaticPowerEnergyStoringItem(String name, int capacity) {
		super(name, new Item.Properties().stacksTo(1).setNoRepair());
		this.capacity = capacity;
	}

	/**
	 * Add the energy storage capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		long capacity = getCapacity();
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackStaticVoltCapability("default", stack, capacity, capacity, capacity));
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	public long getCapacity() {
		return capacity;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getBarWidth(stack) != 0;
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return EnergyHandlerItemStackUtilities.getRGBDurabilityForDisplay(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		// Get the energy handler.
		IStaticVoltHandler handler = EnergyHandlerItemStackUtilities.getEnergyContainer(stack).orElse(null);
		if (handler == null) {
			return 0;
		}

		// Get the power ratio.
		return (int) ((float) handler.getStoredPower() / (float) handler.getCapacity() * 13);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		long remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));
	}

	public static class EnergyItemJEIInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack itemStack, UidContext context) {
			return itemStack.getItem().getRegistryName().toString() + EnergyHandlerItemStackUtilities.getCapacity(itemStack) + " " + EnergyHandlerItemStackUtilities.getStoredPower(itemStack);
		}
	}

}
