package theking530.staticpower.blocks.decorative;


import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.blocks.StaticPowerBlock;

public class Lamp extends StaticPowerBlock {

	public Lamp(String name) {
		super(name, Block.Properties.create(Material.REDSTONE_LIGHT).harvestTool(ToolType.PICKAXE).sound(SoundType.GLASS).lightValue(15));
	}
}
