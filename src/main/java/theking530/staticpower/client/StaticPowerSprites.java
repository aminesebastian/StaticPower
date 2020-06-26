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

	public static final ResourceLocation LOGIC_GATE_INPUT = registerSprite("blocks/logicgates/logic_gate_input");
	public static final ResourceLocation LOGIC_GATE_OUTPUT = registerSprite("blocks/logicgates/logic_gate_output");
	public static final ResourceLocation LOGIC_GATE_OUTPUT_EXTRA = registerSprite("blocks/logicgates/logic_gate_extra_output");

	public static final ResourceLocation LOGIC_GATE_BASE_ON = registerSprite("blocks/logicgates/logic_gate_base_on");
	public static final ResourceLocation LOGIC_GATE_BASE_OFF = registerSprite("blocks/logicgates/logic_gate_base_off");

	public static final ResourceLocation MACHINE_SIDE_NORMAL = registerSprite("blocks/machines/machine_side");
	public static final ResourceLocation MACHINE_SIDE_INPUT = registerSprite("blocks/machines/machine_side_input");
	public static final ResourceLocation MACHINE_SIDE_OUTPUT = registerSprite("blocks/machines/machine_side_output");
	public static final ResourceLocation MACHINE_SIDE_DISABLED = registerSprite("blocks/machines/machine_side_disabled");

	public static final ResourceLocation GREEN_MACHINE_SIDE_NORMAL = registerSprite("blocks/machines/farmer_side");
	public static final ResourceLocation GREEN_MACHINE_SIDE_INPUT = registerSprite("blocks/machines/farmer_side_input");
	public static final ResourceLocation GREEN_MACHINE_SIDE_OUTPUT = registerSprite("blocks/machines/farmer_side_output");
	public static final ResourceLocation GREEN_MACHINE_SIDE_DISABLED = registerSprite("blocks/machines/farmer_side_disabled");

	public static final ResourceLocation TANK_SIDE = registerSprite("blocks/machines/tank_basic");

	public static final ResourceLocation FILTER_WHITELIST = registerSprite("gui/filter_whitelist");
	public static final ResourceLocation FILTER_BLACKLIST = registerSprite("gui/filter_blacklist");
	public static final ResourceLocation FILTER_NBT = registerSprite("gui/filter_nbt");
	public static final ResourceLocation FILTER_TAG = registerSprite("gui/filter_tag");
	public static final ResourceLocation FILTER_MOD = registerSprite("gui/filter_mod");

	public static final ResourceLocation CANCEL = registerSprite("gui/cancel");
	public static final ResourceLocation GREEN_CHECK = registerSprite("gui/check");
	public static final ResourceLocation DISABLED = registerSprite("gui/disabled");

	private static ResourceLocation registerSprite(String path) {
		ResourceLocation sprite = new ResourceLocation(Reference.MOD_ID, path);
		SPRITES.add(sprite);
		return sprite;
	}
}
