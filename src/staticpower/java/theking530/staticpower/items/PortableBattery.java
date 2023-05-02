package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.client.rendering.items.PortableBatteryItemModel;
import theking530.staticpower.init.ModCreativeTabs;

public class PortableBattery extends StaticPowerEnergyStoringItem implements ICustomModelProvider {
	public final ResourceLocation tier;

	public PortableBattery(ResourceLocation tier) {
		super(new Item.Properties().tab(ModCreativeTabs.TOOLS));
		this.tier = tier;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel,
			ModelEvent.BakingCompleted event) {
		return new PortableBatteryItemModel(existingModel);
	}

	@Override
	public double getCapacity() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryCapacity.get();
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	@Override
	public double getMaximumInputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	@Override
	public StaticPowerVoltage getOutputVoltage() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	@Override
	public double getMaximumOutputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}

	@Override
	public boolean canOutputExternalPower() {
		return true;
	}
}
