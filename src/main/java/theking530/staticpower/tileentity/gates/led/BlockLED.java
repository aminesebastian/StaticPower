package theking530.staticpower.tileentity.gates.led;

import javax.annotation.Nullable;

import api.IWrenchTool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.tileentity.gates.BlockLogicGate;
import theking530.staticpower.utils.Color;

public class BlockLED extends BlockLogicGate {

    public static final PropertyInteger SIDE = PropertyInteger.create("side", 0, 5);
	
	public BlockLED(String name) {
		super(name, 0);
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityLED tempLED = (TileEntityLED) world.getTileEntity(pos);
			if(heldItem != null) {
				if(heldItem.getItem() instanceof ItemDye) {
					tempLED.setColor(Color.values()[heldItem.getMetadata()]);
					return true;	
				}
			}
			if(player.isSneaking()) {
				tempLED.cycleColor(true);					
			}else{
				tempLED.cycleColor(false);	
			}
			player.addChatComponentMessage(new TextComponentString("Color Set: " + tempLED.COLOR));
    		return true;
    	}
		return true;
	}
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
    	if(world.getTileEntity(pos) instanceof TileEntityLED) {
			TileEntityLED tempMachine = (TileEntityLED)world.getTileEntity(pos);
			if(placer.getHeldItemMainhand().hasTagCompound()) {
				tempMachine.onMachinePlaced(placer.getHeldItemMainhand().getTagCompound());
			}
		}
    }
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos){
		return super.getLightValue(state, world, pos);
    }
	@Override
	public void wrenchBlock(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, boolean returnDrops) {
		if (!world.isRemote) {
			TileEntityLED tempLED = (TileEntityLED) world.getTileEntity(pos);
			tempLED.INVERTED = !tempLED.INVERTED;
			if(tempLED.INVERTED) {
				player.addChatComponentMessage(new TextComponentString("Behaviour: Inverted"));
			}else{
				player.addChatComponentMessage(new TextComponentString("Behaviour: Regular"));
			}
		}
	}
	public String getDescrption(ItemStack stack){
		if(stack.hasTagCompound()) {
			return "Color: " + Color.values()[stack.getTagCompound().getInteger("COLOR")].toString();
		}
		return null;	
	}
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityLED();
	}
}
