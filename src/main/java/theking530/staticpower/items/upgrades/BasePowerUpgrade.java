package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BasePowerUpgrade extends BaseUpgrade{
	
	public float CAPACITY = 1f;
	public float TICK_UPGRADE = 1f;
	
	public  BasePowerUpgrade(String name, Tier tier){
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);	
		switch(tier) {
		case BASE:
			CAPACITY = 1.25f;
			TICK_UPGRADE = 1.5f;
			break;
		case STATIC:
			CAPACITY = 1.5f;
			TICK_UPGRADE = 2f;
			break;
		case ENERGIZED:
			CAPACITY = 1.75f;
			TICK_UPGRADE = 3f;
			break;
		case LUMUM:
			CAPACITY = 2f;
			TICK_UPGRADE = 5f;
			break;
		default:
			break;
		}
	}
	
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE +  new java.text.DecimalFormat("#").format((CAPACITY-1)*100) + "%" + EnumTextFormatting.GREEN + " RF Capacity");
		list.add(EnumTextFormatting.WHITE + new java.text.DecimalFormat("#").format((TICK_UPGRADE-1)*100) + "%" + EnumTextFormatting.GREEN + " RF Per Tick");
    }
}
