package theking530.staticpower.initialization;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DeferredWorkQueue;
import theking530.staticpower.Registry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentity.vacuumchest.ContainerVacuumChest;
import theking530.staticpower.tileentity.vacuumchest.GuiVacuumChest;

public class ModContainerTypes {
	public static ContainerType<ContainerVacuumChest> VACUUM_CHEST_CONTAINER;

	public static void init() {
		VACUUM_CHEST_CONTAINER = Registry.preRegisterContainer("chest_vacuum", ContainerVacuumChest::new);
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void initializeGui() {
		DeferredWorkQueue.runLater(() -> {
			ScreenManager.registerFactory(VACUUM_CHEST_CONTAINER, GuiVacuumChest::new);
			StaticPower.LOGGER.debug("Registered ContainerType Screens");
		});
	}
}
