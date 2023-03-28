package theking530.staticcore.item.compound;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.item.compound.capability.CapabilityCompoundItem;
import theking530.api.item.compound.capability.CompoundItemHandlerCapabilityProvider;
import theking530.api.item.compound.capability.CompoundItemHandlerCapabilityProvider.CompoundItemHandlerCapabilityBuilder;
import theking530.api.item.compound.capability.ICompoundItem;
import theking530.staticcore.item.StaticCoreItem;

public abstract class AbstractCompoundItem extends StaticCoreItem {
	public AbstractCompoundItem(Item.Properties properties) {
		super(properties.stacksTo(1));
	}

	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return createCompoundItemCapability(stack, nbt);
	}

	protected CompoundItemHandlerCapabilityProvider createCompoundItemCapability(ItemStack stack, @Nullable CompoundTag nbt) {
		CompoundItemHandlerCapabilityBuilder builder = new CompoundItemHandlerCapabilityBuilder();
		addSlots(builder, stack);

		CompoundItemHandlerCapabilityProvider output = builder.build(stack);
		if (nbt != null) {
			//output.deserializeNBT(nbt);
		}
		return output;
	}

	protected abstract void addSlots(CompoundItemHandlerCapabilityBuilder builder, ItemStack stack);

	public ICompoundItem getCompoundItemCapability(ItemStack stack) {
		return stack.getCapability(CapabilityCompoundItem.CAPABILITY_COMPOUND_ITEM).orElse(null);
	}

	public int getDamage(ItemStack stack) {
		ICompoundItem item = getCompoundItemCapability(stack);
		return item == null ? super.getDamage(stack) : item.getDamage();
	}

	public int getMaxDamage(ItemStack stack) {
		ICompoundItem item = getCompoundItemCapability(stack);
		return item == null ? super.getMaxDamage(stack) : item.getMaxDamage();
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		ICompoundItem item = getCompoundItemCapability(stack);
		return item == null ? false : item.isComplete();
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		int current = getDamage(stack);
		int max = getMaxDamage(stack);

		// Get the power ratio.
		return 13 - (int) (((double) current / max) * 13);
	}

	public int getBarColor(ItemStack stack) {
		float f = Math.max(0.0F, ((float) getMaxDamage(stack) - (float) getDamage(stack)) / (float) getMaxDamage(stack));
		return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
	}
}