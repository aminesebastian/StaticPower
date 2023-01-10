package theking530.staticpower.cables.power;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPowerCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_basic",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_advanced",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_static",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_energized",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_lumum",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_creative",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableCreative);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_BASIC = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_industrial_basic",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_ADVANCED = new BlockEntityTypeAllocator<BlockEntityPowerCable>(
			"cable_power_industrial_advanced", (allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_STATIC = new BlockEntityTypeAllocator<BlockEntityPowerCable>(
			"cable_power_industrial_static", (allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityPowerCable>(
			"cable_power_industrial_energized", (allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_LUMUM = new BlockEntityTypeAllocator<BlockEntityPowerCable>("cable_power_industrial_lumum",
			(allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPowerCable> TYPE_INDUSTRIAL_CREATIVE = new BlockEntityTypeAllocator<BlockEntityPowerCable>(
			"cable_power_industrial_creative", (allocator, pos, state) -> new BlockEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableCreative);

	public final PowerCableComponent powerCableComponent;

	public BlockEntityPowerCable(BlockEntityTypeAllocator<BlockEntityPowerCable> allocator, BlockPos pos, BlockState state, boolean isIndustrial) {
		super(allocator, pos, state);
		double maxPower = isIndustrial ? getTierObject().cablePowerConfiguration.cableIndustrialPowerMaxPower.get()
				: getTierObject().cablePowerConfiguration.cablerMaxCurrent.get();
		double resistance = isIndustrial ? getTierObject().cablePowerConfiguration.cableIndustrialPowerLossPerBlock.get()
				: getTierObject().cablePowerConfiguration.cablePowerLossPerBlock.get();
		registerComponent(powerCableComponent = new PowerCableComponent("PowerCableComponent", isIndustrial, StaticPowerVoltage.BONKERS, maxPower, resistance));
	}

	@Override
	public void process() {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPowerCable(windowId, inventory, this);
	}
}
