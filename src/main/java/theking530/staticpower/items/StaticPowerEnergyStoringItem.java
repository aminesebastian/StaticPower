package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
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
	protected final int capacity;

	/**
	 * Creates a default energy storing item.
	 * 
	 * @param name     The registry name of the item sans namespace.
	 * @param capacity The amount of energy that can be stored by this item.
	 */
	public StaticPowerEnergyStoringItem(String name, int capacity) {
		super(name, new Item.Properties().maxStackSize(1).setNoRepair().maxDamage(capacity));
		this.capacity = capacity;
	}

	/**
	 * Add the energy storage capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilityProvider() {
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
				if (cap == CapabilityEnergy.ENERGY) {
					return LazyOptional.of(() -> new EnergyHandlerItemStack(stack, capacity, capacity, capacity)).cast();
				}
				return LazyOptional.empty();
			}
		};
	}

	/**
	 * Always shows the durability bar.
	 */
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		int remainingCharge = EnergyHandlerItemStackUtilities.getEnergyStored(stack);
		int capacity = EnergyHandlerItemStackUtilities.getEnergyStorageCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));
	}
}
