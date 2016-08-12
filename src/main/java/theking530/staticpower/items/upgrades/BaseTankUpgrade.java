package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseTankUpgrade extends BaseUpgrade{
	public float CAPACITY = 1f;
	
	public  BaseTankUpgrade(String name, Tier tier){
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);	
		switch(tier) {
		case BASE:
			CAPACITY = 1.5f;
			break;
		case STATIC:
			CAPACITY = 2.0f;
			break;
		case ENERGIZED:
			CAPACITY = 2.5f;
			break;
		case LUMUM:
			CAPACITY = 3.0f;
			break;
		default:
			break;
		}
	}
	
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE +  new java.text.DecimalFormat("#").format((CAPACITY-1)*100) + "%" + EnumTextFormatting.GREEN + " Tank Capacity");
    }
}
