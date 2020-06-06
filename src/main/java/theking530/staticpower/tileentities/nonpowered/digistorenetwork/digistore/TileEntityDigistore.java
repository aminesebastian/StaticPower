package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityDigistore extends BaseDigistoreTileEntity {
	public static final int DEFAULT_CAPACITY = 1024;

	public final InventoryComponent upgradesInventory;
	public final InventoryComponent outputInventory;

	private ItemStack storedItem;
	private int currentlyStoredAmount;
	private int maximumStorage;

	private boolean shouldVoid;
	private boolean locked;

	public TileEntityDigistore() {
		super(ModTileEntityTypes.DIGISTORE);
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		storedItem = ItemStack.EMPTY;
		maximumStorage = DEFAULT_CAPACITY;
		shouldVoid = false;
	}

	@Override
	public void process() {
		super.process();
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return super.onBlockActivated(state, player, hand, hit);
	}

	@Override
	public void onBlockLeftClicked(BlockState state, PlayerEntity player) {
		super.onBlockLeftClicked(state, player);
	}

	public ItemStack getStoredItem() {
		return storedItem;
	}

	public int getStoredAmount() {
		return currentlyStoredAmount;
	}

	public int getMaxStoredAmount() {
		return maximumStorage;
	}

	public boolean isFull() {
		return getStoredAmount() >= getMaxStoredAmount();
	}

	public float getFilledRatio() {
		return (float) getStoredAmount() / (float) getMaxStoredAmount();
	}

	public boolean isVoidUpgradeInstalled() {
		return shouldVoid;
	}

	public int getRemainingStorage(boolean checkForVoidUpgrade) {
		return checkForVoidUpgrade ? shouldVoid ? maximumStorage : maximumStorage - currentlyStoredAmount : maximumStorage - currentlyStoredAmount;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
		if (!locked && currentlyStoredAmount <= 0) {
			storedItem = ItemStack.EMPTY;
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistore(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.Digistore.getTranslationKey());
	}
}
