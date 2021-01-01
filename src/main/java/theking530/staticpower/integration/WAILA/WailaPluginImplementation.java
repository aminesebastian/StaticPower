package theking530.staticpower.integration.WAILA;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.TOP.BarTooltipRenderer;
import theking530.staticpower.tileentities.digistorenetwork.digistore.BlockDigistore;

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
		registrar.registerComponentProvider(new DigistoreMasterPresenceDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new DigistoreDecorator(), TooltipPosition.BODY, BlockDigistore.class);
		registrar.registerBlockDataProvider(new WailaDataProviders(), Block.class);
		registrar.registerComponentProvider(new StaticVoltDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new HeatDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new ProcessingTimeDecorator(), TooltipPosition.BODY, Block.class);
		registrar.registerComponentProvider(new FluidDecorator(), TooltipPosition.BODY, Block.class);
	}

	public static class StaticVoltDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(WailaDataProviders.POWER_TAG)) {
				tooltip.add(new RenderableTextComponent(POWER_BAR_RENDERER, accessor.getServerData().getCompound(WailaDataProviders.POWER_TAG)));
			}
		}
	}

	public static class HeatDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(WailaDataProviders.HEAT_TAG)) {
				tooltip.add(new RenderableTextComponent(HEAT_BAR_RENDERER, accessor.getServerData().getCompound(WailaDataProviders.HEAT_TAG)));
			}
		}
	}

	public static class ProcessingTimeDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(WailaDataProviders.PROCESSING_TAG)) {
				tooltip.add(new RenderableTextComponent(PROCESSING_BAR_RENDERER, accessor.getServerData().getCompound(WailaDataProviders.PROCESSING_TAG)));
			}
		}
	}

	public static class FluidDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(WailaDataProviders.FLUID_TAG)) {
				tooltip.add(new RenderableTextComponent(FLUID_BAR_RENDERER, accessor.getServerData().getCompound(WailaDataProviders.FLUID_TAG)));
			}
		}
	}

	public static class DigistoreDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {

		}
	}

	public static class DigistoreMasterPresenceDecorator implements IComponentProvider {
		@Override
		public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
			if (accessor.getServerData().contains(WailaDataProviders.DIGISTORE_MANAGER_TAG)) {
				boolean managerPresent = accessor.getServerData().getBoolean(WailaDataProviders.DIGISTORE_MANAGER_TAG);
				if (managerPresent) {
					tooltip.add(new StringTextComponent(TextFormatting.GREEN.toString()).append(new TranslationTextComponent("gui.staticpower.manager_present")));
				} else {
					tooltip.add(new StringTextComponent(TextFormatting.RED.toString()).append(new TranslationTextComponent("gui.staticpower.manager_missing")));
				}
			}
		}
	}
}
