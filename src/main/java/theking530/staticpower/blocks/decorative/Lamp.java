package theking530.staticpower.blocks.decorative;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import theking530.staticpower.StaticPower;

public class Lamp extends Block{

	public Lamp(String name) {
		super(Material.GLASS);
	    setLightLevel(1.0F);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);	
		setRegistryName(name);
	}
}
