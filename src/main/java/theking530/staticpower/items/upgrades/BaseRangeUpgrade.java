package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseRangeUpgrade  extends BaseUpgrade implements IMachineUpgrade{
	
	public float RANGE_MULT = 1f;
	
	public  BaseRangeUpgrade(String name, Tier tier){
		super(name, tier);
		switch(tier) {
		case BASE:
			RANGE_MULT = (0.75f);
			break;
		case STATIC:
			RANGE_MULT = (1.5f);
			break;
		case ENERGIZED:
			RANGE_MULT = (2.5f);
			break;
		case LUMUM:
			RANGE_MULT = (3f);
			break;
		default:
			break;
		}
		setMaxStackSize(1);
	}
	@Override
	public float getMultiplier(ItemStack stack, int upgradeNumber) {
		return RANGE_MULT;
	}
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(RANGE_MULT < 1) {
			list.add(EnumTextFormatting.RED +  "-" + new java.text.DecimalFormat("#").format((getMultiplier(stack, 0))*100) + "% " + EnumTextFormatting.WHITE + "Range");
		}else{
			list.add(EnumTextFormatting.GREEN +  "+" + new java.text.DecimalFormat("#").format((getMultiplier(stack, 0))*100) + "% " + EnumTextFormatting.WHITE + "Range");
		}
		
		if(showHiddenTooltips()) {
    		list.add(EnumTextFormatting.WHITE + "Stacks Up To: " + stack.getMaxStackSize());
    	}else{
        	list.add(EnumTextFormatting.ITALIC + "Hold Shift");
    	}
	}
}
