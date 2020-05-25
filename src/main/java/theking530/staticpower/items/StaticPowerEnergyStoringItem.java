package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Base class for any static power items that require the ability to store
 * energy.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerEnergyStoringItem extends StaticPowerItem {
	/** The INITIAL maximum amount of energy that can be stored by this item. */
	protected final int capacity;

	public StaticPowerEnergyStoringItem(String name, int capacity) {
		super(name, new Item.Properties().maxStackSize(1).setNoRepair().maxDamage(capacity));
		this.capacity = capacity;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new EnergyHandlerItemStack(stack, capacity, capacity, capacity);
	}

	@Override
	public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
		EnergyHandlerItemStack.setEnergy(stack, 0);
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
}
