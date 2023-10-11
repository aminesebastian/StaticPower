package theking530.staticpower.blockentities.machines.electricheater;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.PowerStack;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityElectricHeater extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityElectricHeater> TYPE = new BlockEntityTypeAllocator<>(
			"electric_heater", (type, pos, state) -> new BlockEntityElectricHeater(pos, state),
			ModBlocks.ElectricHeater);

	public final BatteryInventoryComponent batteryInventory;
	public final HeatStorageComponent heatStorage;
	private float powerUsage;

	public BlockEntityElectricHeater(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		StaticCoreTier tier = getTierObject();
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tier));
		heatStorage.setMaxHeat(1000);
		powerStorage.setMaximumInputPower(1000);
		powerStorage.setMaximumOutputPower(1000);
		powerStorage.setCapacity(5000);
		powerUsage = 1000;
	}

	@Override
	public void process() {
		super.process();
		powerUsage = 1000;

		if (!getLevel().isClientSide() && redstoneControlComponent.passesRedstoneCheck()) {
			PowerStack simulatedDraw = powerStorage.drainPower(getPowerUsagePerTick(), true);
			if (!simulatedDraw.isEmpty()) {
				float addedHeat = heatStorage.heat((float) simulatedDraw.getPower(), HeatTransferAction.EXECUTE);
				powerStorage.drainPower(addedHeat, false);
			}
		}
	}

	public float getHeatGenerationPerTick() {
		return powerUsage;
	}

	public float getPowerUsagePerTick() {
		return powerUsage;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerElectricHeater(windowId, inventory, this);
	}
}