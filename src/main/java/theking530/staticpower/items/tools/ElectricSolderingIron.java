package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;

public class ElectricSolderingIron extends StaticPowerEnergyStoringItem implements ISolderingIron {

	public ElectricSolderingIron() {
	}

	@Override
	public boolean useSolderingItem(ItemStack itemstack) {
		// Should move to config, but 10SV per soldering operation.
		if (EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10) {
			EnergyHandlerItemStackUtilities.usePower(itemstack, 10, false);
		}
		return false;
	}

	@Override
	public boolean canSolder(ItemStack itemstack) {
		return EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		tooltip.add(new TextComponent("Power per Operation: 10SV"));
		if (showAdvanced) {
			double energyStored = EnergyHandlerItemStackUtilities.getStoredPower(stack);
			double capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
			tooltip.add(new TextComponent("Power Stored: ").append(StaticPowerEnergyTextUtilities.formatPowerToString(energyStored, capacity)));
		}
	}

	@Override
	public double getCapacity() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).portableBatteryCapacity.get();
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputCurrent() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).portableBatteryMaxOutputCurrent.get();
	}

	@Override
	public double getOutputVoltage() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).portableBatteryOutputVoltage.get();
	}
}
