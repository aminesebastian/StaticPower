package theking530.staticpower.items.wirecoils;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;

public class PowerWireCoil extends AbstractWireCoil {

	public PowerWireCoil(SDColor wireColor, float wireThickness, boolean isInsulated, ResourceLocation tier, Supplier<CableNetworkModuleType> cableModuleType) {
		super(wireColor, wireThickness, isInsulated, tier, cableModuleType);
	}

	public double getPowerLoss(ItemStack wireStack) {
		return StaticPowerConfig.getTier(tier).cablePowerConfiguration.wireCoilPowerLossPerBlock.get();
	}

	public StaticPowerVoltage getMaxVoltage(ItemStack wireStack) {
		return StaticPowerConfig.getTier(tier).cablePowerConfiguration.wireCoilMaxVoltage.get();
	}

	public double getMaxPower(ItemStack wireStack) {
		return StaticPowerConfig.getTier(tier).cablePowerConfiguration.wireCoilMaxPower.get();
	}

	public boolean canApplyToTerminal(ItemStack coil, AbstractCableProviderComponent component) {
		return component.getSupportedNetworkModuleTypes().contains(cableModuleType.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		PowerTooltips.addMaximumPowerTransferTooltip(tooltip, getMaxPower(stack));
		PowerTooltips.addPowerLossPerBlockTooltip(tooltip, getPowerLoss(stack));
		PowerTooltips.addMaxVoltageTooltip(tooltip, getMaxVoltage(stack).getVoltage());
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
