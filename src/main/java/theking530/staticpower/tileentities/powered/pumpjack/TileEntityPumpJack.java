package theking530.staticpower.tileentities.powered.pumpjack;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.tileentities.powered.pump.TileEntityPump;

public class TileEntityPumpJack extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPumpJack> TYPE = new BlockEntityTypeAllocator<TileEntityPumpJack>(
			(type, pos, state) -> new TileEntityPumpJack(type, pos, state, StaticPowerTiers.IRON), ModBlocks.PumpJack);

	public final MachineProcessingComponent processingComponent;
	public final BatteryInventoryComponent batteryInventory;

	@UpdateSerialize
	public int pumpRate;

	public TileEntityPumpJack(BlockEntityTypeAllocator<TileEntityPumpJack> allocator, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(allocator, pos, state, tier);

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		pumpRate = tierObject.pumpRate.get();

		// Register the processing component to handle the pumping.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate, this::canProcess, this::canProcess, this::pump, true)
				.setRedstoneControlComponent(redstoneControlComponent).setEnergyComponent(energyStorage));

		// Battery
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));

		// Set the default side configuration.
		ioSideConfiguration.setDefaultConfiguration(new DefaultSideConfiguration().setSide(BlockSide.TOP, true, MachineSideMode.Input));

		// Enable face interaction.
		enableFaceInteraction();
	}

	public ProcessingCheckState canProcess() {
		if (!this.energyStorage.hasEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get())) {
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

	public TileEntityPump getPumpTarget() {
		BlockEntity below = getLevel().getBlockEntity(getBlockPos().below());
		if (below != null && below instanceof TileEntityPump) {
			return (TileEntityPump) below;
		}
		return null;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPumpJack(windowId, inventory, this);
	}
}
