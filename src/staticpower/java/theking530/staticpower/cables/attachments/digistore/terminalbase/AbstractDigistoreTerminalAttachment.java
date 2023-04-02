package theking530.staticpower.cables.attachments.digistore.terminalbase;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.cables.attachments.digistore.AbstractDigistoreCableAttachment;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;

public abstract class AbstractDigistoreTerminalAttachment extends AbstractDigistoreCableAttachment {
	public static final String TERMINAL_SEARCH_MODE = "search_mode";
	public static final String TERMINAL_SORT_TYPE = "sort_type";
	public static final String TERMINAL_SORT_DESC = "sort_desc";
	private static final Vector3D BOUNDS = new Vector3D(6.0f, 6.0f, 2.0f);
	private final ResourceLocation model;
	private final ResourceLocation noManagerModel;

	public AbstractDigistoreTerminalAttachment(ResourceLocation model, ResourceLocation noManagerModel) {
		super();
		this.model = model;
		this.noManagerModel = noManagerModel;
	}

	public AbstractDigistoreTerminalAttachment(ResourceLocation model) {
		this(model, model);
	}

	@Override
	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cableComponent) {
		super.onAddedToCable(attachment, side, cableComponent);
		getAttachmentTag(attachment).putInt(TERMINAL_SEARCH_MODE, DigistoreSyncedSearchMode.DEFAULT.ordinal());
		getAttachmentTag(attachment).putInt(TERMINAL_SORT_TYPE, DigistoreInventorySortType.COUNT.ordinal());
		getAttachmentTag(attachment).putBoolean(TERMINAL_SORT_DESC, true);
	}

	@Override
	public ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos) {
		DigistoreCableProviderComponent digistoreCable = CableUtilities.getCableWrapperComponent(level, pos);
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
		return DigistoreInventorySortType.values()[getAttachmentTag(attachment).getInt(TERMINAL_SORT_TYPE)];
	}

	public static void setSortType(ItemStack attachment, DigistoreInventorySortType type) {
		getAttachmentTag(attachment).putInt(TERMINAL_SORT_TYPE, type.ordinal());
	}

	public static DigistoreSyncedSearchMode getSearchMode(ItemStack attachment) {
		return DigistoreSyncedSearchMode.values()[getAttachmentTag(attachment).getInt(TERMINAL_SEARCH_MODE)];
	}

	public static void setSearchMode(ItemStack attachment, DigistoreSyncedSearchMode mode) {
		getAttachmentTag(attachment).putInt(TERMINAL_SEARCH_MODE, mode.ordinal());
	}

	public static boolean getSortDescending(ItemStack attachment) {
		return getAttachmentTag(attachment).getBoolean(TERMINAL_SORT_DESC);
	}

	public static void setSortDescending(ItemStack attachment, boolean descnding) {
		getAttachmentTag(attachment).putBoolean(TERMINAL_SORT_DESC, descnding);
	}
}
