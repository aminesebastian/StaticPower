package theking530.staticpower.init;

import net.minecraft.world.item.CreativeModeTab;
import theking530.staticpower.itemgroup.StaticPowerCableItemGroup;
import theking530.staticpower.itemgroup.StaticPowerItemGroup;
import theking530.staticpower.itemgroup.StaticPowerMaterialItemGroup;
import theking530.staticpower.itemgroup.StaticPowerToolItemGroup;

public class ModCreativeTabs {
	public static final CreativeModeTab GENERAL = new StaticPowerItemGroup();
	public static final CreativeModeTab CABLES = new StaticPowerCableItemGroup();
	public static final CreativeModeTab TOOLS = new StaticPowerToolItemGroup();
	public static final CreativeModeTab MATERIALS = new StaticPowerMaterialItemGroup();
}
