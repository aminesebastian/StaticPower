package theking530.staticpower.blockentities.machines.cropfarmer.harvesters;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import theking530.staticpower.blockentities.machines.cropfarmer.IFarmerHarvester;
import theking530.staticpower.utilities.WorldUtilities;

public class NetherWartCropHarvester implements IFarmerHarvester {

	@Override
	public HarvestResult harvest(Level level, BlockPos pos) {
		if (level.getBlockState(pos).getBlock() instanceof NetherWartBlock) {
			NetherWartBlock tempNetherwart = (NetherWartBlock) level.getBlockState(pos).getBlock();
			if (tempNetherwart.getPlant(level, pos).getValue(NetherWartBlock.AGE) >= 3) {
				List<ItemStack> results = WorldUtilities.getBlockDrops(level, pos);
				level.setBlock(pos, Blocks.NETHER_WART.defaultBlockState(), 1 | 2);
				return HarvestResult.byHoe(results);
			}
		}
		return HarvestResult.empty();
	}
}
