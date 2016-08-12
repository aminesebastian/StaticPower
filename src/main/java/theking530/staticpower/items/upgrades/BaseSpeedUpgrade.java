package theking530.staticpower.items.upgrades;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseSpeedUpgrade extends BaseUpgrade{
	public float SPEED = 1f;
	public float POWER_MULT = 1f;
	
	public  BaseSpeedUpgrade(String name, Tier tier){
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);	
		switch(tier) {
		case BASE:
			SPEED = 1.2f;
			POWER_MULT = 1.15f;
			break;
		case STATIC:
			SPEED = 1.8f;
			POWER_MULT = 1.75f;
			break;
		case ENERGIZED:
			SPEED = 2.5f;
			POWER_MULT = 2.0f;
			break;
		case LUMUM:
			SPEED = 3.0f;
			POWER_MULT = 2.5f;
			break;
		case CREATIVE:
			SPEED = 10f;
			POWER_MULT = 0f;
			break;
		}
	}
	
	@Override  
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumTextFormatting.WHITE +  new java.text.DecimalFormat("#").format((SPEED-1)*100) + "%" + EnumTextFormatting.GREEN + " Processing Speed");
		list.add(EnumTextFormatting.WHITE + new java.text.DecimalFormat("#").format((POWER_MULT-1)*100) + "%" + EnumTextFormatting.RED + " Power Use");
    }
}
