package theking530.staticpower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import theking530.staticpower.StaticPower;

public class Ore extends Block{
	
	public Ore(String name, String tool, int level) {
		super(Material.ROCK);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);	
		setRegistryName(name);
		setHarvestLevel(tool, level);
	}
}
