package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseRangeUpgrade extends BaseUpgrade{
	
	public float RANGE_MULT = 1f;
	
	public  BaseRangeUpgrade(String name, Tier tier){
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);	
		switch(tier) {
		case BASE:
			RANGE_MULT = 1.5f;
			break;
		case STATIC:
			RANGE_MULT = 2f;
			break;
		case ENERGIZED:
			RANGE_MULT = 2.5f;
			break;
		case LUMUM:
			RANGE_MULT = 3f;
			break;
		default:
			break;
		}
	}
	
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.GREEN +  "+" + new java.text.DecimalFormat("#").format((RANGE_MULT)*100) + "% " + EnumTextFormatting.WHITE + "Range");
    }
}
