package theking530.staticpower.integration.WAILA;

import java.util.List;
import java.util.Optional;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.power.CapabilityStaticVolt;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.integration.TOP.BarTooltipRenderer;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent;

@WailaPlugin(StaticPower.MOD_ID)
public class WailaPluginImplementation implements IWailaPlugin {
	public static final ResourceLocation POWER_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "power_bar");
	public static final ResourceLocation HEAT_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "heat_bar");
	public static final ResourceLocation PROCESSING_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "processing_bar");
	public static final ResourceLocation FLUID_BAR_RENDERER = new ResourceLocation(StaticPower.MOD_ID, "fluid_bar");

	public static final Color MAIN_SV_COLOR = new Color(0, 0.6f, 0.8f);
	public static final Color ALT_SV_COLOR = new Color(0, 0.458f, 1.0f);

	public static final Color MAIN_HEAT_COLOR = new Color(1.0f, 0.517f, 0.f);
	public static final Color ALT_HEAT_COLOR = new Color(1.0f, 0.615f, 0.f);

	public static final Color MAIN_PROCESSING_COLOR = new Color(0.67f, 0.67f, 0.67f);
	public static final Color ALT_PROCESSING_COLOR = new Color(0.67f, 0.67f, 0.67f);

	public static final Color MAIN_FLUID_COLOR = new Color(0, 0.18f, 0.88f);
	public static final Color ALT_FLUID_COLOR = new Color(0, 0.164f, 0.831f);

	@Override
	public void register(IRegistrar registrar) {
		registrar.registerTooltipRenderer(POWER_BAR_RENDERER, new BarTooltipRenderer(100, 10, MAIN_SV_COLOR, ALT_SV_COLOR, Color.DARK_GREY));
		registrar.registerTooltipRenderer(HEAT_BAR_RENDERER, new BarTooltipRenderer(100, 10, MAIN_HEAT_COLOR, ALT_HEAT_COLOR, Color.DARK_GREY));
		registrar.registerTooltipRenderer(PROCESSING_BAR_RENDERER, new BarTooltipRenderer(100, 10, MAIN_PROCESSING_COLOR, ALT_PROCESSING_COLOR, Color.DARK_GREY));
		registrar.registerTooltipRenderer(FLUID_BAR_RENDERER, new BarTooltipRenderer(100, 10, MAIN_FLUID_COLOR, ALT_FLUID_COLOR, Color.DARK_GREY));

		registrar.registerComponentProvider(new StaticVoltDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new HeatDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new ProcessingTimeDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new FluidDecorator(), TooltipPosition.BODY, Block.class);
	}

	public static class StaticVoltDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			TileEntity te = accessor.getTileEntity();
			if (te != null) {
				te.getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, accessor.getSide()).ifPresent(powerStorage -> {
					CompoundNBT powerData = new CompoundNBT();
					powerData.putDouble("value", powerStorage.getStoredPower());
					powerData.putDouble("max", powerStorage.getCapacity());
					powerData.putString("description", GuiTextUtilities.formatEnergyToString(powerStorage.getStoredPower()).getString());
					tooltip.add(new RenderableTextComponent(POWER_BAR_RENDERER, powerData));
				});
			}
		}
	}

	public static class HeatDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			TileEntity te = accessor.getTileEntity();
			if (te != null) {
				te.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, accessor.getSide()).ifPresent(heatStorage -> {
					CompoundNBT heatData = new CompoundNBT();
					heatData.putDouble("value", heatStorage.getCurrentHeat());
					heatData.putDouble("max", heatStorage.getMaximumHeat());
					heatData.putString("description", GuiTextUtilities.formatHeatToString(heatStorage.getCurrentHeat()).getString());
					tooltip.add(new RenderableTextComponent(HEAT_BAR_RENDERER, heatData));
				});
			}
		}
	}

	public static class ProcessingTimeDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			TileEntity te = accessor.getTileEntity();
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

				tooltip.add(new RenderableTextComponent(PROCESSING_BAR_RENDERER, processingData));
			}
		}
	}

	public static class FluidDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			TileEntity te = accessor.getTileEntity();
			if (te != null) {
				te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, accessor.getSide()).ifPresent(tank -> {
					// Do nothing if there are not tanks.
					if (tank.getTanks() == 0 || tank.getFluidInTank(0).isEmpty()) {
						return;
					}

					CompoundNBT fluidData = new CompoundNBT();
					fluidData.putDouble("value", tank.getFluidInTank(0).getAmount());
					fluidData.putDouble("max", tank.getTankCapacity(0));
					fluidData.putString("description",
							GuiTextUtilities.formatFluidToString(tank.getFluidInTank(0).getAmount()).appendString(" ").append(tank.getFluidInTank(0).getDisplayName()).getString());
					tooltip.add(new RenderableTextComponent(FLUID_BAR_RENDERER, fluidData));
				});
			}
		}
	}
}
