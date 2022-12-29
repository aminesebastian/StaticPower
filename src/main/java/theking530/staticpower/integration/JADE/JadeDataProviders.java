
package theking530.staticpower.integration.JADE;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import snownee.jade.api.IServerDataProvider;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.components.ComponentUtilities;
import theking530.staticpower.blockentities.components.control.processing.AbstractProcesingComponent;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable;
import theking530.staticpower.client.utilities.GuiTextUtilities;

public class JadeDataProviders implements IServerDataProvider<BlockEntity> {
	public static final String POWER_TAG = "power";
	public static final String DEP_POWER_TAG = "dep_power";
	public static final String FLUID_TAG = "fluid";
	public static final String HEAT_TAG = "heat";
	public static final String PROCESSING_TAG = "processing";
	public static final String DIGISTORE_MANAGER_TAG = "digistore_manager";

	@Override
	public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity te, boolean advanced) {
		if (te == null) {
			return;
		}

		// Add fluid data.
		te.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(tank -> {
			CompoundTag fluidData = new CompoundTag();
			fluidData.putInt("value", tank.getFluidInTank(0).getAmount());
			fluidData.putInt("max", tank.getTankCapacity(0));
			fluidData.putString("description",
					GuiTextUtilities.formatFluidToString(tank.getFluidInTank(0).getAmount()).append(" ").append(tank.getFluidInTank(0).getDisplayName()).getString());
			data.put(FLUID_TAG, fluidData);
		});

		// Add heat data.
		te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).ifPresent(heatStorage -> {
			CompoundTag heatData = new CompoundTag();
			heatData.putInt("value", heatStorage.getCurrentHeat());
			heatData.putInt("max", heatStorage.getMaximumHeat());
			data.put(HEAT_TAG, heatData);
		});

		if (te instanceof BlockEntityFluidCable) {
			BlockEntityFluidCable fluidCable = (BlockEntityFluidCable) te;
			float pressure = fluidCable.fluidCableComponent.getFluidCapability().get().getPressure();
			data.putFloat("pressure", pressure);
		}

		// Add static power data.
		te.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
			CompoundTag powerData = new CompoundTag();
			powerData.putDouble("output_voltage", powerStorage.getOutputVoltage());

			powerData.put("voltage_range", powerStorage.getInputVoltageRange().serializeNBT());
			powerData.putDouble("stored_power", powerStorage.getStoredPower());
			powerData.putDouble("capacity", powerStorage.getCapacity());

			powerData.putBoolean("canAcceptExternalPower", powerStorage.canAcceptExternalPower());
			powerData.putBoolean("canOutputExternalPower", powerStorage.canOutputExternalPower());

			data.put(POWER_TAG, powerData);
		});

		// Add digistore data.
		Optional<DigistoreCableProviderComponent> digistoreComponent = ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, te);
		if (digistoreComponent.isPresent()) {
			data.putBoolean(DIGISTORE_MANAGER_TAG, digistoreComponent.get().isManagerPresent());
		}

		// Add processing data.
		Optional<AbstractProcesingComponent> processing = ComponentUtilities.getComponent(AbstractProcesingComponent.class, te);
		if (processing.isPresent()) {
			CompoundTag processingData = new CompoundTag();
			if (processing.get().hasProcessingStarted()) {
				int remaining = processing.get().getMaxProcessingTime() - processing.get().getCurrentProcessingTime();
				processingData.putInt("remaining", remaining);
				processingData.putInt("max", processing.get().getMaxProcessingTime());
				processingData.putString("description",
						GuiTextUtilities.formatNumberAsString(remaining).append(" ").append(Component.translatable("gui.staticpower.ticks_remaining")).getString());
			} else {
				processingData.putInt("remaining", 0);
				processingData.putInt("max", 0);
				processingData.putString("description",
						GuiTextUtilities.formatNumberAsString(0).append(" ").append(Component.translatable("gui.staticpower.ticks_remaining")).getString());
			}
			data.put(PROCESSING_TAG, processingData);
		}
	}

	@Override
	public ResourceLocation getUid() {
		return new ResourceLocation(StaticPower.MOD_ID, "jade_data_provider");
	}
}