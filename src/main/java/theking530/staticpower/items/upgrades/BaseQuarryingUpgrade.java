package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseQuarryingUpgrade extends BaseUpgrade {

	public BaseQuarryingUpgrade(String name, ResourceLocation tier) {
		super(name, tier, new Properties().maxStackSize(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		// tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Mines " +
		// getUpgradeValueAtIndex(stack, 0) + TextFormatting.GREEN + " Blocks Per
		// Tick"));
		// tooltip.add(new StringTextComponent(TextFormatting.WHITE + new
		// DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 1)) * 100) + "%" +
		// TextFormatting.RED + " Power Use"));
	}
}
