package theking530.staticpower.integration.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.gui.BaseContainerGui;
import theking530.staticpower.utilities.Reference;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(BaseContainerGui.class, new JEITabSlotAdjuster());
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID);
	}
}
