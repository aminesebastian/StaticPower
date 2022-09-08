package theking530.staticpower.cables.power;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class TileEntityPowerCable extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_BASIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_ADVANCED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_STATIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_ENERGIZED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_LUMUM = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_CREATIVE = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, false), ModBlocks.PowerCableCreative);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_BASIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ADVANCED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableAdvanced);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_STATIC = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_ENERGIZED = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_LUMUM = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableLumum);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPowerCable> TYPE_INDUSTRIAL_CREATIVE = new BlockEntityTypeAllocator<TileEntityPowerCable>(
			(allocator, pos, state) -> new TileEntityPowerCable(allocator, pos, state, true), ModBlocks.IndustrialPowerCableCreative);

	public final PowerCableComponent powerCableComponent;

	public TileEntityPowerCable(BlockEntityTypeAllocator<TileEntityPowerCable> allocator, BlockPos pos, BlockState state, boolean isIndustrial) {
		super(allocator, pos, state);
		double maxCurrent = isIndustrial ? getTierObject().cableIndustrialPowerMaxCurrent.get() : getTierObject().cablePowerMaxCurrent.get();
		double resistance = isIndustrial ? getTierObject().cableIndustrialPowerResistancePerBlock.get() : getTierObject().cablePowerResistancePerBlock.get();
		registerComponent(powerCableComponent = new PowerCableComponent("PowerCableComponent", isIndustrial, maxCurrent, resistance));
	}

	@Override
	public void process() {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPowerCable(windowId, inventory, this);
	}
}
