package theking530.staticpower.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

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
	 * @param hardness The hardness of this ore.
	 */
	public StaticPowerOre(float hardness) {
		this(hardness, 0, 0);
	}

	/**
	 * Creates a static power ore.
	 * 
	 * @param tool         The tool that must be used to harvest this ore.
	 * @param harvestLevel The harvest level of this ore.
	 * @param hardness     The hardness of this ore.
	 * @param minimumXP    The minimum amount of XP to spawn when this ore is mined.
	 * @param maximumXP    The maximum amount of XP to spawn when this ore is mined.
	 */
	public StaticPowerOre(float hardness, int minimumXP, int maximumXP) {
		this(Block.Properties.of(Material.STONE).strength(hardness).sound(SoundType.STONE), 0, 0);

	}

	public StaticPowerOre(Block.Properties properties) {
		this(properties, 0, 0);

	}

	public StaticPowerOre(Block.Properties properties, int minimumXP, int maximumXP) {
		super(properties);
		this.minimumXP = minimumXP;
		this.maximumXP = maximumXP;
	}

	/**
	 * Gets the amount of XP spawned when this ore is mined.
	 */
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader reader, BlockPos pos, int fortune,
			int silktouch) {
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
			return Mth.nextInt(rand, minimumXP, maximumXP);
		}
		return 0;
	}
}
