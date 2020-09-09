package theking530.staticpower.cables.attachments.digistore.terminalbase;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;

public abstract class AbstractDigistoreTerminalAttachment extends AbstractCableAttachment {
	public static final String TERMINAL_SEARCH_MODE = "search_mode";
	public static final String TERMINAL_SORT_TYPE = "sort_type";
	public static final String TERMINAL_SORT_DESC = "sort_desc";
	private static final Vector3D BOUNDS = new Vector3D(6.0f, 6.0f, 2.0f);
	private final ResourceLocation model;
	private final ResourceLocation noManagerModel;

	public AbstractDigistoreTerminalAttachment(String name, ResourceLocation model, ResourceLocation noManagerModel) {
		super(name);
		this.model = model;
		this.noManagerModel = noManagerModel;
	}

	public AbstractDigistoreTerminalAttachment(String name, ResourceLocation model) {
		this(name, model, model);
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
		DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cableComponent;
		return digistoreCable.isManagerPresent() ? model : noManagerModel;
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
