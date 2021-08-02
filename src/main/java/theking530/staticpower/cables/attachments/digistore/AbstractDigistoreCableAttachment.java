package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public abstract class AbstractDigistoreCableAttachment extends AbstractCableAttachment {

	public AbstractDigistoreCableAttachment(String name) {
		super(name);
	}

	public abstract long getPowerUsage(ItemStack attachment);

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
	}

	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		long powerUsage = getPowerUsage(stack);
		if (powerUsage > 0) {
			tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.digistore_attachment_power_usage").mergeStyle(TextFormatting.GREEN)
					.appendString(TextFormatting.WHITE.toString() + GuiTextUtilities.formatEnergyRateToString(powerUsage).getString())));
		}
	}
}
