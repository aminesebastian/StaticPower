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

public class CraftingUpgrade extends BaseUpgrade {

	public CraftingUpgrade(String name) {
		super(name, new Properties().maxStackSize(1), UpgradeType.DIGISTORE_ATTACHMENT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "Allows digistore attachments"));
		tooltip.add(new StringTextComponent(TextFormatting.GREEN + "to craft items if needed."));
	}
}