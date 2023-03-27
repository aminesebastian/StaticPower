package theking530.staticcore.block;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public abstract class StaticCoreTieredBlock extends StaticCoreBlock implements EntityBlock {
	@Nullable
	private final ResourceLocation tier;

	protected StaticCoreTieredBlock(CreativeModeTab tab, @Nullable ResourceLocation tier) {
		this(tab, tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticCoreTieredBlock(CreativeModeTab tab, @Nullable ResourceLocation tier, Properties properties) {
		super(tab, properties);
		this.tier = tier;
	}

	public boolean hasTier() {
		return tier != null;
	}

	public ResourceLocation getTier() {
		return tier;
	}
}