package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreLight extends DigistoreScreen {

	public DigistoreLight(String name) {
		super(name);
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cableComponent;
		return digistoreCable.isManagerPresent() ? StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON : StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT;
	}

	@Override
	public long getPowerUsage(ItemStack attachment, DigistoreCableProviderComponent cableComponent) {
		return 500;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslationTextComponent("gui.staticpower.digistore_light"));
	}
}
