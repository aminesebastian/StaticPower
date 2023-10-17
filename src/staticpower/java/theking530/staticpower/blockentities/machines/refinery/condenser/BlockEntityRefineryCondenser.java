package theking530.staticpower.blockentities.machines.refinery.condenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.utilities.math.Vector4D;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController.RefineryTowerInfo;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryCondenser extends BaseRefineryBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryCondenser> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryCondenser>(
			"refinery_condenser", (type, pos, state) -> new BlockEntityRefineryCondenser(pos, state),
			ModBlocks.RefineryCondenser);

	public final SideConfigurationComponent ioSideConfiguration;
	public final FluidTankComponent tank;
	private int recipeTankIndex;

	public BlockEntityRefineryCondenser(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		recipeTankIndex = -1;

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				RefineryCondenserConfiguration.INSTANCE));

		registerComponent(tank = new FluidTankComponent("FluidTankOutput", 100));
		tank.setCapabilityExposedModes(MachineSideMode.Output);

		registerComponent(new FluidOutputServoComponent("FluidOutputServo", 100, tank, MachineSideMode.Output));
	}

	@Override
	public void process() {
		if (SDMath.diceRoll(0.5f)) {
			Vector4D randomVector = SDMath.getRandomVectorOffset();
			randomVector.setX(randomVector.getX() * 0.01f);
			randomVector.setZ(randomVector.getZ() * 0.01f);
			getLevel().addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + 0.45f + randomVector.getX(),
					getBlockPos().getY() + 1.1f, getBlockPos().getZ() + 0.5f + randomVector.getZ(), 0.0f, 0.0f, 0.0f);
		}
	}

	public int getRecipeTankIndex() {
		return recipeTankIndex;
	}

	public boolean hasRecipeTankIndex() {
		return recipeTankIndex >= 0 && recipeTankIndex < 3;
	}

	@Override
	public void multiblockStateChanged(MultiblockState state) {
		super.multiblockStateChanged(state);
		if (state.isWellFormed()) {
			recipeTankIndex = calculateAttachedTank();
		} else {
			recipeTankIndex = -1;
		}
	}

	private int calculateAttachedTank() {
		if (!hasController()) {
			return -1;
		}

		Direction towerTestSide = getFacingDirection().getOpposite();
		RefineryTowerInfo towerInfo = getController().getTowerInfoForPos(getBlockPos().relative(towerTestSide));
		if (!towerInfo.isValid()) {
			return -1;
		}

		int distanceFromBase = getBlockPos().getY() - towerInfo.base().getY();
		if (distanceFromBase < 0 || distanceFromBase > 2) {
			return -1;
		}
		return distanceFromBase;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (hasController()) {
			return new ContainerRefineryCondenser(windowId, inventory, this);
		}
		return null;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap != ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.empty();
		}

		if (side != null && !ioSideConfiguration.getWorldSpaceDirectionConfiguration(side).isOutputMode()) {
			return LazyOptional.empty();
		}

		return LazyOptional.of(() -> tank).cast();
	}

}
