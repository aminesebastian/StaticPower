package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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
		super(name, new Item.Properties().stacksTo(1));
		this.tier = tier;
		this.repairItem = repairItem;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == repairItem) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.hurt(1, RANDOM, null)) {
			stackCopy.shrink(1);
			stackCopy.setDamageValue(0);
		}
		return stackCopy;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage();
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
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		// Get the tier.
		StaticPowerTier tier = StaticPowerConfig.getTier(this.tier);

		// Add the swinging speed modifier.
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", tier.hammerDamage.get(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", tier.hammerSwingSpeed.get(), AttributeModifier.Operation.ADDITION));

		return equipmentSlot == EquipmentSlot.MAINHAND ? builder.build() : super.getDefaultAttributeModifiers(equipmentSlot);
	}

	public boolean onHitBlockLeftClick(ItemStack stack, Player player, BlockPos pos, Direction face) {
		// Only craft if on an anvil.
		if (player.getCommandSenderWorld().getBlockState(pos).is(Blocks.ANVIL) || player.getCommandSenderWorld().getBlockState(pos).is(Blocks.CHIPPED_ANVIL)
				|| player.getCommandSenderWorld().getBlockState(pos).is(Blocks.DAMAGED_ANVIL)) {
			// Check for all items on the block above the one we hit.
			boolean crafted = false;
			AABB bounds = new AABB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
			List<ItemEntity> droppedItems = player.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, bounds, (ItemEntity entity) -> true);
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
					if (!player.getCommandSenderWorld().isClientSide) {
						if (craftRecipe(stack, (Player) player, pos, recipe.get())) {
							entity.getItem().shrink(recipe.get().getInputItem().getCount());
							entity.getCommandSenderWorld().playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5F, (float) (0.8F + Math.random() * 0.3));
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.00D);
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.SMOKE, entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D,
									0.00D);
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.LAVA, entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.1D);
							crafted = true;
							break;
						}
					}
				}
			}

			// If we haven't crafted, still play a sound on the server.
			if (!player.getCommandSenderWorld().isClientSide) {
				if (crafted) {
					// Get the tier.
					StaticPowerTier tier = StaticPowerConfig.getTier(this.tier);

					// Set the cooldown before the player can hammer again.
					player.getCooldowns().addCooldown(stack.getItem(), tier.hammerCooldown.get());
				} else {
					player.getCommandSenderWorld().playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5F, (float) (1.2F + Math.random() * 0.3));
					player.getCooldowns().addCooldown(stack.getItem(), 5);
				}
			}
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		// Only perform on server.
		if (player.getCommandSenderWorld().isClientSide) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Get the block state of the block we're trying to break.
		BlockState blockToBreak = player.getCommandSenderWorld().getBlockState(pos);

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
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		tooltip.add(new TextComponent("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(new TextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}

	protected boolean craftRecipe(ItemStack hammer, Player player, BlockPos pos, HammerRecipe recipe) {
		ItemStack output = recipe.getOutput().calculateOutput();
		if (!output.isEmpty()) {
			// Drop the item.
			WorldUtilities.dropItem(player.getCommandSenderWorld(), pos, output);

			// Check if this is a block type recipe.
			if (recipe.isBlockType()) {
				player.getCommandSenderWorld().setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
			}

			// Damage the item.
			if (hammer.hurt(1, RANDOM, null)) {
				hammer.shrink(1);
				hammer.setDamageValue(0);
			}

			// Return early.
			return true;
		} else {
			return false;
		}
	}
}
