package theking530.staticpower.integration.WAILA;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.machines.heatingelement.TileEntityHeatingElement;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.GUIUtilities;

public class WailaHeatingElementProvider implements IWailaDataProvider{

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
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileEntityHeatingElement te = (TileEntityHeatingElement) accessor.getTileEntity();
		
		String heatInfo = EnumTextFormatting.WHITE + "Current Heat: ";
		
		if(te.HEAT_STORAGE.getHeatedPercent() > .75) {
			heatInfo += EnumTextFormatting.YELLOW + "" + te.HEAT_STORAGE.getHeat() + "/" + te.HEAT_STORAGE.getMaxHeat() + "°";
		}else if(te.HEAT_STORAGE.getHeatedPercent() > .50) {
			heatInfo += EnumTextFormatting.GOLD + "" + te.HEAT_STORAGE.getHeat() + "/" + te.HEAT_STORAGE.getMaxHeat() + "°";	
		}else if(te.HEAT_STORAGE.getHeatedPercent() > .25) {
			heatInfo += EnumTextFormatting.RED + "" + te.HEAT_STORAGE.getHeat() + "/" + te.HEAT_STORAGE.getMaxHeat() + "°";
		}else{
			heatInfo += te.HEAT_STORAGE.getHeat() + "/" + te.HEAT_STORAGE.getMaxHeat() + "°";	
		}
		currenttip.add(heatInfo);
		
		//currenttip.add("Stored: " + GUIUtilities.getStoredPowerFormatted(te.STORAGE));
		
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
