package theking530.staticpower.blockentities.machines.pumpjack;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.blockentities.machines.pump.BlockEntityPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPumpJack extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPumpJack> TYPE = new BlockEntityTypeAllocator<BlockEntityPumpJack>(
			(type, pos, state) -> new BlockEntityPumpJack(type, pos, state), ModBlocks.PumpJack);

	public final MachineProcessingComponent processingComponent;
	public final BatteryInventoryComponent batteryInventory;

	@UpdateSerialize
	public int pumpRate;

	public BlockEntityPumpJack(BlockEntityTypeAllocator<BlockEntityPumpJack> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = getTierObject();
		pumpRate = tierObject.pumpRate.get();

		// Register the processing component to handle the pumping.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate, this::canProcess, this::canProcess, this::pump, true)
				.setRedstoneControlComponent(redstoneControlComponent).setPowerComponent(powerStorage));

		// Battery
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		// Set the default side configuration.
		ioSideConfiguration.setDefaultConfiguration(new DefaultSideConfiguration().setSide(BlockSide.TOP, true, MachineSideMode.Input), true);

		// Enable face interaction.
		enableFaceInteraction();
	}

	public ProcessingCheckState canProcess() {
		if (!this.powerStorage.canSupplyPower(StaticPowerConfig.SERVER.pumpPowerUsage.get())) {
			return ProcessingCheckState.notEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get());
		}

		if (getPumpTarget() == null) {
			return ProcessingCheckState.error("Missing pump to jack!");
		}

		return ProcessingCheckState.ok();
	}

	public ProcessingCheckState pump() {
		return ProcessingCheckState.ok();
	}

	@Override()
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.TOP && mode != MachineSideMode.Never) {
			return false;
		}
		return mode == MachineSideMode.Output;
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.TOP_SIDE_ONLY_OUTPUT;
	}

	public BlockEntityPump getPumpTarget() {
		BlockEntity below = getLevel().getBlockEntity(getBlockPos().below());
		if (below != null && below instanceof BlockEntityPump) {
			return (BlockEntityPump) below;
		}
		return null;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPumpJack(windowId, inventory, this);
	}
}
