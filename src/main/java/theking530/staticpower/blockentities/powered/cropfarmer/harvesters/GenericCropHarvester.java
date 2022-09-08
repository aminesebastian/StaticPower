package theking530.staticpower.blockentities.powered.cropfarmer.harvesters;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import theking530.staticpower.blockentities.powered.cropfarmer.IFarmerHarvester;
import theking530.staticpower.utilities.WorldUtilities;

public class GenericCropHarvester implements IFarmerHarvester {

	@Override
	public HarvestResult harvest(Level level, BlockPos pos) {
		// If the current position is an instance of a CropsBlock.
		if (level.getBlockState(pos).getBlock() instanceof CropBlock) {
			// Get the block and check if it is of max age.
			CropBlock tempCrop = (CropBlock) level.getBlockState(pos).getBlock();
			// If the crop is fully grown, harvest it.
			if (tempCrop.isMaxAge(level.getBlockState(pos))) {
				// Harvest the provided position, set the age of the crop back to 0.
				List<ItemStack> results = WorldUtilities.getBlockDrops(level, pos);
				level.setBlock(pos, tempCrop.getStateForAge(0), 1 | 2);
				level.playSound(null, pos, level.getBlockState(pos).getBlock().getSoundType(level.getBlockState(pos), level, pos, null).getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
				return HarvestResult.byHoe(results);
			}
		}
		return HarvestResult.empty();
	}
}
