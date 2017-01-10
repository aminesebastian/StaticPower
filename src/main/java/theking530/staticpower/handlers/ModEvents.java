package theking530.staticpower.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.SkeletonType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theking530.staticpower.items.armor.BaseArmor;
import theking530.staticpower.items.armor.BaseShield;
import theking530.staticpower.items.armor.ModArmor;
import theking530.staticpower.items.armor.SkeletonArmor;
import theking530.staticpower.items.armor.UndeadArmor;
import theking530.staticpower.potioneffects.BasePotion;

public class ModEvents {
	
	public final Random RANDOM;
	public static ArrayList<PotionEffect> POTION_EFFECTS;
	
	public ModEvents() {
		RANDOM = new Random();
		POTION_EFFECTS = new ArrayList<PotionEffect>();
	}	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}
	
    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
	public void attackEvent(LivingAttackEvent e) {
    	handleShieldDamage(e);
    	handleArmorDamage(e);
	}
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void dropEvent(LivingDropsEvent event) {
    	handleArmorSetDrops(event);
    } 
    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onEntityUpdate(LivingUpdateEvent event) {
    	Collection<PotionEffect> tempEffects = event.getEntityLiving().getActivePotionEffects();
    	if(tempEffects == null) {
    		return;
    	}
    	Object[] tempEffectsArray = tempEffects.toArray();
		for(int i=0; i<tempEffectsArray.length; i++) {
			PotionEffect tempEffect = (PotionEffect) tempEffectsArray[i];
			if(tempEffect.getPotion() instanceof BasePotion) {
				BasePotion tempPotion = (BasePotion) tempEffect.getPotion();
				if(!POTION_EFFECTS.contains(tempEffect)) {
					tempPotion.potionEffectApplied(event.getEntityLiving());
					POTION_EFFECTS.add(tempEffect);
				}else if(tempEffect.getDuration() == 1) {
					tempPotion.potionEffectRemoved(event.getEntityLiving());
					POTION_EFFECTS.remove(tempEffect);
				}else{
					tempPotion.potionEffectTick(event.getEntityLiving(), tempEffect.getDuration());
				}
			}
		}
    }
    
    public void handleShieldDamage(LivingAttackEvent e) {
		float damage = e.getAmount();
		ItemStack activeItemStack;
		EntityPlayer player;
		if (!(e.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		player = (EntityPlayer) e.getEntityLiving();
		if (player.getActiveItemStack() == null) {
			return;
		}
		activeItemStack = player.getActiveItemStack();

		if (damage > 0.0F && activeItemStack != null && activeItemStack.getItem() instanceof BaseShield) {
			int i = 1 + MathHelper.floor_float(damage);
			activeItemStack.damageItem(i, player);

			if (activeItemStack.stackSize <= 0) {
				EnumHand enumhand = player.getActiveHand();
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, activeItemStack, enumhand);

				if (enumhand == EnumHand.MAIN_HAND) {
					player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, (ItemStack) null);
				}
				else {
					player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, (ItemStack) null);
				}

				activeItemStack = null;
				if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
					player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.worldObj.rand.nextFloat() * 0.4F);
				}
			}
		}
    }
    public void handleArmorDamage(LivingAttackEvent e) {
		if (!(e.getEntityLiving() instanceof EntityPlayer)) {
			return;
		}
		EntityPlayer player = (EntityPlayer) e.getEntityLiving();
		
		float damageAmount = e.getAmount();
        damageAmount = CombatRules.getDamageAfterAbsorb(damageAmount, (float)player.getTotalArmorValue(), (float)player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        if (!e.getSource().isDamageAbsolute()){
            if (player.isPotionActive(MobEffects.RESISTANCE) && e.getSource() != DamageSource.outOfWorld) {
                int i = (player.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f = damageAmount * (float)j;
                damageAmount = f / 25.0F;
            }

            if (damageAmount <= 0.0F){

            } else {
                int k = EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), e.getSource());
                if (k > 0) {
                	damageAmount = CombatRules.getDamageAfterMagicAbsorb(damageAmount, (float)k);
                }
            }
        }
        if(player != null && player.inventory != null) {
			if(player.inventory.armorInventory[3] != null && player.inventory.armorInventory[3].getItem() instanceof BaseArmor) {
				BaseArmor tempArmor = (BaseArmor) player.inventory.armorInventory[3].getItem();
				tempArmor.onWearerDamaged(e, damageAmount, player, EntityEquipmentSlot.HEAD, player.inventory.armorInventory[3]);
			}
			if(player.inventory.armorInventory[2] != null && player.inventory.armorInventory[2].getItem() instanceof BaseArmor) {
				BaseArmor tempArmor = (BaseArmor) player.inventory.armorInventory[2].getItem();
				tempArmor.onWearerDamaged(e, damageAmount, player, EntityEquipmentSlot.CHEST, player.inventory.armorInventory[2]);
			}
			if(player.inventory.armorInventory[1] != null && player.inventory.armorInventory[1].getItem() instanceof BaseArmor) {
				BaseArmor tempArmor = (BaseArmor) player.inventory.armorInventory[1].getItem();
				tempArmor.onWearerDamaged(e, damageAmount, player, EntityEquipmentSlot.LEGS, player.inventory.armorInventory[1]);
			}
			if(player.inventory.armorInventory[0] != null && player.inventory.armorInventory[0].getItem() instanceof BaseArmor) {
				BaseArmor tempArmor = (BaseArmor) player.inventory.armorInventory[0].getItem();
				tempArmor.onWearerDamaged(e, damageAmount, player, EntityEquipmentSlot.FEET, player.inventory.armorInventory[0]);
			}	
        }
    }

    public void handleArmorSetDrops(LivingDropsEvent event) {
		Entity entity = event.getEntity();
		World world = event.getEntity().getEntityWorld();
		BlockPos pos = event.getEntity().getPosition();
		
		if(entity instanceof EntityZombie && !(entity instanceof EntityPigZombie)){	
			UndeadArmor tempArmor = (UndeadArmor) ModArmor.UndeadHelmet;
			ItemStack tempStack = tempArmor.getRandomDrop(0.06);
			if(tempStack != null) {
				EntityItem itemDropX = new EntityItem(world, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, tempStack);
				if(itemDropX != null) {
					event.getDrops().add(itemDropX);			
				}
			}
		}else if(entity instanceof EntitySkeleton){	
			EntitySkeleton tempSkele = (EntitySkeleton) entity;
			if(tempSkele.getSkeletonType() != SkeletonType.WITHER) {
				SkeletonArmor tempArmor = (SkeletonArmor) ModArmor.SkeletonHelmet;
				ItemStack tempStack = tempArmor.getRandomDrop(0.06);
				if(tempStack != null) {
					EntityItem itemDropX = new EntityItem(world, pos.getX()+.5, pos.getY()+.5, pos.getZ()+.5, tempStack);
					event.getDrops().add(itemDropX);	
				}
			}
		}	
    }
    
    
	public boolean diceRoll(float percentage) {
		if(percentage >= 1) {
			return true;
		}
		float randFloat = RANDOM.nextFloat();		
		return percentage > randFloat ? true : false;
	}
}
