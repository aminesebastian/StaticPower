package theking530.staticpower.integration.WAILA;

import java.util.Optional;

import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent;

public class WailaDataProviders implements IServerDataProvider<TileEntity> {
	public static final String POWER_TAG = "power";
	public static final String FLUID_TAG = "fluid";
	public static final String HEAT_TAG = "heat";
	public static final String PROCESSING_TAG = "processing";
	public static final String DIGISTORE_MANAGER_TAG = "digistore_manager";

	@Override
	public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity te) {
		if (te == null) {
			return;
		}

		// Add fluid data.
		te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(tank -> {
			CompoundNBT fluidData = new CompoundNBT();
			fluidData.putDouble("value", tank.getFluidInTank(0).getAmount());
			fluidData.putDouble("max", tank.getTankCapacity(0));
			fluidData.putString("description",
					GuiTextUtilities.formatFluidToString(tank.getFluidInTank(0).getAmount()).appendString(" ").append(tank.getFluidInTank(0).getDisplayName()).getString());
			data.put(FLUID_TAG, fluidData);
		});

		// Add heat data.
		te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).ifPresent(heatStorage -> {
			CompoundNBT heatData = new CompoundNBT();
			heatData.putDouble("value", heatStorage.getCurrentHeat());
			heatData.putDouble("max", heatStorage.getMaximumHeat());
			heatData.putString("description", GuiTextUtilities.formatHeatToString(heatStorage.getCurrentHeat()).getString());
			data.put(HEAT_TAG, heatData);
		});

		// Add static volt data.
		te.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
			CompoundNBT powerData = new CompoundNBT();
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
			CompoundNBT processingData = new CompoundNBT();
			if (processing.get().isProcessing()) {
				processingData.putDouble("value", processing.get().getReminingTicks());
				processingData.putDouble("max", processing.get().getMaxProcessingTime());
				processingData.putString("description", GuiTextUtilities.formatNumberAsString(processing.get().getReminingTicks()).appendString(" ")
						.append(new TranslationTextComponent("gui.staticpower.ticks_remaining")).getString());
			} else {
				processingData.putDouble("value", 0);
				processingData.putDouble("max", 0);
				processingData.putString("description",
						GuiTextUtilities.formatNumberAsString(0).appendString(" ").append(new TranslationTextComponent("gui.staticpower.ticks_remaining")).getString());
			}
			data.put(PROCESSING_TAG, processingData);
		}
	}
}