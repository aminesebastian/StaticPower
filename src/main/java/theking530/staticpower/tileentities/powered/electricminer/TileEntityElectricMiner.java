package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.nonpowered.miner.AbstractTileEntityMiner;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityElectricMiner extends AbstractTileEntityMiner {
	public final EnergyStorageComponent energyStorage;
	public final InventoryComponent upgradesInventory;
	public final InventoryComponent batteryInventory;

	public TileEntityElectricMiner() {
		super(ModTileEntityTypes.ELECTRIC_MINER);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", TileEntityMachine.DEFAULT_RF_CAPACITY, TileEntityMachine.DEFAULT_POWER_TRANSFER * 10,
				TileEntityMachine.DEFAULT_POWER_TRANSFER * 10));
		
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));

		setBlockMiningFuelCost(getBlockMiningFuelCost() * 100);
		setIdleFuelCost(getIdleFuelCost() * 100);
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
		if (processingComponent.getIsOnBlockState()) {
			energyStorage.usePower(getIdleFuelCost());
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
