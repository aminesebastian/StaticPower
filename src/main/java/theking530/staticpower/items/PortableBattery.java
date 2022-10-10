package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.PortableBatteryItemModel;

public class PortableBattery extends StaticPowerEnergyStoringItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public PortableBattery(ResourceLocation tier) {
		this.tier = tier;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new PortableBatteryItemModel(existingModel);
	}

	@Override
	public double getCapacity() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryCapacity.get();
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticPowerConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}
}
