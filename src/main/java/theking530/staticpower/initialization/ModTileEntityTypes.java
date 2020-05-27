package theking530.staticpower.initialization;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.Registry;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

public class ModTileEntityTypes {
	public static TileEntityType<?> VACCUM_CHEST;
	
	public static void init() {
		VACCUM_CHEST = Registry.preRegisterTileEntity(TileEntityVacuumChest::new, ModBlocks.VacuumChest);
	}
}
