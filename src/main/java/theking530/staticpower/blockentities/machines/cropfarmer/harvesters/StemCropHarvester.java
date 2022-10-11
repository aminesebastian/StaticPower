package theking530.staticpower.blockentities.machines.cropfarmer.harvesters;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MelonBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import theking530.staticpower.blockentities.machines.cropfarmer.IFarmerHarvester;
import theking530.staticpower.utilities.WorldUtilities;

public class StemCropHarvester implements IFarmerHarvester {

	@Override
	public HarvestResult harvest(Level level, BlockPos pos) {
		Block stem = level.getBlockState(pos).getBlock();
		if (stem instanceof AttachedStemBlock) {
			for (Direction dir : Direction.values()) {
				if (dir.getAxis() == Direction.Axis.Y) {
					continue;
				}
				HarvestResult result = harvestStemCrop(level, pos.relative(dir));
				if (result != null) {
					return result;
				}
			}
		}
		return HarvestResult.empty();
	}

	private HarvestResult harvestStemCrop(Level level, BlockPos pos) {
		Block block = level.getBlockState(pos).getBlock();
		if (block instanceof MelonBlock || block instanceof PumpkinBlock) {
			List<ItemStack> results = WorldUtilities.getBlockDrops(level, pos);
			level.destroyBlock(pos, false);
			return HarvestResult.byAxe(results);
		}
		return null;
	}
}
