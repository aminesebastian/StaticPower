package theking530.staticpower.items.crops;

import net.minecraft.world.level.block.Block;
import theking530.staticpower.blocks.StaticPowerItemBlock;

/**
 * Base class for any static power items that are responsible for planting a
 * crop.
 * 
 * @author Amine Sebastian
 *
 */
public class CropSeeds extends StaticPowerItemBlock {

	/**
	 * Creates a crop seed.
	 * 
	 * @param blockCrop The block this crop should plant.
	 */
	public CropSeeds(Block blockCrop) {
		super(blockCrop);
	}
}