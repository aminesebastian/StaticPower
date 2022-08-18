package theking530.staticpower.tileentities.nonpowered.conveyors.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.control.ConveyorMotionComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorTileEntity;

public class TileEntityConveyorExtractor extends AbstractConveyorTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE_BASIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorExtractor(type, pos, state, StaticPowerTiers.BASIC), ModBlocks.ConveyorExtractorBasic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE_ADVANCED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorExtractor(type, pos, state, StaticPowerTiers.ADVANCED), ModBlocks.ConveyorExtractorAdvanced);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE_STATIC = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorExtractor(type, pos, state, StaticPowerTiers.STATIC), ModBlocks.ConveyorExtractorStatic);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE_ENERGIZED = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorExtractor(type, pos, state, StaticPowerTiers.ENERGIZED), ModBlocks.ConveyorExtractorEnergized);
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityConveyorExtractor> TYPE_LUMUM = new BlockEntityTypeAllocator<>(
			(type, pos, state) -> new TileEntityConveyorExtractor(type, pos, state, StaticPowerTiers.LUMUM), ModBlocks.ConveyorExtractorLumum);

	public final InventoryComponent internalInventory;
	protected AABB importBox;

	public TileEntityConveyorExtractor(BlockEntityTypeAllocator<TileEntityConveyorExtractor> type, BlockPos pos, BlockState state, ResourceLocation tier) {
		super(type, pos, state, tier);
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Input) {
			public int getSlotLimit(int slot) {
				return 1;
			}
		}.setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(true));
		registerComponent(new InputServoComponent("InputServo", 20, internalInventory));
		enableFaceInteraction();
	}

	@Override
	public void process() {
		// Do nothing on the client.
		if (level.isClientSide) {
			return;
		}

		// Do nothing if the internal inventory is empty.
		if (internalInventory.getStackInSlot(0).isEmpty()) {
			return;
		}

		// Extract the stored item.
		ItemStack extracted = internalInventory.extractItem(0, StaticPowerConfig.getTier(tier).conveyorExtractorStackSize.get(), false);

		// Get the facing direction.
		Direction facing = getFacingDirection();
		Vector3D offset = new Vector3D(facing);
		offset.multiply(0.3f);

		// Create the entity and spawn it.
		ConveyorBeltEntity newEntity = new ConveyorBeltEntity(level, getBlockPos().getX() + 0.5f + offset.getX(), getBlockPos().getY() + 0.5f,
				getBlockPos().getZ() + 0.5f + offset.getZ(), extracted);
		newEntity.setDeltaMovement(0, 0, 0);
		level.addFreshEntity(newEntity);
	}

	@Override
	protected void configureConveyorComponent(ConveyorMotionComponent component, StaticPowerTier tier, Level world, BlockPos pos, BlockState state) {
		component.updateBounds(new AABB(pos.getX(), pos.getY() + 0.5, pos.getZ(), pos.getX() + 1, pos.getY() + 0.55, pos.getZ() + 1));
		component.setShouldAffectEntitiesAbove(false);
		component.setVelocity(new Vector3D(-(float) (0.05f * tier.conveyorSpeedMultiplier.get()), 0f, 0f));
		// Make sure the front is input only.
		Direction facing = getFacingDirection();
		ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, facing), MachineSideMode.Input);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.FRONT) {
			return mode == MachineSideMode.Input;
		} else {
			return mode == MachineSideMode.Never;
		}
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return new DefaultSideConfiguration() {
			@Override
			public boolean getSideDefaultEnabled(BlockSide side) {
				if (side == BlockSide.FRONT) {
					return true;
				}
				return false;
			}

			@Override
			public MachineSideMode getSideDefaultMode(BlockSide side) {
				if (side == BlockSide.FRONT) {
					return MachineSideMode.Input;
				}
				return MachineSideMode.Never;
			}
		};
	}
}
