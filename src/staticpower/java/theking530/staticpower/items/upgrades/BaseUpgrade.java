package theking530.staticpower.items.upgrades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.IUpgradeItem;
import theking530.api.upgrades.IUpgradeSupplier;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.items.StaticPowerItem;

public class BaseUpgrade extends StaticPowerItem implements IUpgradeItem {

	private final ResourceLocation tier;
	private final Map<Supplier<? extends UpgradeType<?>>, IUpgradeSupplier<?>> upgradeTypes;

	public BaseUpgrade(Properties properties) {
		this(null, properties);
	}

	public BaseUpgrade(ResourceLocation tier) {
		this(tier, new Properties().stacksTo(16));
	}

	public BaseUpgrade(ResourceLocation tier, Properties properties) {
		super(properties.tab(ModCreativeTabs.UPGRADES));
		this.tier = tier;
		this.upgradeTypes = new HashMap<>();
	}

	public StaticCoreTier getTierObject() {
		return StaticCoreConfig.getTier(tier);
	}

	@Override
	public ResourceLocation getTier() {
		if (isTiered()) {
			return tier;
		} else {
			throw new RuntimeException("Attempted to get the tier of a non-tiered ugprade!");
		}
	}

	@Override
	public boolean isOfType(ItemStack upgradeStack, UpgradeType<?> type) {
		return upgradeTypes.keySet().stream().filter((x) -> x.get() == type).findAny().isPresent();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.WHITE + "Stacks Up To " + stack.getMaxStackSize()));
	}

	@Override
	public boolean isTiered() {
		return tier != null;
	}

	protected <T> BaseUpgrade addUpgrade(Supplier<UpgradeType<T>> type, IUpgradeSupplier<T> supplier) {
		upgradeTypes.put(type, supplier);
		return this;
	}

	@Override
	public <T> T getUpgradeValue(ItemStack upgradeStack, UpgradeType<T> type) {
		Optional<Entry<Supplier<? extends UpgradeType<?>>, IUpgradeSupplier<?>>> entry = upgradeTypes.entrySet().stream().filter((x) -> x.getKey().get() == type)
				.findFirst();
		if (entry.isEmpty()) {
			return null;
		}

		@SuppressWarnings("unchecked")
		IUpgradeSupplier<T> supplier = (IUpgradeSupplier<T>) entry.get().getValue();
		return supplier.apply(type, upgradeStack);
	}
}
