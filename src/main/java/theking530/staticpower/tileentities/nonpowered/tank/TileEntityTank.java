package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderTank;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;

public class TileEntityTank extends TileEntityBase {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_IRON = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.IRON),
			ModBlocks.IronTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.BASIC),
			ModBlocks.BasicTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.ADVANCED),
			ModBlocks.AdvancedTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.STATIC),
			ModBlocks.StaticTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.ENERGIZED),
			ModBlocks.EnergizedTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.LUMUM),
			ModBlocks.LumumTank);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityTank> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityTank>((type) -> new TileEntityTank(type, StaticPowerTiers.CREATIVE),
			ModBlocks.CreativeTank);

	public static final int MACHINE_TANK_CAPACITY_MULTIPLIER = 4;

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_IRON.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderTank::new);
		}
	}

	public final FluidContainerInventoryComponent inputFluidContainerComponent;
	public final FluidContainerInventoryComponent outputFluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final SideConfigurationComponent ioSideConfiguration;

	public TileEntityTank(TileEntityTypeAllocator<TileEntityTank> allocator, ResourceLocation tier) {
		super(allocator);

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);

		// Add the tank component.
		int capacity = SDMath.multiplyRespectingOverflow(tierObject.defaultTankCapacity.get(), MACHINE_TANK_CAPACITY_MULTIPLIER);
		registerComponent(
				fluidTankComponent = new FluidTankComponent("FluidTank", capacity).setCapabilityExposedModes(MachineSideMode.Regular, MachineSideMode.Input, MachineSideMode.Output));
		fluidTankComponent.setCanFill(true);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);
		enableFaceInteraction();

		// Add the side configuration component.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", (side, mode) -> {
		}, (side, mode) -> {
			return mode == MachineSideMode.Input || mode == MachineSideMode.Output || mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular;
		}, new MachineSideMode[] { MachineSideMode.Output, MachineSideMode.Input, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular }));

		// Add the inventory for the fluid containers.
		registerComponent(inputFluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent));
		registerComponent(outputFluidContainerComponent = new FluidContainerInventoryComponent("FluidDrainContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Add the two components to auto input and output fluids.
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", fluidTankComponent.getCapacity() / 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		// markTileEntityForSynchronization(); // Need to improve this.
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTank(windowId, inventory, this);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return true;
	}
}
