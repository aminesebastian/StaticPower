package theking530.staticpower.init;

import net.minecraft.world.item.CreativeModeTab;
import theking530.staticpower.itemgroup.StaticPowerCableItemGroup;
import theking530.staticpower.itemgroup.StaticPowerItemGroup;
import theking530.staticpower.itemgroup.StaticPowerMaterialItemGroup;
import theking530.staticpower.itemgroup.StaticPowerToolItemGroup;

public class ModGroups {
	public static final CreativeModeTab CREATIVE_TAB = new StaticPowerItemGroup();
	public static final CreativeModeTab CABLE_CREATIVE_TAB = new StaticPowerCableItemGroup();
	public static final CreativeModeTab TOOL_CREATIVE_TAB = new StaticPowerToolItemGroup();
	public static final CreativeModeTab MATERIAL_CREATIVE_TAB = new StaticPowerMaterialItemGroup();
}
