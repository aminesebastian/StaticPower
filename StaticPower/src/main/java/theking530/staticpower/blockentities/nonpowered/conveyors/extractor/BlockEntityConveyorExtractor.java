package theking530.staticpower.blockentities.nonpowered.conveyors.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.FrontInputOnly;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.Vector3D;
import theking530.staticpower.blockentities.components.ConveyorMotionComponent;
import theking530.staticpower.blockentities.nonpowered.conveyors.AbstractConveyorBlockEntity;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityConveyorExtractor extends AbstractConveyorBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityConveyorExtractor> TYPE = new BlockEntityTypeAllocator<>("conveyor_extractor",
			(type, pos, state) -> new BlockEntityConveyorExtractor(type, pos, state), ModBlocks.ConveyorsExtractor.values());

	public final InventoryComponent internalInventory;
	public final SideConfigurationComponent ioSideConfiguration;
	protected AABB importBox;

	public BlockEntityConveyorExtractor(BlockEntityTypeAllocator<BlockEntityConveyorExtractor> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Input) {
			public int getSlotLimit(int slot) {
				return 1;
			}
		}.setCapabilityExtractEnabled(false).setCapabilityInsertEnabled(true));
		registerComponent(new InputServoComponent("InputServo", 20, internalInventory));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", FrontInputOnly.INSTANCE));
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
		ItemStack extracted = internalInventory.extractItem(0, getTierObject().conveyorExtractorStackSize.get(), false);

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
}
