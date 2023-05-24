package theking530.staticpower.cables.heat;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableModules;

public class HeatNetworkModule extends CableNetworkModule {

	public HeatNetworkModule() {
		super(ModCableModules.Heat.get());
	}

	@Override
	public void getReaderOutput(List<Component> components, BlockPos pos) {
		if (!Network.getGraph().getCables().containsKey(pos)) {
			return;
		}

		Optional<HeatCableCapability> capability = getHeatCableCapability(pos);
		if (capability.isEmpty()) {
			return;
		}

		IHeatStorage heatStorage = capability.get().getStorage();

		Cable cable = Network.getGraph().getCables().get(pos);
		Component currentHeat = GuiTextUtilities.formatHeatToString(heatStorage.getCurrentTemperature(),
				heatStorage.getOverheatTemperature());
		Component cooling = GuiTextUtilities.formatHeatRateToString(heatStorage.getTicker().getAverageCooledPerTick());
		Component heating = GuiTextUtilities.formatHeatRateToString(heatStorage.getTicker().getAverageHeatedPerTick());
		Component averageConductivity = GuiTextUtilities
				.formatHeatRateToString(cable.getDataTag().getFloat(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY));

		components.add(Component.literal(ChatFormatting.WHITE.toString()).append(Component.literal("Contains: "))
				.append(ChatFormatting.GRAY.toString()).append(currentHeat));
		components.add(Component.literal(ChatFormatting.RED.toString()).append(Component.literal("Heating: "))
				.append(ChatFormatting.GRAY.toString()).append(heating));
		components.add(Component.literal(ChatFormatting.BLUE.toString()).append(Component.literal("Cooling: "))
				.append(ChatFormatting.GRAY.toString()).append(cooling));
		components.add(Component.literal(ChatFormatting.AQUA.toString()).append(Component.literal("Conductivity: "))
				.append(ChatFormatting.GRAY.toString()).append(averageConductivity));
	}

	@Override
	public void tick(Level world) {

	}

	protected Optional<HeatCableCapability> getHeatCableCapability(BlockPos pos) {
		Cable cable = Network.getGraph().getCables().get(pos);
		if (cable == null) {
			return Optional.empty();
		}

		return cable.getCapability(ModCableCapabilities.Heat.get());
	}
}
