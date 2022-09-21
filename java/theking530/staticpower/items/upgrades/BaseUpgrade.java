package theking530.staticpower.items.upgrades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.IUpgradeItem;
import theking530.api.upgrades.UpgradeType;
import theking530.api.upgrades.UpgradeTypes;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.items.StaticPowerItem;

public class BaseUpgrade extends StaticPowerItem implements IUpgradeItem {

	private final ResourceLocation tier;
	private final Set<UpgradeType> upgradeTypes;

	public BaseUpgrade(Properties properties, UpgradeType... upgradeTypes) {
		this(null, properties, upgradeTypes);
	}

	public BaseUpgrade(ResourceLocation tier, UpgradeType... upgradeTypes) {
		this(tier, new Properties().stacksTo(16), upgradeTypes);
	}

	public BaseUpgrade(ResourceLocation tier, Properties properties, UpgradeType... upgradeTypes) {
		super(properties);
		this.tier = tier;
		this.upgradeTypes = new HashSet<UpgradeType>();

		// If no upgrade type was supplied, then mark this as a special singular
		// upgrade.
		if (upgradeTypes.length == 0) {
			this.upgradeTypes.add(UpgradeTypes.SPECIAL);
		} else {
			for (UpgradeType type : upgradeTypes) {
				this.upgradeTypes.add(type);
			}
		}
	}

	public StaticPowerTier getTierObject() {
		return StaticPowerConfig.getTier(tier);
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
	public boolean isOfType(UpgradeType type) {
		return upgradeTypes.contains(type);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent(ChatFormatting.WHITE + "Stacks Up To " + stack.getMaxStackSize()));
	}

	@Override
	public boolean isTiered() {
		return tier != null;
	}
}
