package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class BasePowerUpgrade  extends BaseUpgrade implements IMachineUpgrade{
	
	public float CAPACITY = 1f;
	public float TICK_UPGRADE = 1f;
	
	public  BasePowerUpgrade(String name, Tier tier){
		super(name, tier);
		switch(tier) {
		case BASE:
			CAPACITY = (1.25f/maxStackSize);
			TICK_UPGRADE = (1.5f/maxStackSize);
			break;
		case STATIC:
			CAPACITY = (1.5f/maxStackSize);
			TICK_UPGRADE = (2f/maxStackSize);
			break;
		case ENERGIZED:
			CAPACITY = (1.75f/maxStackSize);
			TICK_UPGRADE = (3f/maxStackSize);
			break;
		case LUMUM:
			CAPACITY = (2f/maxStackSize);
			TICK_UPGRADE = (5f/maxStackSize);
			break;
		default:
			break;
		}
	}
	@Override
	public float getMultiplier(ItemStack stack, int upgradeNumber) {
		if(upgradeNumber == 1) {
			return CAPACITY * stack.stackSize;
		}else{
			return TICK_UPGRADE * stack.stackSize;
		}
	}
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getMultiplier(stack, 0))*100) + "%" + EnumTextFormatting.GREEN + " RF Capacity");
		list.add(EnumTextFormatting.WHITE + "+" + new java.text.DecimalFormat("#").format((getMultiplier(stack, 1))*100) + "%" + EnumTextFormatting.GREEN + " RF Per Tick");

		if(showHiddenTooltips()) {
    		list.add(EnumTextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize());
    	}else{
        	list.add(EnumTextFormatting.ITALIC + "Hold Shift");
    	}
	}
}
