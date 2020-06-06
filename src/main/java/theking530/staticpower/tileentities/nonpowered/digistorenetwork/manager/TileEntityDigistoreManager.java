package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import java.util.Map.Entry;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.DigistoreNetwork;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	public final InventoryComponent upgradesInventory;
	private DigistoreNetwork network;

	public TileEntityDigistoreManager() {
		super(ModTileEntityTypes.DIGISTORE_MANAGER);
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
	}

	@Override
	public void process() {
		updateGrid();
	}

	public DigistoreNetwork getNetwork() {
		return network;
	}

	private void updateGrid() {
		network = new DigistoreNetwork(this);
		network.updateGrid();
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		updateGrid();
	}

	@Override
	public void onBroken() {
		for (Entry<BlockPos, BaseDigistoreTileEntity> entry : network.getMasterList().entrySet()) {
			entry.getValue().setManager(null);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
}
