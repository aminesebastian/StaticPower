package theking530.staticpower.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public class GemOre extends Block{
	
	private Item droppedGem;
	private int dropMin;
	private int dropMax;
	
	public GemOre(String name, String tool, int level, Item gemDrop, int dropMin, int dropMax) {
		super(Material.ROCK);
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);	
		setRegistryName(name);
		setHarvestLevel(tool, level);
		//RegisterHelper.registerItem(new BaseItemBlock(this, name));
		droppedGem = gemDrop;
		this.dropMin = dropMin;
		this.dropMax = dropMax;
	}
    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return droppedGem;
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return this.quantityDropped(random) + random.nextInt(fortune + 1);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return (dropMin) + random.nextInt(Math.max(1, dropMax-dropMin));
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     */
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        if (this.getItemDropped(state, RANDOM, fortune) != Item.getItemFromBlock(this)) {
            return 1 + RANDOM.nextInt(5);
        }
        return 0;
    }
}