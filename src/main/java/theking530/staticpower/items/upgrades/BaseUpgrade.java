package theking530.staticpower.items.upgrades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.items.StaticPowerItem;

public class BaseUpgrade extends StaticPowerItem implements IUpgradeItem {

	private final ResourceLocation tier;
	private final Set<UpgradeType> upgradeTypes;

	BaseUpgrade(String name, ResourceLocation tier, Properties properties, UpgradeType... upgradeTypes) {
		super(name, properties);
		this.tier = tier;
		this.upgradeTypes = new HashSet<UpgradeType>();

		// If no upgrade type was supplied, then mark this as a special singular
		// upgrade.
		if (upgradeTypes.length == 0) {
			this.upgradeTypes.add(UpgradeType.SPECIAL);
		} else {
			for (UpgradeType type : upgradeTypes) {
				this.upgradeTypes.add(type);
			}
		}
	}

	BaseUpgrade(String name, ResourceLocation tier, UpgradeType... upgradeTypes) {
		this(name, tier, new Properties().maxStackSize(16), upgradeTypes);
	}

	@Override
	public StaticPowerTier getTier() {
		return StaticPowerDataRegistry.getTier(tier);
	}

	@Override
	public boolean isOfType(UpgradeType type) {
		return upgradeTypes.contains(type);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));
	}
}
