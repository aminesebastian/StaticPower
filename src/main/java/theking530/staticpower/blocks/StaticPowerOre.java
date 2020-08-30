package theking530.staticpower.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;
import theking530.staticcore.utilities.HarvestLevel;

/**
 * Base class for any static power ores.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerOre extends StaticPowerBlock {
	/** The minimum amount of XP spawned when this ore is mined. */
	private final int minimumXP;
	/** The maximum amount of XP spawned when this ore is mined. */
	private final int maximumXP;

	/**
	 * Creates a static power ore.
	 * 
	 * @param name         The registry name of the ore sans namespace.
	 * @param tool         The tool that must be used to harvest this ore.
	 * @param harvestLevel The harvest level of this ore.
	 * @param hardness     The hardness of this ore.
	 */
	public StaticPowerOre(String name, ToolType tool, HarvestLevel harvestLevel, float hardness) {
		this(name, tool, harvestLevel, hardness, 0, 0);
	}

	/**
	 * Creates a static power ore.
	 * 
	 * @param name         The registry name of the ore sans namespace.
	 * @param tool         The tool that must be used to harvest this ore.
	 * @param harvestLevel The harvest level of this ore.
	 * @param hardness     The hardness of this ore.
	 * @param minimumXP    The minimum amount of XP to spawn when this ore is mined.
	 * @param maximumXP    The maximum amount of XP to spawn when this ore is mined.
	 */
	public StaticPowerOre(String name, ToolType tool, HarvestLevel harvestLevel, float hardness, int minimumXP, int maximumXP) {
		super(name, Block.Properties.create(Material.ROCK).harvestTool(tool).harvestLevel(harvestLevel.ordinal()).hardnessAndResistance(hardness).sound(SoundType.STONE));
		this.minimumXP = minimumXP;
		this.maximumXP = maximumXP;
	}

	/**
	 * Gets the amount of XP spawned when this ore is mined.
	 */
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.IWorldReader reader, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? this.getExperience(RANDOM) : 0;
	}

	/**
	 * Gets the amount of XP to be spawned when this ore is mined.
	 * 
	 * @param rand An instance of {@link Random} to use in the calculation.
	 * @return The amount of XP to spawn, or 0 if no XP should be spawned.
	 */
	protected int getExperience(Random rand) {
		if (maximumXP > 0) {
			return MathHelper.nextInt(rand, minimumXP, maximumXP);
		}
		return 0;
	}
}
