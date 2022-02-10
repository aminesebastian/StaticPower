package theking530.staticpower.integration.JADE;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.api.ui.IProgressStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.IHeatStorage;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.digistorenetwork.digistore.BlockDigistore;

@WailaPlugin(StaticPower.MOD_ID)
public class JadePluginImplementation implements IWailaPlugin {
	public static final ResourceLocation POWER_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "power_bar");
	public static final ResourceLocation HEAT_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "heat_bar");
	public static final ResourceLocation PROCESSING_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "processing_bar");
	public static final ResourceLocation FLUID_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "fluid_bar");

	public static final Color MAIN_SV_COLOR = new Color(0, 0.6f, 0.8f).fromFloatToEightBit();
	public static final Color ALT_SV_COLOR = new Color(0, 0.458f, 1.0f).fromFloatToEightBit();

	public static final Color MAIN_HEAT_COLOR = new Color(1.0f, 0.517f, 0.f).fromFloatToEightBit();
	public static final Color ALT_HEAT_COLOR = new Color(1.0f, 0.615f, 0.f).fromFloatToEightBit();

	public static final Color MAIN_PROCESSING_COLOR = new Color(0.67f, 0.67f, 0.67f).fromFloatToEightBit();
	public static final Color ALT_PROCESSING_COLOR = new Color(0.67f, 0.67f, 0.67f).fromFloatToEightBit();

	public static final Color MAIN_FLUID_COLOR = new Color(0, 0.18f, 0.88f).fromFloatToEightBit();
	public static final Color ALT_FLUID_COLOR = new Color(0, 0.164f, 0.831f).fromFloatToEightBit();

	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(new DigistoreMasterPresenceDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new DigistoreDecorator(), TooltipPosition.BODY, BlockDigistore.class);
		registrar.registerBlockDataProvider(new JadeDataProviders(), BlockEntity.class);
		registrar.registerComponentProvider(new StaticVoltDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new HeatDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new ProcessingTimeDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new FluidDecorator(), TooltipPosition.BODY, Block.class);
	}

	public static class StaticVoltDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BlockEntity tile = accessor.getBlockEntity();
			if (tile != null) {
				IStaticVoltHandler storage = tile.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY).orElse(null);
				if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains(JadeDataProviders.POWER_TAG))) {
					long stored, capacity;
					if (accessor.isServerConnected()) {
						CompoundTag svData = accessor.getServerData().getCompound(JadeDataProviders.POWER_TAG);
						stored = svData.getLong("value");
						capacity = svData.getLong("max");
					} else {
						stored = storage.getStoredPower();
						capacity = storage.getCapacity();
					}

					// Draw bar
					JadePluginImplementation.drawBar(tooltip, stored, capacity, MAIN_SV_COLOR, ALT_SV_COLOR, GuiTextUtilities.formatEnergyToString(stored, capacity), POWER_BAR_RENDERER);
				}
			}
		}
	}

	public static class HeatDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BlockEntity tile = accessor.getBlockEntity();
			if (tile != null) {
				IHeatStorage storage = tile.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).orElse(null);
				if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains(JadeDataProviders.HEAT_TAG))) {
					double stored, capacity;
					if (accessor.isServerConnected()) {
						CompoundTag heatData = accessor.getServerData().getCompound(JadeDataProviders.HEAT_TAG);
						stored = heatData.getDouble("value");
						capacity = heatData.getDouble("max");
					} else {
						stored = storage.getCurrentHeat();
						capacity = storage.getMaximumHeat();
					}

					// Draw bar
					JadePluginImplementation.drawBar(tooltip, stored, capacity, MAIN_HEAT_COLOR, ALT_FLUID_COLOR, GuiTextUtilities.formatHeatToString(stored, capacity), HEAT_BAR_RENDERER);
				}
			}
		}
	}

	public static class ProcessingTimeDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BlockEntity tile = accessor.getBlockEntity();
			if (tile != null) {
				if ((accessor.isServerConnected() && accessor.getServerData().contains(JadeDataProviders.PROCESSING_TAG))) {
					CompoundTag processingData = accessor.getServerData().getCompound(JadeDataProviders.PROCESSING_TAG);
					double current = processingData.getDouble("value");
					double totalTime = processingData.getDouble("max");

					// Draw bar
					if (current > 0) {
						JadePluginImplementation.drawBar(tooltip, current, totalTime, MAIN_PROCESSING_COLOR, ALT_PROCESSING_COLOR, new TextComponent(processingData.getString("description)")),
								PROCESSING_BAR_RENDERER);
					}
				}
			}
		}
	}

	public static class FluidDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
//			BlockEntity tile = accessor.getBlockEntity();
//			if (tile != null) {
//				IHeatStorage storage = tile.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).orElse(null);
//				if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains(JadeDataProviders.HEAT_TAG))) {
//					double stored, capacity;
//					if (accessor.isServerConnected()) {
//						CompoundTag heatData = accessor.getServerData().getCompound(JadeDataProviders.HEAT_TAG);
//						stored = heatData.getDouble("value");
//						capacity = heatData.getDouble("max");
//					} else {
//						stored = storage.getCurrentHeat();
//						capacity = storage.getMaximumHeat();
//					}
//
//					// Draw bar
//					JadePluginImplementation.drawBar(tooltip, stored, capacity, MAIN_FLUID_COLOR, ALT_HEAT_COLOR, GuiTextUtilities.formatHeatToString(stored, capacity), HEAT_BAR_RENDERER);
//				}
//			}
		}
	}

	public static class DigistoreDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {

		}
	}

	public static class DigistoreMasterPresenceDecorator implements IComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(JadeDataProviders.DIGISTORE_MANAGER_TAG)) {
				boolean managerPresent = accessor.getServerData().getBoolean(JadeDataProviders.DIGISTORE_MANAGER_TAG);
				if (managerPresent) {
					tooltip.add(new TextComponent(ChatFormatting.GREEN.toString()).append(new TranslatableComponent("gui.staticpower.manager_present")));
				} else {
					tooltip.add(new TextComponent(ChatFormatting.RED.toString()).append(new TranslatableComponent("gui.staticpower.manager_missing")));
				}
			}
		}
	}

	public static void drawBar(ITooltip tooltip, double currentValue, double maximum, Color mainColor, Color alternateColor, MutableComponent label, ResourceLocation name) {
		IElementHelper helper = tooltip.getElementHelper();
		MutableComponent text = GuiTextUtilities.formatHeatToString(currentValue, maximum);
		IProgressStyle progressStyle = helper.progressStyle().color(mainColor.encodeInInteger(), alternateColor.encodeInInteger());
		tooltip.add(helper.progress((float) (currentValue / maximum), text, progressStyle, helper.borderStyle()).tag(name));
	}
}
