package theking530.staticpower.integration.JADE;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.gui.GuiTextUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.digistorenetwork.digistore.BlockDigistore;
import theking530.staticpower.cables.power.PowerCableComponent;

@WailaPlugin(StaticPower.MOD_ID)
public class JadePluginImplementation implements IWailaPlugin {
	public static final ResourceLocation POWER_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "power_bar");
	public static final ResourceLocation OUTPUT_VOLTAGE_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "output_voltage");
	public static final ResourceLocation INPUT_VOLTAGE_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "input_voltage");
	public static final ResourceLocation HEAT_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "heat_bar");
	public static final ResourceLocation PROCESSING_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "processing_bar");
	public static final ResourceLocation FLUID_HEAD_PRESSURE_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "fluid_bar");

	public static final SDColor MAIN_SV_COLOR = new SDColor(0, 0.6f, 0.8f).fromFloatToEightBit();
	public static final SDColor ALT_SV_COLOR = new SDColor(0, 0.458f, 1.0f).fromFloatToEightBit();

	public static final SDColor MAIN_HEAT_COLOR = new SDColor(1.0f, 0.517f, 0.f).fromFloatToEightBit();
	public static final SDColor ALT_HEAT_COLOR = new SDColor(1.0f, 0.615f, 0.f).fromFloatToEightBit();

	public static final SDColor MAIN_PROCESSING_COLOR = new SDColor(0.67f, 0.67f, 0.67f).fromFloatToEightBit();
	public static final SDColor ALT_PROCESSING_COLOR = new SDColor(0.67f, 0.67f, 0.67f).fromFloatToEightBit();

	public static final SDColor MAIN_PRESSURE_COLOR = new SDColor(0.67f, 0.67f, 0.67f).fromFloatToEightBit();
	public static final SDColor ALT_PRESSURE_COLOR = new SDColor(0.6f, 0.6f, 0.6f).fromFloatToEightBit();

	@Override
	public void register(IWailaCommonRegistration registrar) {
		registrar.registerBlockDataProvider(new JadeDataProviders(), BlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registrar) {
		registrar.registerBlockComponent(new DigistoreMasterPresenceDecorator(), Block.class);
		registrar.registerBlockComponent(new DigistoreDecorator(), BlockDigistore.class);
		registrar.registerBlockComponent(new StaticVoltDecorator(), Block.class);
		registrar.registerBlockComponent(new HeatDecorator(), Block.class);
		registrar.registerBlockComponent(new ProcessingTimeDecorator(), Block.class);
		registrar.registerBlockComponent(new FluidPipeDecorator(), Block.class);
	}

	public static class StaticVoltDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BlockEntity be = accessor.getBlockEntity();
			if (be != null) {
				IStaticPowerStorage storage = be.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY).orElse(null);
				if (storage != null || (accessor.isServerConnected() && accessor.getServerData().contains(JadeDataProviders.POWER_TAG))) {
					double stored = 0, capacity = 0;
					StaticPowerVoltage outputVoltage = StaticPowerVoltage.ZERO;
					StaticPowerVoltage minVoltage = StaticPowerVoltage.ZERO, maxVoltage = StaticPowerVoltage.ZERO;
					boolean canAcceptExternalPower = false;
					boolean canOutputExternalPower = false;
					boolean isAlternating = false;
					boolean isPowerCable = ComponentUtilities.getComponent(PowerCableComponent.class, be).isPresent();

					if (accessor.isServerConnected()) {
						CompoundTag svData = accessor.getServerData().getCompound(JadeDataProviders.POWER_TAG);
						canAcceptExternalPower = svData.getBoolean("canAcceptExternalPower");
						canOutputExternalPower = svData.getBoolean("canOutputExternalPower");

						if (svData.contains("output_voltage")) {
							outputVoltage = StaticPowerVoltage.values()[svData.getByte("output_voltage")];
						}

						isAlternating = svData.getBoolean("is_alternating");

						if (svData.contains("capacity")) {
							stored = svData.getDouble("stored_power");
							capacity = svData.getDouble("capacity");

							StaticVoltageRange range = StaticVoltageRange.deserializeNBT(svData.getCompound("voltage_range"));

							minVoltage = range.minimumVoltage();
							maxVoltage = range.maximumVoltage();
						}

					} else {
						canAcceptExternalPower = storage.canAcceptExternalPower();
						canOutputExternalPower = storage.canOutputExternalPower();

						if (!storage.drainPower(1, true).isEmpty()) {
							outputVoltage = storage.getOutputVoltage();
						}

						if (storage.getInputVoltageRange().minimumVoltage() != StaticPowerVoltage.ZERO
								&& storage.getInputVoltageRange().maximumVoltage() != StaticPowerVoltage.ZERO) {
							stored = storage.getStoredPower();
							capacity = storage.getCapacity();
							minVoltage = storage.getInputVoltageRange().minimumVoltage();
							maxVoltage = storage.getInputVoltageRange().maximumVoltage();
						}

						isAlternating = storage.getOutputCurrentType() == CurrentType.ALTERNATING;
					}

					Component voltageTypeComponent = Component.literal("⎓");
					if (isAlternating) {
						voltageTypeComponent = Component.literal("∿");
					}

					// Draw the output voltage.
					if (canOutputExternalPower) {
						String key = isPowerCable ? "gui.staticpower.current_voltage" : "gui.staticpower.output_voltage";
						JadePluginImplementation.drawValue(tooltip,
								Component.translatable(key).append(": ").append(PowerTextFormatting.formatVoltageToString(outputVoltage).append(voltageTypeComponent)),
								OUTPUT_VOLTAGE_RENDERER);
					}

					if (canAcceptExternalPower && !isPowerCable) {
						JadePluginImplementation.drawValue(tooltip, Component.translatable("gui.staticpower.input_voltage").append(": ")
								.append(PowerTextFormatting.formatVoltageRangeToString(new StaticVoltageRange(minVoltage, maxVoltage))), INPUT_VOLTAGE_RENDERER);
					}

					if (capacity > 0) {
						JadePluginImplementation.drawBar(tooltip, stored, capacity, MAIN_SV_COLOR, ALT_SV_COLOR,
								PowerTextFormatting.formatPowerToString(stored, capacity).withStyle(ChatFormatting.WHITE), POWER_BAR_RENDERER);
					}
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "static_voltage");
		}
	}

	public static class HeatDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			BlockEntity tile = accessor.getBlockEntity();
			if (tile != null) {
				IHeatStorage storage = tile.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY).orElse(null);
				if (storage != null && (!accessor.isServerConnected() || accessor.getServerData().contains(JadeDataProviders.HEAT_TAG))) {
					int stored, capacity;
					if (accessor.isServerConnected()) {
						CompoundTag heatData = accessor.getServerData().getCompound(JadeDataProviders.HEAT_TAG);
						stored = heatData.getInt("value");
						capacity = heatData.getInt("max");
					} else {
						stored = storage.getCurrentHeat();
						capacity = storage.getOverheatThreshold();
					}

					// Draw bar
					JadePluginImplementation.drawBar(tooltip, stored, capacity, MAIN_HEAT_COLOR, ALT_HEAT_COLOR,
							GuiTextUtilities.formatHeatToString(stored, capacity).withStyle(ChatFormatting.WHITE), HEAT_BAR_RENDERER);
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "heat");
		}
	}

	public static class ProcessingTimeDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.isServerConnected() && accessor.getServerData().contains(JadeDataProviders.PROCESSING_TAG)) {
				CompoundTag processingData = accessor.getServerData().getCompound(JadeDataProviders.PROCESSING_TAG);
				int remaining = processingData.getInt("remaining");
				int totalTime = processingData.getInt("max");

				// Draw bar
				if (remaining > 0) {
					JadePluginImplementation.drawBar(tooltip, remaining, totalTime, MAIN_PROCESSING_COLOR, ALT_PROCESSING_COLOR, GuiTextUtilities.formatNumberAsString(remaining)
							.append(" ").append(Component.translatable("gui.staticpower.ticks_remaining")).withStyle(ChatFormatting.WHITE), PROCESSING_BAR_RENDERER);
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "processing");
		}
	}

	public static class FluidPipeDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.isServerConnected() && accessor.getServerData().contains("head_pressure")) {
				float pressure = accessor.getServerData().getFloat("head_pressure");
				JadePluginImplementation.drawBar(
						tooltip, pressure / 2, IStaticPowerFluidHandler.MAX_PRESSURE / 2, MAIN_PRESSURE_COLOR, ALT_PRESSURE_COLOR, GuiTextUtilities
								.formatNumberAsStringOneDecimal(pressure / 2).append(" ").append(Component.translatable("gui.staticpower.head_pressure")).withStyle(ChatFormatting.WHITE),
						FLUID_HEAD_PRESSURE_RENDERER);
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "fluid_pipe");
		}
	}

	public static class DigistoreDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {

		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "digistore");
		}
	}

	public static class DigistoreMasterPresenceDecorator implements IBlockComponentProvider {
		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(JadeDataProviders.DIGISTORE_MANAGER_TAG)) {
				boolean managerPresent = accessor.getServerData().getBoolean(JadeDataProviders.DIGISTORE_MANAGER_TAG);
				if (managerPresent) {
					tooltip.add(Component.translatable("gui.staticpower.manager_present").withStyle(ChatFormatting.GREEN));
				} else {
					tooltip.add(Component.translatable("gui.staticpower.manager_missing").withStyle(ChatFormatting.RED));
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return new ResourceLocation(StaticPower.MOD_ID, "digistore_master_presence");
		}
	}

	public static void drawBar(ITooltip tooltip, double currentValue, double maximum, SDColor mainColor, SDColor alternateColor, MutableComponent label, ResourceLocation name) {
		IElementHelper helper = tooltip.getElementHelper();
		IProgressStyle progressStyle = helper.progressStyle().color(mainColor.encodeInInteger(), alternateColor.encodeInInteger());
		tooltip.add(helper.progress((float) (currentValue / maximum), label, progressStyle, helper.borderStyle()).tag(name));
	}

	public static void drawValue(ITooltip tooltip, Component component, ResourceLocation name) {
		IElementHelper helper = tooltip.getElementHelper();
		tooltip.add(helper.text(component).tag(name));
	}
}
