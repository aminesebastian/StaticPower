package theking530.staticpower.tileentity.digistorenetwork.digistore;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.utilities.EnumTextFormatting;

public class DigistoreItemBlock extends StaticPowerItemBlock {

	public DigistoreItemBlock(Block block, String name) {
		super(block, name);
	}

	
	@Override  
	public void addInformation(ItemStack itemstack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
    	if(itemstack.hasTag()) {
    	    if(showHiddenTooltips()) {
    			CompoundNBT nbt = itemstack.getTag();
    			
    			ItemStack storedItem = new ItemStack(nbt.getCompoundTag("STORED_ITEM"));
    			int storedAmount = nbt.getInteger("STORED_AMOUNT");
    			
    			ItemStackHandler slotsUpgrades = new ItemStackHandler();
    			slotsUpgrades.deserializeNBT((CompoundNBT) nbt.getTag("UPGRADES"));
    			
    			if(!storedItem.isEmpty()) {
    				list.add(EnumTextFormatting.AQUA + "Contains: " + EnumTextFormatting.WHITE + storedItem.getDisplayName());
    				list.add(EnumTextFormatting.GREEN + "Count: " + EnumTextFormatting.WHITE + storedAmount);
    			}
    			boolean hasUpgrade = false;
    			for(int i=0; i<slotsUpgrades.getSlots(); i++) {
    				if(!slotsUpgrades.getStackInSlot(i).isEmpty()) {
    					hasUpgrade = true;
    					break;
    				}
    			}
    			
    			if(hasUpgrade) {
    				list.add(EnumTextFormatting.GOLD +"Installed Upgrades: ");
        			for(int i=0; i<slotsUpgrades.getSlots(); i++) {
        				if(!slotsUpgrades.getStackInSlot(i).isEmpty()) {
        					list.add(EnumTextFormatting.WHITE + slotsUpgrades.getStackInSlot(i).getDisplayName());
        				}
        			}
    			}

    		}else{
        		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
    	    }
    	}
	}
}
