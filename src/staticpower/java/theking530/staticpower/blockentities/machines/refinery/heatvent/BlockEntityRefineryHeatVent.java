package theking530.staticpower.blockentities.machines.refinery.heatvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatTicker;
import theking530.api.heat.HeatUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryHeatVent extends BaseRefineryBlockEntity implements IHeatStorage {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryHeatVent> TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryHeatVent>(
			"refinery_heat_vent", (type, pos, state) -> new BlockEntityRefineryHeatVent(pos, state),
			ModBlocks.RefineryHeatVent);

	public BlockEntityRefineryHeatVent(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.ADVANCED);
	}

	@Override
	public void process() {
		if (hasController()) {
			if (!getLevel().isClientSide()) {
				HeatUtilities.transferHeat(getLevel(), getController().heatStorage, getBlockPos(),
						HeatTransferAction.EXECUTE);
			}
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
			if (hasController()) {
				return getController().heatStorage.manuallyGetCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (hasController()) {
			return getController().createMenu(windowId, inventory, player);
		}
		return null;
	}

	public BlockPos getContainerReferencedBlockPos() {
		if (hasController()) {
			return getController().getContainerReferencedBlockPos();
		}
		return super.getContainerReferencedBlockPos();
	}

	@Override
	public float getTemperature() {
		if (hasController()) {
			return getController().heatStorage.getTemperature();
		}
		return 0;
	}

	@Override
	public float getOverheatThreshold() {
		if (hasController()) {
			return getController().heatStorage.getOverheatThreshold();
		}
		return 0;
	}

	@Override
	public float getMaximumTemperature() {
		if (hasController()) {
			return getController().heatStorage.getMaximumTemperature();
		}
		return 0;
	}

	@Override
	public float getConductivity() {
		if (hasController()) {
			return getController().heatStorage.getConductivity();
		}
		return 0;
	}

	@Override
	public float getMass() {
		if (hasController()) {
			return getController().heatStorage.getMass();
		}
		return 0;
	}

	@Override
	public float heat(float amountToHeat, HeatTransferAction action) {
		// Cannot heat directly through this.
		return 0;
	}

	@Override
	public float cool(float amountToCool, HeatTransferAction action) {
		// Cannot cool directly through this.
		return 0;
	}

	@Override
	public HeatTicker getTicker() {
		if (hasController()) {
			return getController().heatStorage.getTicker();
		}
		return null;
	}

	@Override
	public float getSpecificHeat() {
		if (hasController()) {
			return getController().heatStorage.getSpecificHeat();
		}
		return 0;
	}
}
