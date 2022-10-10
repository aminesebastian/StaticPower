package theking530.staticpower.items.tools;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.ISolderingIron;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;

public class ElectricSolderingIron extends StaticPowerEnergyStoringItem implements ISolderingIron {

	public ElectricSolderingIron() {
	}

	@Override
	public boolean useSolderingItem(Level level, ItemStack itemstack) {
		// Should move to config, but 10SV per soldering operation.
		if (EnergyHandlerItemStackUtilities.getStoredPower(itemstack) >= 10) {
			EnergyHandlerItemStackUtilities.drainPower(itemstack, 10, false);
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
		tooltip.add(Component.literal("Power per Operation: 10SV"));
		if (showAdvanced) {
			double energyStored = EnergyHandlerItemStackUtilities.getStoredPower(stack);
			double capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
			tooltip.add(Component.literal("Power Stored: ").append(PowerTextFormatting.formatPowerToString(energyStored, capacity)));
		}
	}

	@Override
	public double getCapacity() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryCapacity.get();
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticPowerConfig.getTier(StaticPowerTiers.ADVANCED).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}
}
