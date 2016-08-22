package theking530.staticpower.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.assists.Reference;

public class AdvancedEarth extends BlockFarmland{

	public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, 7);
    protected static final AxisAlignedBB FARMLAND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    protected AdvancedEarth() {
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, Integer.valueOf(0)));
        this.setTickRandomly(true);
        this.setLightOpacity(255);
		setRegistryName("AdvancedEarth");
		setUnlocalizedName(Reference.MODID + "_" + "AdvancedEarth");
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FARMLAND_AABB;
    }
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
    public boolean isFullCube(IBlockState state){
        return false;
    }
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int i = ((Integer)state.getValue(MOISTURE)).intValue();
    }
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
    }
    private boolean hasCrops(World worldIn, BlockPos pos){
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof net.minecraftforge.common.IPlantable && canSustainPlant(worldIn.getBlockState(pos), worldIn, pos, net.minecraft.util.EnumFacing.UP, (net.minecraftforge.common.IPlantable)block);
    }
    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.AdvancedEarth);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.AdvancedEarth));
    }	
}
