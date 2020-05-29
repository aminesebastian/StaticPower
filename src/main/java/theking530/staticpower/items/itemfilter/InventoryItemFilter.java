package theking530.staticpower.items.itemfilter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

public class InventoryItemFilter extends ItemStackHandler {
	/** Top level tag name for filter serialization. */
	public static final String ITEM_FILTER_NBT_KEY = "StaticPowerItemFilter";
	/** Indicates if we are in whitelist or blacklist modes. */
	public static final String WHITE_LIST_MODE_NBT_KEY = "WhitelistMode";
	/** Indicates if this filter should match metadata. */
	public static final String MATCH_METADATA_NBT_KEY = "MatchMetadata";
	/** Indicates if this filter should match NBT. */
	public static final String MATCH_NBT_NBT_KEY = "MatchNBT";
	/** Indicates if this filter should match item tags. */
	public static final String MATCH_TAGS_DICT_NBT_KEY = "MatchTags";

	public final ItemStack owningItemStack;
	private boolean whiteListMode = true;
	private boolean checkMetadata = false;
	private boolean checkNBT = false;
	private boolean checkOreDictionary = false;
	private boolean checkModDomain = false;
	private FilterTier filterTier;

	public InventoryItemFilter(ItemStack stack, FilterTier tier) {
		super(tier.getSlotCount());
		owningItemStack = stack;
		filterTier = tier;
		initialize();
	}

	public FilterTier getFilterTier() {
		return filterTier;
	}

	public void setMatchMetadata(boolean mode) {
		checkMetadata = mode;
	}

	public boolean getMatchMetadata() {
		return checkMetadata;
	}

	public void setMatchNBT(boolean mode) {
		checkNBT = mode;
	}

	public boolean getMatchNBT() {
		return checkNBT;
	}

	public void setMatchOreDictionary(boolean mode) {
		checkOreDictionary = mode;
	}

	public boolean getMatchOreDictionary() {
		return checkOreDictionary;
	}

	public void setMatchModeID(boolean mode) {
		checkModDomain = mode;
	}

	public boolean getMatchModID() {
		return checkModDomain;
	}

	public void setWhiteListMode(boolean mode) {
		whiteListMode = mode;
	}

	public boolean getWhiteListMode() {
		return whiteListMode;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT compound = super.serializeNBT();
		compound.putBoolean(WHITE_LIST_MODE_NBT_KEY, whiteListMode);
		compound.putBoolean(MATCH_METADATA_NBT_KEY, checkMetadata);
		compound.putBoolean(MATCH_NBT_NBT_KEY, checkNBT);
		compound.putBoolean(MATCH_TAGS_DICT_NBT_KEY, checkOreDictionary);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		super.deserializeNBT(compound);
		whiteListMode = compound.getBoolean(WHITE_LIST_MODE_NBT_KEY);
		checkMetadata = compound.getBoolean(MATCH_METADATA_NBT_KEY);
		checkNBT = compound.getBoolean(MATCH_NBT_NBT_KEY);
		checkOreDictionary = compound.getBoolean(MATCH_TAGS_DICT_NBT_KEY);
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	protected void onContentsChanged(int slot) {
		owningItemStack.setTag(serializeNBT());
	}

	private void initialize() {
		// If this ItemStack does not have an nbt tag, make one.
		if (owningItemStack.getTag() == null) {
			owningItemStack.setTag(new CompoundNBT());
		}
		// Create the default filter tag if one doesn't exist on this item, and then add
		// it as a sub tag to the item's nbt.
		if (!owningItemStack.getTag().contains(ITEM_FILTER_NBT_KEY)) {
			owningItemStack.getTag().put(ITEM_FILTER_NBT_KEY, serializeNBT());
		}

	}

	public String getName() {
		String name = "ItemFilter";
		switch (filterTier) {
		case BASIC:
			name = "BasicItemFilter";
			break;
		case UPGRADED:
			name = "UpgradedItemFilter";
			break;
		case ADVANCED:
			name = "AdvancedItemFilter";
			break;
		}
		return "container." + name;
	}
}