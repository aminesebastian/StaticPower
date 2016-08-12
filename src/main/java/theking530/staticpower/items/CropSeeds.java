package theking530.staticpower.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import theking530.staticpower.StaticPower;

public class CropSeeds extends ItemSeeds implements IPlantable {

    private final Block crops;
    
    @SuppressWarnings("static-access")
    public CropSeeds(String name, Block blockCrop, Block blockSoil) {
        super(blockCrop, blockSoil);
        crops = blockCrop;
        setCreativeTab(StaticPower.StaticPower);
        setRegistryName(name);
        setUnlocalizedName(name);
    }

    @SuppressWarnings("static-access")
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        net.minecraft.block.state.IBlockState state = worldIn.getBlockState(pos);
        if (facing == EnumFacing.UP && playerIn.canPlayerEdit(pos.offset(facing), facing, stack) 
        		&& state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.crops.getDefaultState());
            --stack.stackSize;
            return EnumActionResult.SUCCESS;
        }else{
            return EnumActionResult.FAIL;
        }
    }
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
    	return EnumPlantType.Crop;
    }
    @Override
    public IBlockState getPlant(net.minecraft.world.IBlockAccess world, BlockPos pos){
        return this.crops.getDefaultState();
    }
}