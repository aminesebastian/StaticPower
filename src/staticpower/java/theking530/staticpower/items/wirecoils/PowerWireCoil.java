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
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.utilities.SDColor;

public class PowerWireCoil extends AbstractWireCoil {

	public PowerWireCoil(SDColor wireColor, float wireThickness, boolean isInsulated, ResourceLocation tier, Supplier<CableNetworkModuleType> cableModuleType) {
		super(wireColor, wireThickness, isInsulated, tier, cableModuleType);
	}

	public double getPowerLoss(ItemStack wireStack) {
		return StaticCoreConfig.getTier(tier).cablePowerConfiguration.wireCoilPowerLossPerBlock.get();
	}

	public double getMaxCurrent(ItemStack wireStack) {
		return StaticCoreConfig.getTier(tier).cablePowerConfiguration.wireCoilMaxCurrent.get();
	}

	public boolean canApplyToTerminal(ItemStack coil, AbstractCableProviderComponent component) {
		return component.getSupportedNetworkModuleTypes().contains(cableModuleType.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		PowerTooltips.addMaximumCurrentTooltip(tooltip, getMaxCurrent(stack));
		PowerTooltips.addResistanceTooltip(tooltip, getPowerLoss(stack));
		super.getTooltip(stack, worldIn, tooltip, showAdvanced);
	}
}
