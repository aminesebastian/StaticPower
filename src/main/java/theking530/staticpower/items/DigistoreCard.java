package theking530.staticpower.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.DigistoreInventoryCapabilityProvider;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.utilities.MetricConverter;

public class DigistoreCard extends StaticPowerItem {
	public static final int MAX_UNIQUE_ITEM_TYPES_PER_CARD = 64;
	public final ResourceLocation tierType;
	public final ResourceLocation model;
	private final boolean shouldGlow;

	public DigistoreCard(String name, ResourceLocation tierType, ResourceLocation model) {
		this(name, tierType, model, false);
	}

	public DigistoreCard(String name, ResourceLocation tierType, ResourceLocation model, boolean shouldGlow) {
		super(name, new Item.Properties().maxStackSize(1));
		this.tierType = tierType;
		this.model = model;
		this.shouldGlow = shouldGlow;
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new DigistoreInventoryCapabilityProvider(stack, MAX_UNIQUE_ITEM_TYPES_PER_CARD, TierReloadListener.getTier(tierType).getDigistoreCapacity(), nbt);
	}

	public static IDigistoreInventory getInventory(ItemStack stack) {
		return stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY)
				.orElseThrow(() -> new RuntimeException("Encounetered a digistore card without an attached digistore inventory."));
	}

	@Nullable
	@Override
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT nbt = stack.getOrCreateTag();
		stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).ifPresent(inventory -> {
			nbt.put("digistore_inventory", inventory.serializeNBT());
		});
		return nbt;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		super.readShareTag(stack, nbt);

		if (nbt != null && nbt.contains("digistore_inventory")) {
			stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).ifPresent(inventory -> {
				inventory.deserializeNBT((CompoundNBT) nbt.get("digistore_inventory"));
			});
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent("Stores: ").appendString(String.valueOf(getInventory(stack).getItemCapacity() / 64)).appendString(" Stacks"));
		tooltip.add(new StringTextComponent("Max Types: ").appendString(String.valueOf(getInventory(stack).getUniqueItemCapacity())));

		if (showAdvanced) {
			int storedAmount = getInventory(stack).getTotalContainedCount();
			float filledPercentage = (float) storedAmount / getInventory(stack).getItemCapacity();
			MetricConverter converter = new MetricConverter(storedAmount);
			tooltip.add(new StringTextComponent("Stored: ").appendString(converter.getValueAsString(true)).appendString(" Items (")
					.appendString(String.valueOf((int) (100 * filledPercentage))).appendString("%)"));
			tooltip.add(new StringTextComponent("Types: ").appendString(String.valueOf(getInventory(stack).getCurrentUniqueItemTypeCount())));
		}
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return shouldGlow;
	}
}
