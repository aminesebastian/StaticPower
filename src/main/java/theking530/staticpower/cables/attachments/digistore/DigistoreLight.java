package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreLight extends DigistoreScreen {

	public DigistoreLight() {
		super();
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cableComponent;
		return digistoreCable.isManagerPresent() ? StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON : StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT;
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 0.5;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TranslatableComponent("gui.staticpower.digistore_light"));
	}
}
