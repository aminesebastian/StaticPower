package theking530.staticpower.items.tools.basictools;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.EquipmentMaterial;
import theking530.staticpower.utils.EnumTextFormatting;


public class BaseAxe extends ItemTool {

    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE});

	public String NAME = "";
	public EquipmentMaterial MATERIAL;
	
	public BaseAxe(EquipmentMaterial material, String unlocalizedName) {
        super(material.getToolMaterial(), EFFECTIVE_ON);
		NAME= unlocalizedName;
		setUnlocalizedName(unlocalizedName);
		setRegistryName(unlocalizedName);
		setCreativeTab(StaticPower.StaticPower);
		
		MATERIAL = material;
        this.attackDamage = material.getToolMaterial().getAttackDamage();
        this.attackSpeed = -3.0F;
	}
	
	@Override  
		public void addInformation(ItemStack itemstack, @Nullable World worldIn, List<String> list, ITooltipFlag flagIn) {
    	if(showHiddenTooltips()) {
    		String tempLevel = "Mining Level: " +  MATERIAL.getToolMaterial().getHarvestLevel();
    		String tempSpeed = "Speed: " +  MATERIAL.getToolMaterial().getEfficiency();
    		String tempDamage = "Damage: " + (Math.round(MATERIAL.getToolMaterial().getAttackDamage() + 1)); 
    		String tempDurability = "Durability: " + (MATERIAL.getToolMaterial().getMaxUses()-itemstack.getMetadata()) + "/" + MATERIAL.getToolMaterial().getMaxUses();
    		
    		list.add(tempLevel);
    		list.add(tempSpeed);
    		list.add(tempDurability);
    		list.add(tempDamage);
    	}else{
    		list.add(EnumTextFormatting.ITALIC + "Hold Shift");
	    }
	}
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	
    //public float getStrVsBlock(ItemStack stack, IBlockState state) {
       // Material material = state.getMaterial();
        //return material != Material.WOOD && material != Material.PLANTS && material != Material.VINE ? super.get(stack, state) : this.getEfficiency();
   // }
}