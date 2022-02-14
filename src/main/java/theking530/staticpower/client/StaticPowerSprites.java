package theking530.staticpower.client;

import java.util.HashSet;

import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;

public class StaticPowerSprites {
	public static final HashSet<ResourceLocation> SPRITES = new HashSet<ResourceLocation>();

	public static final ResourceLocation BLANK_TEXTURE = registerSprite("blank_texture");
	public static final ResourceLocation BLACK_TEXTURE = registerSprite("black_texture");
	public static final ResourceLocation EMPTY_TEXTURE = registerSprite("empty_texture");

	public static final ResourceLocation DIGISTORE_VOID_INDICATOR = registerSprite("blocks/digistore/void_indicator");
	public static final ResourceLocation DIGISTORE_LOCKED_INDICATOR = registerSprite("blocks/digistore/locked_indicator");
	public static final ResourceLocation DIGISTORE_FILL_BAR = registerSprite("blocks/digistore/digistore_fill_bar");
	public static final ResourceLocation DIGISTORE_FILL_BAR_FULL = registerSprite("blocks/digistore/digistore_fill_bar_full");

	public static final ResourceLocation LOGIC_GATE_INPUT = registerSprite("blocks/logicgates/logic_gate_input");
	public static final ResourceLocation LOGIC_GATE_OUTPUT = registerSprite("blocks/logicgates/logic_gate_output");
	public static final ResourceLocation LOGIC_GATE_OUTPUT_EXTRA = registerSprite("blocks/logicgates/logic_gate_extra_output");

	public static final ResourceLocation LOGIC_GATE_BASE_ON = registerSprite("blocks/logicgates/logic_gate_base_on");
	public static final ResourceLocation LOGIC_GATE_BASE_OFF = registerSprite("blocks/logicgates/logic_gate_base_off");

	public static final ResourceLocation MACHINE_SIDE_NORMAL = registerSprite("blocks/machines/sides_modes/machine_side_regular");
	public static final ResourceLocation MACHINE_SIDE_INPUT = registerSprite("blocks/machines/sides_modes/machine_side_input");
	public static final ResourceLocation MACHINE_SIDE_OUTPUT = registerSprite("blocks/machines/sides_modes/machine_side_output");
	public static final ResourceLocation MACHINE_SIDE_DISABLED = registerSprite("blocks/machines/sides_modes/machine_side_disabled");
	public static final ResourceLocation MACHINE_SIDE_GREEN = registerSprite("blocks/machines/sides_modes/machine_side_green");
	public static final ResourceLocation MACHINE_SIDE_YELLOW = registerSprite("blocks/machines/sides_modes/machine_side_yellow");
	public static final ResourceLocation MACHINE_SIDE_PURPLE = registerSprite("blocks/machines/sides_modes/machine_side_purple");
	public static final ResourceLocation MACHINE_SIDE_MAGENTA = registerSprite("blocks/machines/sides_modes/machine_side_magenta");
	public static final ResourceLocation MACHINE_SIDE_AQUA = registerSprite("blocks/machines/sides_modes/machine_side_aqua");

	public static final ResourceLocation BASIC_BATTERY_TOP = registerSprite("blocks/machines/batteries/battery_basic_top");
	public static final ResourceLocation BASIC_BATTERY_SIDE = registerSprite("blocks/machines/batteries/battery_basic_side");

	public static final ResourceLocation IRON_TANK = registerSprite("blocks/machines/tank_iron");
	public static final ResourceLocation BASIC_TANK = registerSprite("blocks/machines/tank_basic");
	public static final ResourceLocation ADVANCED_TANK = registerSprite("blocks/machines/tank_advanced");
	public static final ResourceLocation STATIC_TANK = registerSprite("blocks/machines/tank_static");
	public static final ResourceLocation ENERGIZED_TANK = registerSprite("blocks/machines/tank_energized");
	public static final ResourceLocation LUMUM_TANK = registerSprite("blocks/machines/tank_lumum");
	public static final ResourceLocation CREATIVE_TANK = registerSprite("blocks/machines/tank_creative");

	public static final ResourceLocation BATTERY_BLOCK_BASIC = registerSprite("blocks/machines/batteries/battery_block_basic");
	public static final ResourceLocation BATTERY_BLOCK_BAR = registerSprite("blocks/machines/batteries/battery_block_side_filled");

	public static final ResourceLocation GUI_POWER_BAR_FG = registerSprite("gui/power_bar_power");
	public static final ResourceLocation GUI_POWER_BAR_BG = registerSprite("gui/power_bar_bg");

	public static final ResourceLocation FILTER_WHITELIST = registerSprite("gui/filter_whitelist");
	public static final ResourceLocation FILTER_BLACKLIST = registerSprite("gui/filter_blacklist");
	public static final ResourceLocation FILTER_NBT = registerSprite("gui/filter_nbt");
	public static final ResourceLocation FILTER_TAG = registerSprite("gui/filter_tag");
	public static final ResourceLocation FILTER_MOD = registerSprite("gui/filter_mod");

	public static final ResourceLocation SORT_ALPHA_ASC = registerSprite("gui/sort_alphabetical_asc");
	public static final ResourceLocation SORT_ALPHA_DESC = registerSprite("gui/sort_alphabetical_desc");
	public static final ResourceLocation SORT_NUMERICAN_ASC = registerSprite("gui/sort_numerical_asc");
	public static final ResourceLocation SORT_NUMERICAL_DESC = registerSprite("gui/sort_numerical_desc");

	public static final ResourceLocation EMPTY_BUCKET = registerSprite("gui/empty_bucket_indicator");
	public static final ResourceLocation EMPTY_BUCKET_HOVERED = registerSprite("gui/empty_bucket_indicator_hover");
	public static final ResourceLocation FILL_BUCKET = registerSprite("gui/fill_bucket_indicator");
	public static final ResourceLocation FILL_BUCKET_HOVERED = registerSprite("gui/fill_bucket_indicator_hover");

	public static final ResourceLocation SEARCH_MODE_DEFAULT = registerSprite("gui/search_mode_default");
	public static final ResourceLocation SEARCH_MODE_ONE_WAY = registerSprite("gui/search_mode_one_way");
	public static final ResourceLocation SEARCH_MODE_TWO_WAY = registerSprite("gui/search_mode_two_way");

	public static final ResourceLocation PORTABLE_BATTERY_FILL_BAR = registerSprite("items/batteries/portable_battery_filled_bar");
	public static final ResourceLocation PORTABLE_CREATIVE_BATTERY_FILL_BAR = registerSprite("items/batteries/portable_battery_creative_filled_bar");

	public static final ResourceLocation PORTABLE_BATTERY_PACK_FILL_BAR = registerSprite("items/batteries/battery_pack_filled_bar");
	public static final ResourceLocation PORTABLE_CREATIVE_BATTERY_PACK_FILL_BAR = registerSprite("items/batteries/battery_pack_creative_filled_bar");

	public static final ResourceLocation CANCEL = registerSprite("gui/cancel");
	public static final ResourceLocation GREEN_CHECK = registerSprite("gui/check");
	public static final ResourceLocation DISABLED = registerSprite("gui/disabled");
	public static final ResourceLocation RANGE_ICON = registerSprite("gui/show_range_icon");
	public static final ResourceLocation ERROR = registerSprite("gui/error");
	public static final ResourceLocation NOTIFICATION = registerSprite("gui/notification");
	public static final ResourceLocation IMPORT = registerSprite("gui/import_icon");
	public static final ResourceLocation EXPORT = registerSprite("gui/export_icon");
	public static final ResourceLocation FLAMES = registerSprite("gui/flames");

	public static final ResourceLocation SIZE_TWO_CRAFTING = registerSprite("gui/2x2");
	public static final ResourceLocation SIZE_THREE_CRAFTING = registerSprite("gui/3x3");

	public static final ResourceLocation CRAFTING_TABLE_ICON = registerSprite("gui/crafting_table_icon");
	public static final ResourceLocation FURNACE_ICON = registerSprite("gui/furnace_icon");
	public static final ResourceLocation CLOSE = registerSprite("gui/close");
	public static final ResourceLocation ARROW_DOWN = registerSprite("gui/arrow_down");

	public static final ResourceLocation TOOL_POWER_BAR = registerSprite("items/tools/tool_power_bar");

	public static final ResourceLocation CONCRETE_BUCKET_TOP_LAYER = registerSprite("items/buckets/bucket_concrete_top_layer");

	private static ResourceLocation registerSprite(String path) {
		ResourceLocation sprite = new ResourceLocation(StaticPower.MOD_ID, path);
		SPRITES.add(sprite);
		return sprite;
	}
}
