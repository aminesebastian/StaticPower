package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
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
		super(name, new Item.Properties().maxStackSize(1).setNoRepair());
		this.capacity = capacity;
	}

	/**
	 * Add the energy storage capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackStaticVoltCapability("default", stack, getCapacity(), getCapacity(), getCapacity()));
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getDurabilityForDisplay(stack) > 0.0f && getDurabilityForDisplay(stack) < 1.0f;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return EnergyHandlerItemStackUtilities.getRGBDurabilityForDisplay(stack);
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		// Get the energy handler.
		IStaticVoltHandler handler = EnergyHandlerItemStackUtilities.getEnergyContainer(stack).orElse(null);
		if (handler == null) {
			return 0.0f;
		}

		// Get the power ratio.
		return 1.0 - (float) handler.getStoredPower() / (float) handler.getCapacity();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		int remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		int capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));
	}

	public static class EnergyItemJEIInterpreter implements ISubtypeInterpreter {
		@Override
		public String apply(ItemStack itemStack) {
			return itemStack.getItem().getRegistryName().toString() + EnergyHandlerItemStackUtilities.getCapacity(itemStack) + " " + EnergyHandlerItemStackUtilities.getStoredPower(itemStack);
		}
	}

}
