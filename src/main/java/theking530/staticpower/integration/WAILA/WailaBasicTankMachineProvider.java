package theking530.staticpower.integration.WAILA;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;

public class WailaBasicTankMachineProvider implements IWailaDataProvider{

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return accessor.getStack();
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		BaseMachineWithTank tempMachine = (BaseMachineWithTank) accessor.getTileEntity();
		String percentage = "Progress: " + (int)(((float)tempMachine.PROCESSING_TIMER/(float)tempMachine.PROCESSING_TIME)*100) + "%";
		String storedString;
		String fluid = tempMachine.TANK.getFluid() == null ? "Fluid: Empty" : "Fluid: " + tempMachine.TANK.getFluid().getLocalizedName() + " (" + tempMachine.TANK.getFluidAmount() + "mB)";
		currenttip.add(percentage);
		if(tempMachine.STORAGE.getEnergyRatio() >= .75) {
			storedString = EnumTextFormatting.GREEN.toString();
		}else if(tempMachine.STORAGE.getEnergyRatio() >= .25){
			storedString = EnumTextFormatting.WHITE.toString();
		}else{
			storedString = EnumTextFormatting.RED.toString();
		}
		storedString +="Stored: " + GUIUtilities.getStoredPowerFormatted(tempMachine.STORAGE);
		currenttip.add(storedString);	
		currenttip.add(fluid);
		if(accessor.getPlayer().isSneaking()){
			if(tempMachine.CURRENT_RF_TICK > 0) {
				currenttip.add(EnumTextFormatting.GREEN + "" + "+" + NumberFormat.getNumberInstance(Locale.US).format(tempMachine.CURRENT_RF_TICK) + " RF/t");
			}else if(tempMachine.CURRENT_RF_TICK < 0){
				currenttip.add(EnumTextFormatting.RED + "" +NumberFormat.getNumberInstance(Locale.US).format(tempMachine.CURRENT_RF_TICK) + " RF/t");	
			}else{
				currenttip.add(EnumTextFormatting.WHITE + "" +  NumberFormat.getNumberInstance(Locale.US).format(tempMachine.CURRENT_RF_TICK) + " RF/t");
			}
			currenttip.add("Output: " + EnumTextFormatting.WHITE +GUIUtilities.getMaxExtractFormatted(tempMachine.STORAGE));
			currenttip.add("Input: " + EnumTextFormatting.WHITE +GUIUtilities.getMaxRecieveFormatted(tempMachine.STORAGE));
		}else{
			currenttip.add(EnumTextFormatting.ITALIC + "(Hold Shift)");
		}
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world,
			BlockPos pos) {
		return tag;
	}
}
