package theking530.staticpower.integration.WAILA;

import java.util.Optional;

import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent;

public class WailaDataProviders implements IServerDataProvider<BlockEntity> {
	public static final String POWER_TAG = "power";
	public static final String FLUID_TAG = "fluid";
	public static final String HEAT_TAG = "heat";
	public static final String PROCESSING_TAG = "processing";
	public static final String DIGISTORE_MANAGER_TAG = "digistore_manager";

	@Override
	public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity te) {
		if (te == null) {
			return;
		}

		// Add fluid data.
		te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(tank -> {
			CompoundTag fluidData = new CompoundTag();
			fluidData.putDouble("value", tank.getFluidInTank(0).getAmount());
			fluidData.putDouble("max", tank.getTankCapacity(0));
			fluidData.putString("description",
					GuiTextUtilities.formatFluidToString(tank.getFluidInTank(0).getAmount()).append(" ").append(tank.getFluidInTank(0).getDisplayName()).getString());
			data.put(FLUID_TAG, fluidData);
		});

		// Add heat data.
		te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).ifPresent(heatStorage -> {
			CompoundTag heatData = new CompoundTag();
			heatData.putDouble("value", heatStorage.getCurrentHeat());
			heatData.putDouble("max", heatStorage.getMaximumHeat());
			heatData.putString("description", GuiTextUtilities.formatHeatToString(heatStorage.getCurrentHeat()).getString());
			data.put(HEAT_TAG, heatData);
		});

		// Add static volt data.
		te.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
			CompoundTag powerData = new CompoundTag();
			powerData.putDouble("value", powerStorage.getStoredPower());
			powerData.putDouble("max", powerStorage.getCapacity());
			powerData.putString("description", GuiTextUtilities.formatEnergyToString(powerStorage.getStoredPower()).getString());
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
			if (processing.get().isProcessing()) {
				processingData.putDouble("value", processing.get().getReminingTicks());
				processingData.putDouble("max", processing.get().getMaxProcessingTime());
				processingData.putString("description", GuiTextUtilities.formatNumberAsString(processing.get().getReminingTicks()).append(" ")
						.append(new TranslatableComponent("gui.staticpower.ticks_remaining")).getString());
			} else {
				processingData.putDouble("value", 0);
				processingData.putDouble("max", 0);
				processingData.putString("description",
						GuiTextUtilities.formatNumberAsString(0).append(" ").append(new TranslatableComponent("gui.staticpower.ticks_remaining")).getString());
			}
			data.put(PROCESSING_TAG, processingData);
		}
	}
}