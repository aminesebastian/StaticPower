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

public class DigistoreVoidUpgrade extends BaseUpgrade {

	public DigistoreVoidUpgrade(String name) {
		super(name, new Properties().maxStackSize(1));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Voids all excess items"));
		tooltip.add(new StringTextComponent(TextFormatting.WHITE + "entering a Digistore."));
	}
}
