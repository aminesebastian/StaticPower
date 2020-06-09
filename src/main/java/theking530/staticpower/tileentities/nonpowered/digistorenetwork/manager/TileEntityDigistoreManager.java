package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.network.TileEntityNetwork;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	public final InventoryComponent upgradesInventory;
	private TileEntityNetwork<BaseDigistoreTileEntity> network;

	public TileEntityDigistoreManager() {
		super(ModTileEntityTypes.DIGISTORE_MANAGER);
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
	}

	@Override
	public void process() {
		if (network == null) {
			return;
		}
		updateGrid();
		for (BlockPos blocks : network.getAllNetworkTiles().keySet()) {
			if (world.isRemote) {
				world.addParticle(ParticleTypes.END_ROD, blocks.getX() + 0.5f, blocks.getY() + 0.5f, blocks.getZ() + 0.5f, 0, 0.1f, 0);
			}
		}
	}

	public TileEntityNetwork<BaseDigistoreTileEntity> getNetwork() {
		return network;
	}

	private void updateGrid() {
		if (network != null) {
			network.updateNetwork(this, true);
			for (BaseDigistoreTileEntity digistoreBase : network.getAllNetworkTiles().values()) {
				digistoreBase.setManager(this);
			}
		} else {
			StaticPower.LOGGER.error(String.format("A Digistore Manager at position: %1$s attempted to update a null network.", getPos()));
		}
	}

	@Override
	public void onPlaced(BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onPlaced(state, placer, stack);
		network = new TileEntityNetwork<BaseDigistoreTileEntity>(getWorld(), this::isValidTileEntityForNetwork, this::isValidExtenderForNetwork);
		updateGrid();
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		for (Entry<BlockPos, BaseDigistoreTileEntity> entry : network.getAllNetworkTiles().entrySet()) {
			entry.getValue().setManager(null);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.DigistoreManager.getTranslationKey());
	}

	private boolean isValidTileEntityForNetwork(TileEntity tileEntity, Direction dir) {
		return tileEntity instanceof BaseDigistoreTileEntity;
	}

	private boolean isValidExtenderForNetwork(BlockPos position, Direction dir) {
		if (world.getBlockState(pos).getBlock() == ModBlocks.DigistoreWire) {
			return true;
		} else if (world.getTileEntity(position) instanceof BaseDigistoreTileEntity) {
			return true;
		}
		return false;

	}
}
