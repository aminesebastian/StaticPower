package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.SDMath;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.nonpowered.miner.AbstractTileEntityMiner;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityElectricMiner extends AbstractTileEntityMiner {
	public final EnergyStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final InventoryComponent batteryInventory;

	public TileEntityElectricMiner() {
		super(ModTileEntityTypes.ELECTRIC_MINER);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", TileEntityMachine.DEFAULT_RF_CAPACITY, TileEntityMachine.DEFAULT_POWER_TRANSFER * 10,
				TileEntityMachine.DEFAULT_POWER_TRANSFER * 10));

		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
		processingComponent.setUpgradeInventory(upgradesInventory);
		setBlockMiningFuelCost(getBlockMiningFuelCost() * 100);
		setIdleFuelCost(getIdleFuelCost() * 100);
		
		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	/**
	 * Checks to make sure we can mine.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		boolean superCall = super.canProcess();
		return superCall && energyStorage.hasEnoughPower(Math.max(getIdleFuelCost(), getBlockMiningFuelCost()));
	}

	@Override
	public void process() {
		super.process();
		// Use the idle power.
		if (processingComponent.isPerformingWork()) {
			energyStorage.usePower(getIdleFuelCost());
		}

		// Render the particle effects.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * RANDOM.nextFloat()) - 1.0f;
			randomOffset /= 3.5f;
			float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
			Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
			getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
			if (SDMath.diceRoll(0.25f)) {
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.0f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.01f, 0.0f);
			}
		}
	}

	@Override
	public void onBlockMined(BlockPos pos, BlockState minedBlock) {
		energyStorage.usePower(getBlockMiningFuelCost());
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerElectricMiner(windowId, inventory, this);
	}
}
