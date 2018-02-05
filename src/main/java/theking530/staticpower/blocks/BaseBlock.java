package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import theking530.staticpower.StaticPower;

public class BaseBlock extends Block{

	public BaseBlock(Material materialIn, String name) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHarvestLevel("pickaxe", 0);
		setCreativeTab(StaticPower.StaticPower);
	}
	public BaseBlock(Material materialIn, String name, String tool, int level) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setHarvestLevel(tool, level);
		setCreativeTab(StaticPower.StaticPower);
	}
}
