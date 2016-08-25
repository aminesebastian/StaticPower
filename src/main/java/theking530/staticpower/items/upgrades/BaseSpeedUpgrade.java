package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseSpeedUpgrade extends BaseUpgrade implements IMachineUpgrade{
	public float SPEED = 1f;
	public float POWER_MULT = 1f;
	
	public  BaseSpeedUpgrade(String name, Tier tier){
		super(name, tier);
		switch(tier) {
		case BASE:
			SPEED = (1.1f/maxStackSize);
			POWER_MULT = (1.15f/maxStackSize);
			break;
		case STATIC:
			SPEED = (1.75f/maxStackSize);
			POWER_MULT = (1.75f/maxStackSize);
			break;
		case ENERGIZED:
			SPEED = (2.25f/maxStackSize);
			POWER_MULT = (2.0f/maxStackSize);
			break;
		case LUMUM:
			SPEED = (3.5f/maxStackSize);
			POWER_MULT = (2.5f/maxStackSize);
			break;
		case CREATIVE:
			SPEED = (10f/maxStackSize);
			POWER_MULT = (0f/maxStackSize);
			break;
		}
	}
	@Override
	public float getMultiplier(ItemStack stack, int upgradeNumber) {
		if(upgradeNumber == 0) {
			return (SPEED * stack.stackSize);
		}else{
			return (POWER_MULT * stack.stackSize);
		}
	}
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getMultiplier(stack, 0))*100) + "%" + EnumTextFormatting.GREEN + " Processing Speed");
		list.add(EnumTextFormatting.WHITE + "+" +  new java.text.DecimalFormat("#").format((getMultiplier(stack, 1))*100) + "%" + EnumTextFormatting.RED + " Power Use");
		if(showHiddenTooltips()) {
    		list.add(EnumTextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize());
    	}else{
        	list.add(EnumTextFormatting.ITALIC + "Hold Shift");
    	}
	}
}
