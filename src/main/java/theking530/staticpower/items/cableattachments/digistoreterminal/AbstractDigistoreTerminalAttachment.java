package theking530.staticpower.items.cableattachments.digistoreterminal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import theking530.common.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;

public abstract class AbstractDigistoreTerminalAttachment extends AbstractCableAttachment {
	public static final String TERMINAL_SEARCH_MODE = "search_mode";
	public static final String TERMINAL_SORT_TYPE = "sort_type";
	public static final String TERMINAL_SORT_DESC = "sort_desc";
	private static final Vector3D BOUNDS = new Vector3D(6.0f, 6.0f, 0.0f);
	private final ResourceLocation model;

	public AbstractDigistoreTerminalAttachment(String name, ResourceLocation model) {
		super(name);
		this.model = model;
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		attachment.getTag().putInt(TERMINAL_SEARCH_MODE, DigistoreSearchMode.DEFAULT.ordinal());
		attachment.getTag().putInt(TERMINAL_SORT_TYPE, DigistoreInventorySortType.COUNT.ordinal());
		attachment.getTag().putBoolean(TERMINAL_SORT_DESC, true);
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent) {
		return model;
	}

	@Override
	public boolean hasGui(ItemStack attachment) {
		return true;
	}

	@Override
	public Vector3D getBounds() {
		return BOUNDS;
	}

	public static DigistoreInventorySortType getSortType(ItemStack attachment) {
		return DigistoreInventorySortType.values()[attachment.getTag().getInt(TERMINAL_SORT_TYPE)];
	}

	public static void setSortType(ItemStack attachment, DigistoreInventorySortType type) {
		attachment.getTag().putInt(TERMINAL_SORT_TYPE, type.ordinal());
	}

	public static DigistoreSearchMode getSearchMode(ItemStack attachment) {
		return DigistoreSearchMode.values()[attachment.getTag().getInt(TERMINAL_SEARCH_MODE)];
	}

	public static void setSearchMode(ItemStack attachment, DigistoreSearchMode mode) {
		attachment.getTag().putInt(TERMINAL_SEARCH_MODE, mode.ordinal());
	}

	public static boolean getSortDescending(ItemStack attachment) {
		return attachment.getTag().getBoolean(TERMINAL_SORT_DESC);
	}

	public static void setSortDescending(ItemStack attachment, boolean descnding) {
		attachment.getTag().putBoolean(TERMINAL_SORT_DESC, descnding);
	}
}
