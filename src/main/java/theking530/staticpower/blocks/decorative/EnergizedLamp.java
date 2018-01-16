package theking530.staticpower.blocks.decorative;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.blocks.BaseItemBlock;

public class EnergizedLamp extends Block{

	public EnergizedLamp(Material material) {
		super(material.GLASS);
		setLightLevel(1.0F);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName("EnergizedLamp");	
		setRegistryName("EnergizedLamp");
		//RegisterHelper.registerItem(new BaseItemBlock(this, "EnergizedLamp"));
	}
}
