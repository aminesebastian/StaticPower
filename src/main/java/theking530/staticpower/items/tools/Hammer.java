package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.WorldUtilities;

public class Hammer extends StaticPowerItem {
	private final ResourceLocation tier;
	private final Item repairItem;

	public Hammer(String name, ResourceLocation tier, Item repairItem) {
		super(name, new Item.Properties().maxStackSize(1));
		this.tier = tier;
		this.repairItem = repairItem;
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == repairItem) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.attemptDamageItem(1, random, null)) {
			stackCopy.shrink(1);
			stackCopy.setDamage(0);
		}
		return stackCopy;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return StaticPowerConfig.getTier(tier).hammerUses.get();
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
		// Get the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(this.tier);

		// Add the swinging speed modifier.
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", tier.hammerDamage.get(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", tier.hammerSwingSpeed.get(), AttributeModifier.Operation.ADDITION));

		return equipmentSlot == EquipmentSlotType.MAINHAND ? builder.build() : super.getAttributeModifiers(equipmentSlot);
	}

	public boolean onHitBlockLeftClick(ItemStack stack, PlayerEntity player, BlockPos pos, Direction face) {
		// Only craft if on an anvil.
		if (player.getEntityWorld().getBlockState(pos).isIn(Blocks.ANVIL)) {
			// Check for all items on the block above the one we hit.
			boolean crafted = false;
			AxisAlignedBB bounds = new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
			List<ItemEntity> droppedItems = player.getEntityWorld().getEntitiesWithinAABB(ItemEntity.class, bounds, (ItemEntity entity) -> true);
			for (int i = droppedItems.size() - 1; i >= 0; i--) {
				// Get the item entity.
				ItemEntity entity = droppedItems.get(i);
				if (entity == null || entity.getItem().isEmpty()) {
					continue;
				}

				// Create the params and attempt to get the recipe.
				RecipeMatchParameters params = new RecipeMatchParameters(entity.getItem().copy());
				Optional<HammerRecipe> recipe = StaticPowerRecipeRegistry.getRecipe(HammerRecipe.RECIPE_TYPE, params);

				// If we have a recipe, craft using the recipe.
				if (recipe.isPresent() && !recipe.get().isBlockType()) {

					// Perform the crafting only on the server.
					if (!player.getEntityWorld().isRemote) {
						if (craftRecipe(stack, (PlayerEntity) player, pos, recipe.get())) {
							entity.getItem().shrink(recipe.get().getInputItem().getCount());
							entity.getEntityWorld().playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 0.5F, (float) (0.8F + Math.random() * 0.3));
							((ServerWorld) entity.getEntityWorld()).spawnParticle(ParticleTypes.CRIT, entity.getPosX(), entity.getPosY() + 0.1, entity.getPosZ(), 1, 0.0D, 0.0D, 0.0D, 0.00D);
							((ServerWorld) entity.getEntityWorld()).spawnParticle(ParticleTypes.SMOKE, entity.getPosX(), entity.getPosY() + 0.1, entity.getPosZ(), 1, 0.0D, 0.0D, 0.0D,
									0.00D);
							((ServerWorld) entity.getEntityWorld()).spawnParticle(ParticleTypes.LAVA, entity.getPosX(), entity.getPosY() + 0.1, entity.getPosZ(), 1, 0.0D, 0.0D, 0.0D, 0.1D);
							crafted = true;
							break;
						}
					}
				}
			}

			// If we haven't crafted, still play a sound on the server.
			if (!player.getEntityWorld().isRemote) {
				if (crafted) {
					// Get the tier.
					StaticPowerTier tier = StaticPowerConfig.getTier(this.tier);

					// Set the cooldown before the player can hammer again.
					player.getCooldownTracker().setCooldown(stack.getItem(), tier.hammerCooldown.get());
				} else {
					player.getEntityWorld().playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.5F, (float) (1.2F + Math.random() * 0.3));
					player.getCooldownTracker().setCooldown(stack.getItem(), 5);
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// Only perform on server.
		if (player.getEntityWorld().isRemote) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Get the block state of the block we're trying to break.
		BlockState blockToBreak = player.getEntityWorld().getBlockState(pos);

		// If the block is not air, attempt to get the hammer recipe for it.
		if (!blockToBreak.isAir()) {
			// Create the params and attempt to get the recipe.
			RecipeMatchParameters params = new RecipeMatchParameters(blockToBreak);
			Optional<HammerRecipe> recipe = StaticPowerRecipeRegistry.getRecipe(HammerRecipe.RECIPE_TYPE, params);

			// If the recipe is present, caluclate the output. If it exists, drop the output
			// and damage the hammer.
			if (recipe.isPresent() && recipe.get().isBlockType()) {
				return craftRecipe(itemstack, player, pos, recipe.get());
			}
		}

		// If none of the above, return the super.
		return super.onBlockStartBreak(itemstack, pos, player);
	}

	@Override
	public boolean hasContainerItem() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(new StringTextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}

	protected boolean craftRecipe(ItemStack hammer, PlayerEntity player, BlockPos pos, HammerRecipe recipe) {
		ItemStack output = recipe.getOutput().calculateOutput();
		if (!output.isEmpty()) {
			// Drop the item.
			WorldUtilities.dropItem(player.getEntityWorld(), pos, output);

			// Check if this is a block type recipe.
			if (recipe.isBlockType()) {
				player.getEntityWorld().setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());
			}

			// Damage the item.
			if (hammer.attemptDamageItem(1, random, null)) {
				hammer.shrink(1);
				hammer.setDamage(0);
			}

			// Return early.
			return true;
		} else {
			return false;
		}
	}
}
