package theking530.staticpower.tileentities.nonpowered.miner;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import theking530.common.utilities.SDMath;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityMiner extends AbstractTileEntityMiner {

	public final InventoryComponent fuelInventory;
	public final InventoryComponent fuelBurningInventory;
	public final MachineProcessingComponent fuelComponent;
	public final MachineProcessingComponent fuelMoveComponent;

	public TileEntityMiner() {
		super(ModTileEntityTypes.MINER);
		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Input));
		registerComponent(fuelBurningInventory = new InventoryComponent("FuelBurningInventory", 1, MachineSideMode.Never));
		registerComponent(fuelMoveComponent = new MachineProcessingComponent("FuelMoveComponent", DEFAULT_FUEL_MOVE_TIME, this::canMoveFuel, this::canMoveFuel, this::moveFuel, true));
		registerComponent(fuelComponent = new MachineProcessingComponent("FuelComponent", 0, this::canStartProcessingFuel, this::canContinueProcessingFuel, this::fuelProcessingCompleted, true));
		registerComponent(new InputServoComponent("FuelInputServo", 20, fuelInventory));
	}

	@Override
	public void process() {
		super.process();

		// Randomly generate smoke and flame particles.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * RANDOM.nextFloat()) - 1.0f;
			if (SDMath.diceRoll(0.25f)) {
				randomOffset /= 3.5f;

				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
				getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
				getWorld().addParticle(ParticleTypes.FLAME, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
			}
		}
	}

	public boolean canMoveFuel() {
		return isValidFuel(fuelInventory.getStackInSlot(0)) && fuelBurningInventory.getStackInSlot(0).isEmpty() && hasDrillBit() && redstoneControlComponent.passesRedstoneCheck();
	}

	public boolean moveFuel() {
		int burnTime = getFuelBurnTime(fuelInventory.getStackInSlot(0));
		fuelComponent.setMaxProcessingTime(burnTime);
		transferItemInternally(fuelInventory, 0, fuelBurningInventory, 0);
		return true;
	}

	public boolean canStartProcessingFuel() {
		return !isDoneMining() && isValidFuel(fuelInventory.getStackInSlot(0)) && hasDrillBit() && redstoneControlComponent.passesRedstoneCheck();
	}

	public boolean canContinueProcessingFuel() {
		return !isDoneMining() && redstoneControlComponent.passesRedstoneCheck() && processingComponent.isPerformingWork();
	}

	public boolean fuelProcessingCompleted() {
		fuelComponent.setMaxProcessingTime(0);
		fuelBurningInventory.setStackInSlot(0, ItemStack.EMPTY);
		return true;
	}

	/**
	 * Checks to make sure we can mine.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		boolean superCall = super.canProcess();
		return superCall && getRemainingFuel() > 0;
	}

	@Override
	public void onBlockMined(BlockPos pos, BlockState minedBlock) {
		fuelComponent.setMaxProcessingTime(Math.min(fuelComponent.getCurrentProcessingTime() + getBlockMiningFuelCost(), fuelComponent.getMaxProcessingTime()));
		// IF we have reached the final block, set the current block index to -1.
		if (isDoneMining()) {
			fuelComponent.cancelProcessing();
		}
	}

	public int getFuelBurnTime(ItemStack input) {
		return ForgeHooks.getBurnTime(input);
	}

	public boolean isValidFuel(ItemStack input) {
		return ForgeHooks.getBurnTime(input) > 0;
	}

	public int getRemainingFuel() {
		return fuelComponent.getMaxProcessingTime() - fuelComponent.getCurrentProcessingTime();
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerMiner(windowId, inventory, this);
	}
}
