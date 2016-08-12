package theking530.staticpower.blocks.decorative;

import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class LumumLamp extends Block{

	public LumumLamp(Material material) {
		super(material.GLASS);
		setLightLevel(1.0F);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("LumumLamp");	
		setRegistryName("LumumLamp");
		RegisterHelper.registerItem(new BaseItemBlock(this, "LumumLamp"));
	}
}
