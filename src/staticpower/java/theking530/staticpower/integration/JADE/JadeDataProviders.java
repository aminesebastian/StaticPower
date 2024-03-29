
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
import theking530.api.energy.CurrentType;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.blockentity.components.control.processing.AbstractProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.Timer;
import theking530.staticcore.blockentity.components.multiblock.MultiblockComponent;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.fluid.BlockEntityFluidCable;

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
			fluidData.putString("description", GuiTextUtilities.formatFluidToString(tank.getFluidInTank(0).getAmount())
					.append(" ").append(tank.getFluidInTank(0).getDisplayName()).getString());
			data.put(FLUID_TAG, fluidData);
		});

		// Add heat data.
		te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).ifPresent(heatStorage -> {
			CompoundTag heatData = new CompoundTag();
			heatData.putFloat("value", heatStorage.getTemperature());
			heatData.putFloat("max", heatStorage.getMaximumTemperature());
			data.put(HEAT_TAG, heatData);
		});

		if (te instanceof BlockEntityFluidCable) {
			BlockEntityFluidCable fluidCable = (BlockEntityFluidCable) te;
			float pressure = fluidCable.fluidCableComponent.getFluidCapability().get().getHeadPressure();
			data.putFloat("head_pressure", pressure);
		}

		// Add static power data.
		te.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).ifPresent(powerStorage -> {
			CompoundTag powerData = new CompoundTag();
			powerData.putByte("output_voltage", (byte) powerStorage.getOutputVoltage().ordinal());

			powerData.put("voltage_range", powerStorage.getInputVoltageRange().serializeNBT());
			powerData.putDouble("stored_power", powerStorage.getStoredPower());
			powerData.putDouble("capacity", powerStorage.getCapacity());

			powerData.putBoolean("canAcceptExternalPower", powerStorage.canAcceptExternalPower());
			powerData.putBoolean("canOutputExternalPower", powerStorage.canOutputExternalPower());

			powerData.putBoolean("is_alternating", powerStorage.getOutputCurrentType() == CurrentType.ALTERNATING);

			data.put(POWER_TAG, powerData);
		});

		// Add digistore data.
		Optional<DigistoreCableProviderComponent> digistoreComponent = ComponentUtilities
				.getComponent(DigistoreCableProviderComponent.class, te);
		if (digistoreComponent.isPresent()) {
			data.putBoolean(DIGISTORE_MANAGER_TAG, digistoreComponent.get().isManagerPresent());
		}

		// Add processing data.
		@SuppressWarnings("rawtypes")
		Optional<AbstractProcessingComponent> processing = ComponentUtilities
				.getComponent(AbstractProcessingComponent.class, te);

		if (!processing.isPresent()) {

			@SuppressWarnings("rawtypes")
			Optional<MultiblockComponent> multiblockComp = ComponentUtilities.getComponent(MultiblockComponent.class,
					te);
			if (multiblockComp.isPresent() && multiblockComp.get().isWellFormed()) {
				BlockEntity masterBe = world.getBlockEntity(multiblockComp.get().getMasterPosition());
				if (masterBe != null) {
					processing = ComponentUtilities.getComponent(AbstractProcessingComponent.class, masterBe);
				}
			}
		}

		if (processing.isPresent()) {
			CompoundTag processingData = new CompoundTag();
			if (processing.get().hasProcessingStarted()) {
				Timer processingTimer = processing.get().getProcessingTimer();
				int remaining = processingTimer.getMaxTime() - processingTimer.getCurrentTime();
				processingData.putInt("remaining", remaining);
				processingData.putInt("max", processingTimer.getMaxTime());
				processingData.putString("description", GuiTextUtilities.formatNumberAsStringNoDecimal(remaining)
						.append(" ").append(Component.translatable("gui.staticcore.ticks_remaining")).getString());
			} else {
				processingData.putInt("remaining", 0);
				processingData.putInt("max", 0);
				processingData.putString("description", GuiTextUtilities.formatNumberAsStringNoDecimal(0).append(" ")
						.append(Component.translatable("gui.staticcore.ticks_remaining")).getString());
			}
			data.put(PROCESSING_TAG, processingData);
		}
	}

	@Override
	public ResourceLocation getUid() {
		return new ResourceLocation(StaticPower.MOD_ID, "jade_data_provider");
	}
}