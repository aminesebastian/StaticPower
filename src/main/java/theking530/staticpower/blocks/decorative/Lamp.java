package theking530.staticpower.blocks.decorative;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import theking530.staticpower.blocks.StaticPowerBlock;

public class Lamp extends StaticPowerBlock {

	public Lamp(String name) {
		super(name, Block.Properties.of(Material.BUILDABLE_GLASS).sound(SoundType.GLASS).lightLevel((state) -> 15));
	}
}
