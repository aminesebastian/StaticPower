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
import theking530.staticpower.data.StaticPowerTiers;

public class DigistoreVoidUpgrade extends BaseUpgrade {

	public DigistoreVoidUpgrade(String name) {
		super(name, StaticPowerTiers.BASIC, new Properties().maxStackSize(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Voids all excess items"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "entering a Digistore."));
	}
}
