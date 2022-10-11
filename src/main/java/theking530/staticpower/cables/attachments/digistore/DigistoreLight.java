package theking530.staticpower.cables.attachments.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.StaticPowerAdditionalModels;

public class DigistoreLight extends DigistoreScreen {

	public DigistoreLight() {
		super();
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		DigistoreCableProviderComponent digistoreCable = CableUtilities.getCableWrapperComponent(level, pos);
		return digistoreCable.isManagerPresent() ? StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON : StaticPowerAdditionalModels.CABLE_DIGISTORE_LIGHT_ATTACHMENT;
	}

	@Override
	public double getPowerUsage(ItemStack attachment) {
		return 0.5;
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.translatable("gui.staticpower.digistore_light"));
	}
}
