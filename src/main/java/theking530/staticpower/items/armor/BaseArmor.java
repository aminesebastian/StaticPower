package theking530.staticpower.items.armor;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.EquipmentMaterial;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ModMaterials;
import theking530.staticpower.utils.EnumTextFormatting;

public class BaseArmor extends ItemArmor {

	public EquipmentMaterial MATERIAL;
	public ArmorType ARMOR_TYPE;

	public BaseArmor(String name, ArmorType type, EquipmentMaterial materialIn, EntityEquipmentSlot equipmentSlotIn) {
		super(materialIn.getArmorMaterial(), equipmentSlotIn == EntityEquipmentSlot.LEGS ? 2 : 1, equipmentSlotIn);
	    MATERIAL = materialIn;
	    ARMOR_TYPE = type;
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
	}
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    	if(!stack.hasTagCompound()) {
    		NBTTagCompound tempTag = new NBTTagCompound();
    		stack.setTagCompound(tempTag);
    	}
    	if(isEquipped((EntityPlayer) entityIn, stack)) {
    		if(!stack.getTagCompound().getBoolean("EQUIPPED")) {
        		onEquipped((EntityPlayer) entityIn, stack, (BaseArmor) stack.getItem());
        		stack.getTagCompound().setBoolean("EQUIPPED", true);
    		}
    	}else{
    		if(stack.getTagCompound().getBoolean("EQUIPPED")) {
    			onUnequipped((EntityPlayer) entityIn, stack, (BaseArmor) stack.getItem());
        		stack.getTagCompound().setBoolean("EQUIPPED", false);
    		}
    	}
    }
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		BaseArmor tempArmor = (BaseArmor) itemStack.getItem();
		if(!isFullSet(player) || !itemStack.getTagCompound().getBoolean("EQUIPPED")) {
			for(int i=0; i<getEffects().length; i++) {
				player.removePotionEffect(getEffects()[i].getPotion());
			}
		}else{
			for(int i=0; i<getEffects().length; i++) {
				effectPlayer(player, getEffects()[i].getPotion(), getEffects()[i].getAmplifier());
			}
		}
	} 
	public void onEquipped(EntityPlayer player, ItemStack itemstack, BaseArmor tempArmor) {
		for(int i=0; i<getEffects().length; i++) {
			effectPlayer(player, getEffects()[i].getPotion(), getEffects()[i].getAmplifier());
		}
	}
	public void onUnequipped(EntityPlayer player, ItemStack itemstack, BaseArmor tempArmor) {
		for(int i=0; i<getEffects().length; i++) {
			player.removePotionEffect(getEffects()[i].getPotion());
		}
	}
	
	public List getSetInfo() {
		List tempList = new ArrayList();
		tempList.add("MISSING");
		return tempList;
	}
	public String getTagline() {
		return "MISSING";
	}
	public PotionEffect[] getEffects() {
		return new PotionEffect[0];
	}
	/**
		Internal methods not meant to be overridden.
	 */
	
	public boolean isEquipped(EntityPlayer player, ItemStack itemstack) {
		if(player.inventory.armorItemInSlot(3) != null && itemstack.isItemEqualIgnoreDurability(player.inventory.armorItemInSlot(3))) {
			return true;
		}
		if(player.inventory.armorItemInSlot(2) != null && itemstack.isItemEqualIgnoreDurability(player.inventory.armorItemInSlot(2))) {
			return true;
		}
		if(player.inventory.armorItemInSlot(1) != null && itemstack.isItemEqualIgnoreDurability(player.inventory.armorItemInSlot(1))) {
			return true;
		}
		if(player.inventory.armorItemInSlot(0) != null && itemstack.isItemEqualIgnoreDurability(player.inventory.armorItemInSlot(0))) {
			return true;
		}		
		return false;
	}
	@Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
    	if(!item.hasTagCompound()) {
    		NBTTagCompound tempTag = new NBTTagCompound();
    		item.setTagCompound(tempTag);
    	}
    	item.getTagCompound().setBoolean("EQUIPPED", false);
		return true;		
	}
	public boolean isFullSet(EntityPlayer player) {
		BaseArmor helmet = null;
		BaseArmor chest = null;
		BaseArmor leggings = null;
		BaseArmor boots = null;
		
		if(player.inventory.armorItemInSlot(3) != null) {
			helmet = (BaseArmor)player.inventory.armorItemInSlot(3).getItem();
		}
		if(player.inventory.armorItemInSlot(2) != null) {
			chest = (BaseArmor)player.inventory.armorItemInSlot(2).getItem();
		}
		if(player.inventory.armorItemInSlot(1) != null) {
			leggings = (BaseArmor)player.inventory.armorItemInSlot(1).getItem();
		}
		if(player.inventory.armorItemInSlot(0) != null) {
			boots = (BaseArmor)player.inventory.armorItemInSlot(0).getItem();
		}
		
		if (helmet != null && helmet.MATERIAL == MATERIAL
		        && chest != null && chest.MATERIAL == MATERIAL
		        && leggings != null && leggings.MATERIAL == MATERIAL
		        && boots != null && boots.MATERIAL == MATERIAL) {
		       	return true;
		}
		return false;
	}
	private void effectPlayer(EntityPlayer player, Potion potion, int amplifier) {
	    if (player.getActivePotionEffect(potion) == null || player.getActivePotionEffect(potion).getDuration() <= 1)
	        player.addPotionEffect(new PotionEffect(potion, 20000000, amplifier, true, true));
	}
	
	public boolean showHiddenTooltips() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
	@Override  
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean par4) {
		BaseArmor tempArmor = (BaseArmor) itemstack.getItem();
		if(tempArmor != null) {	
			ArmorType tempArmorType= tempArmor.ARMOR_TYPE;		
			if(showHiddenTooltips()) {
				list.add("Type: " + tempArmorType.toLocalizedString());
				list.add("Material: " + tempArmor.MATERIAL.toColorString());
				list.add("");
				list.add(EnumTextFormatting.UNDERLINE + "Set Bonus");
				list.addAll(getSetInfo());
			}else{	
				list.add(EnumTextFormatting.YELLOW + "" + EnumTextFormatting.ITALIC + getTagline());
				list.add(EnumTextFormatting.ITALIC + "Hold Shift");
			}
		}
	}
	public static enum ArmorType {
		LIGHT(2, 2, 0.5), MEDIUM(1, 1, 1), HEAVY(0.5, 0.5, 2);
		
		private double SPEED_BOOST;
		private double MINING_SPEED_BOOST;
		private double DAMAGE_BOOST;
		
        private ArmorType(double speedBoost, double miningSpeed, double damage) {
        	SPEED_BOOST = speedBoost;
        	MINING_SPEED_BOOST = miningSpeed;
        	DAMAGE_BOOST = damage;
        }
		
		public String toLocalizedString() {
			String color = toString() == "LIGHT" ? EnumTextFormatting.GREEN +"" : toString() == "MEDIUM" ? EnumTextFormatting.DARK_AQUA +"": EnumTextFormatting.BLUE +"";
			return color + EnumTextFormatting.ITALIC +toString().substring(0, 1) + toString().toLowerCase().substring(1) + " Armor";
			
		}
		public double getSpeedBoost() {
			return SPEED_BOOST;
		}
		public double getMiningSpeedBoost() {
			return MINING_SPEED_BOOST;
		}
		public double getDamageBoost() {
			return DAMAGE_BOOST;
		}
	}
}
