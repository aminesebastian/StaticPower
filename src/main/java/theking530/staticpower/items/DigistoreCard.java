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
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.CapabilityDigistoreInventory;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.IDigistoreInventory;
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
		return new DigistoreInventoryCapabilityProvider(stack, MAX_UNIQUE_ITEM_TYPES_PER_CARD, StaticPowerDataRegistry.getTier(tierType).getDigistoreCapacity(), nbt);
	}

	public static IDigistoreInventory getInventory(ItemStack stack) {
		return stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).orElseThrow(() -> new RuntimeException("Encounetered an extractor attachment without a valid filter inventory."));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Stores: ").appendText(String.valueOf(StaticPowerDataRegistry.getTier(tierType).getDigistoreCapacity() / 64)).appendText(" Stacks"));
		tooltip.add(new StringTextComponent("Max Types: ").appendText(String.valueOf(MAX_UNIQUE_ITEM_TYPES_PER_CARD)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		MetricConverter converter = new MetricConverter(getInventory(stack).getTotalContainedCount());
		tooltip.add(new StringTextComponent("Currently Stored: ").appendText(converter.getValueAsString(true)).appendText(" Items"));
		tooltip.add(new StringTextComponent("Currently Types: ").appendText(String.valueOf(getInventory(stack).getCurrentUniqueItemTypeCount())));
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return shouldGlow;
	}
}
