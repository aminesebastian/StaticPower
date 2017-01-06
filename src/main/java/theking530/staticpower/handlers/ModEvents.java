package theking530.staticpower.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import theking530.staticpower.items.armor.BaseShield;

public class ModEvents {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}
    @SubscribeEvent
	public void attackEvent(LivingAttackEvent e) {
    	handleShieldDamage(e);
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
}
