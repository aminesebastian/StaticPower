package theking530.staticpower.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class BlockEntityMachine extends BlockEntityBase {
	public final PowerStorageComponent powerStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	public BlockEntityMachine(BlockEntityTypeAllocator<? extends BlockEntityMachine> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);
		registerComponent(powerStorage = new TieredPowerStorageComponent("MainEnergyStorage", getTier(), true, false));
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				getDefaultSideConfiguration()));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));

		powerStorage.setSideConfiguration(ioSideConfiguration);
	}

	@Override
	public boolean shouldSerializeWhenBroken(Player player) {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack) {
		return true;
	}

	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return DefaultMachineNoFacePreset.INSTANCE;
	}
}
