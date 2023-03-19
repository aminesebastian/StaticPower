package theking530.staticpower.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticpower.init.ModCreativeTabs;

public class StaticPowerBlock extends StaticCoreBlock {

	public StaticPowerBlock(Block.Properties properties) {
		this(ModCreativeTabs.GENERAL, properties);
	}

	public StaticPowerBlock(Material material) {
		this(ModCreativeTabs.GENERAL, material, 1.0f);
	}

	public StaticPowerBlock(CreativeModeTab tab, Block.Properties properties) {
		super(tab, properties);
	}

	public StaticPowerBlock(CreativeModeTab tab, Material material, float hardnessAndResistance) {
		this(tab, Block.Properties.of(material).strength(hardnessAndResistance));
	}

	public StaticPowerBlock(CreativeModeTab tab, Material material) {
		super(tab, material, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level, and hardness/resistance.
	 * 
	 * @param material              The {@link Material} this block is made of.
	 * @param tool                  The {@link ToolType} this block should be
	 *                              harvested by.
	 * @param hardnessAndResistance The hardness and resistance of this block.
	 */
	public StaticPowerBlock(Material material, float hardnessAndResistance) {
		super(ModCreativeTabs.GENERAL, Block.Properties.of(material).strength(hardnessAndResistance));
	}

	@Override
	public BlockItem createItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}
