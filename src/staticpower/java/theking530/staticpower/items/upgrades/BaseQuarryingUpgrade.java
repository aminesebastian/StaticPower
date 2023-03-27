package theking530.staticpower.items.upgrades;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BaseQuarryingUpgrade extends BaseUpgrade {

	public BaseQuarryingUpgrade(ResourceLocation tier) {
		super(tier, new Properties().stacksTo(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		// tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Mines " +
		// getUpgradeValueAtIndex(stack, 0) + TextFormatting.GREEN + " Blocks Per
		// Tick"));
		// tooltip.add(new StringTextComponent(TextFormatting.WHITE + new
		// DecimalFormat("#").format((getUpgradeValueAtIndex(stack, 1)) * 100) + "%" +
		// TextFormatting.RED + " Power Use"));
	}
}
