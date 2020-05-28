package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.Tier;

public class BaseUpgrade extends StaticPowerItem implements IMachineUpgrade {
	private final Tier TIER;

	BaseUpgrade(String name, Tier tier, Properties properties) {
		super(name, properties);
		TIER = tier;
	}

	BaseUpgrade(String name, Tier tier) {
		this(name, tier, new Properties().maxStackSize(16));
	}

	@Override
	public float getUpgradeValueAtIndex(ItemStack stack, int upgradeNumber) {
		return 0;
	}

	@Override
	public int getValueMultiplied(int value, float multiplier) {
		return (int) (value + (multiplier * (float) value));
	}

	@Override
	public float getValueMultiplied(float value, float multiplier) {
		return value + (multiplier * value);
	}

	public Tier getTier() {
		return TIER;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize()));
	}
}
