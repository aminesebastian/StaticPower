package theking530.staticcore.gui;

import java.util.HashSet;

import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.StaticCore;

public class StaticCoreSprites {
	public static final HashSet<ResourceLocation> SPRITES = new HashSet<ResourceLocation>();

	public static final ResourceLocation BLANK_TEXTURE = registerSprite("blank_texture");
	public static final ResourceLocation BLACK_TEXTURE = registerSprite("black_texture");
	public static final ResourceLocation EMPTY_TEXTURE = registerSprite("empty_texture");

	public static final ResourceLocation GUI_POWER_BAR_FG = registerSprite("gui/power_bar_power");
	public static final ResourceLocation GUI_POWER_BAR_BG = registerSprite("gui/power_bar_bg");

	public static final ResourceLocation POWER_SATISFACTION_BASE = registerSprite("gui/power_indicator/base");
	public static final ResourceLocation POWER_SATISFACTION_INDICATOR = registerSprite("gui/power_indicator/indicator");
	public static final ResourceLocation POWER_SATISFACTION_FRAME = registerSprite("gui/power_indicator/frame");
	public static final ResourceLocation POWER_SATISFACTION_GLASS = registerSprite("gui/power_indicator/glass");

	public static final ResourceLocation CANCEL = registerSprite("gui/cancel");
	public static final ResourceLocation GREEN_CHECK = registerSprite("gui/check");
	public static final ResourceLocation DISABLED = registerSprite("gui/disabled");
	public static final ResourceLocation RANGE_ICON = registerSprite("gui/show_range_icon");
	public static final ResourceLocation ERROR = registerSprite("gui/error");
	public static final ResourceLocation NOTIFICATION = registerSprite("gui/notification");
	public static final ResourceLocation NOTIFICATION_GOOD = registerSprite("gui/notification_good");
	public static final ResourceLocation IMPORT = registerSprite("gui/import_icon");
	public static final ResourceLocation EXPORT = registerSprite("gui/export_icon");
	public static final ResourceLocation FLAMES = registerSprite("gui/flames");
	public static final ResourceLocation TEAM_ICON = registerSprite("gui/team_icon");
	public static final ResourceLocation LOCKED_ICON = registerSprite("gui/locked");

	private static ResourceLocation registerSprite(String path) {
		ResourceLocation sprite = new ResourceLocation(StaticCore.MOD_ID, path);
		SPRITES.add(sprite);
		return sprite;
	}
}
