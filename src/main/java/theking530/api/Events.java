package theking530.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.api.attributes.ItemAttributeRegistry.ItemAttributeRegisterEvent;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.staticpower.StaticPower;

public class Events {
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			ItemAttributeRegisterEvent itemAttributeEvent = new ItemAttributeRegisterEvent();
			MinecraftForge.EVENT_BUS.post(itemAttributeEvent);
		});
	}

	public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject() instanceof ItemStack) {
			if (!event.getObject().getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).isPresent()) {
				AttributeableHandler handler = new AttributeableHandler();
				event.addCapability(new ResourceLocation(StaticPower.MOD_ID, "attributes"), handler);
			}
		}
	}
}
