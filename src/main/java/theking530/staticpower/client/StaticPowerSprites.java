package theking530.staticpower.client;

import java.util.HashSet;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.utilities.Reference;

public class StaticPowerSprites {
	public static final HashSet<ResourceLocation> SPRITES = new HashSet<ResourceLocation>();

	public static final ResourceLocation BLANK_TEXTURE = registerSprite("blank_texture");

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

	public static final ResourceLocation BASIC_BATTERY_TOP = registerSprite("blocks/machines/batteries/battery_basic_top");
	public static final ResourceLocation BASIC_BATTERY_SIDE = registerSprite("blocks/machines/batteries/battery_basic_side");

	public static final ResourceLocation TANK_SIDE = registerSprite("blocks/machines/tank_basic");

	public static final ResourceLocation BATTERY_BLOCK_BASIC = registerSprite("blocks/machines/batteries/battery_block_basic");
	public static final ResourceLocation BATTERY_BLOCK_BAR = registerSprite("blocks/machines/batteries/battery_block_side_filled");

	public static final ResourceLocation FILTER_WHITELIST = registerSprite("gui/filter_whitelist");
	public static final ResourceLocation FILTER_BLACKLIST = registerSprite("gui/filter_blacklist");
	public static final ResourceLocation FILTER_NBT = registerSprite("gui/filter_nbt");
	public static final ResourceLocation FILTER_TAG = registerSprite("gui/filter_tag");
	public static final ResourceLocation FILTER_MOD = registerSprite("gui/filter_mod");
	public static final ResourceLocation SCROLL_HANDLE = registerSprite("gui/scroll_handle");
	public static final ResourceLocation SCROLL_HANDLE_DISABLED = registerSprite("gui/scroll_handle_disabled");

	public static final ResourceLocation SORT_ALPHA_ASC = registerSprite("gui/sort_alphabetical_asc");
	public static final ResourceLocation SORT_ALPHA_DESC = registerSprite("gui/sort_alphabetical_desc");
	public static final ResourceLocation SORT_NUMERICAN_ASC = registerSprite("gui/sort_numerical_asc");
	public static final ResourceLocation SORT_NUMERICAL_DESC = registerSprite("gui/sort_numerical_desc");

	public static final ResourceLocation SEARCH_MODE_DEFAULT = registerSprite("gui/search_mode_default");
	public static final ResourceLocation SEARCH_MODE_ONE_WAY = registerSprite("gui/search_mode_one_way");
	public static final ResourceLocation SEARCH_MODE_TWO_WAY = registerSprite("gui/search_mode_two_way");

	public static final ResourceLocation CANCEL = registerSprite("gui/cancel");
	public static final ResourceLocation GREEN_CHECK = registerSprite("gui/check");
	public static final ResourceLocation DISABLED = registerSprite("gui/disabled");
	public static final ResourceLocation RANGE_ICON = registerSprite("gui/show_range_icon");

	private static ResourceLocation registerSprite(String path) {
		ResourceLocation sprite = new ResourceLocation(Reference.MOD_ID, path);
		SPRITES.add(sprite);
		return sprite;
	}
}
