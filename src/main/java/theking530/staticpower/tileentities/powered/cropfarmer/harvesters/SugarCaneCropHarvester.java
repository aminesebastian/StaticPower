package theking530.staticpower.tileentities.powered.cropfarmer.harvesters;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SugarCaneBlock;
import theking530.staticpower.tileentities.powered.cropfarmer.IFarmerHarvester;
import theking530.staticpower.utilities.WorldUtilities;

public class SugarCaneCropHarvester implements IFarmerHarvester {

	@Override
	public HarvestResult harvest(Level level, BlockPos pos) {
		List<ItemStack> results = new ArrayList<ItemStack>();
		boolean harvested = false;
		for (int i = 1; i < 255; i++) {
			if (level.getBlockState(pos.offset(0, i, 0)).getBlock() instanceof SugarCaneBlock) {
				results.addAll(WorldUtilities.getBlockDrops(level, pos));
				level.destroyBlock(pos.offset(0, i, 0), false);
				harvested = true;
			} else {
				break;
			}
		}

		if (harvested) {
			return HarvestResult.byAxe(results);
		}
		return HarvestResult.empty();
	}
}
