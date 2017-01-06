package theking530.staticpower.items.armor;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.EquipmentMaterial;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.armor.BaseArmor.ArmorType;

public class BaseShield extends ItemShield{
	
	private EquipmentMaterial MATERIAL;
	private ArmorType ARMOR_TYPE;
	
	public BaseShield(String name, ArmorType type, EquipmentMaterial materialIn) {	
	    MATERIAL = materialIn;
	    ARMOR_TYPE = type;
		setCreativeTab(StaticPower.StaticPower);
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
		
        setMaxDamage(336);
        addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)  {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
        MinecraftForge.EVENT_BUS.register(this);
	}
	
    public String getItemStackDisplayName(ItemStack stack){
        if (stack.getSubCompound("BlockEntityTag", false) != null)  {
            String s = getUnlocalizedName();
            EnumDyeColor enumdyecolor = ItemBanner.getBaseColor(stack);
            s = s + enumdyecolor.getUnlocalizedName() + ".name";
            return I18n.translateToLocal(s);
        } else {
            return I18n.translateToLocal(getUnlocalizedName());
        }
    }
    @SubscribeEvent
	public void attackEvent(LivingAttackEvent e) {
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
