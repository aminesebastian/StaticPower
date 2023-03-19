package theking530.staticpower.blocks.tileentity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import theking530.staticcore.block.StaticCoreBlockEntityBlock;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.init.ModCreativeTabs;

public abstract class StaticPowerBlockEntityBlock extends StaticCoreBlockEntityBlock {
	protected StaticPowerBlockEntityBlock() {
		this(null, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerBlockEntityBlock(ResourceLocation tier) {
		this(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticPowerBlockEntityBlock(Properties properties) {
		this(null, properties);
	}

	protected StaticPowerBlockEntityBlock(ResourceLocation tier, Properties properties) {
		this(ModCreativeTabs.GENERAL, tier, properties);
	}

	protected StaticPowerBlockEntityBlock(CreativeModeTab tab, ResourceLocation tier, Properties properties) {
		super(tab, tier, properties);
	}

	@Override
	public BlockItem createItemBlock() {
		return new StaticPowerItemBlock(this);
	}
}