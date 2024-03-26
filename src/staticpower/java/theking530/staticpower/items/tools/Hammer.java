package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModBlockTags;
import theking530.staticpower.items.StaticPowerItem;

public class Hammer extends StaticPowerItem {
	private final ResourceLocation tier;
	private final Supplier<Item> repairItem;

	public Hammer(ResourceLocation tier, Supplier<Item> repairItem) {
		super(new Item.Properties().stacksTo(1).tab(ModCreativeTabs.TOOLS));
		this.tier = tier;
		this.repairItem = repairItem;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == repairItem.get()) {
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		ItemStack stackCopy = stack.copy();
		if (stackCopy.hurt(1, RandomSource.create(), null)) {
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
		return StaticCoreConfig.getTier(tier).toolConfiguration.hammerUses.get();
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
		// Get the tier.
		StaticCoreTier tier = StaticCoreConfig.getTier(this.tier);

		// Add the swinging speed modifier.
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier",
				tier.toolConfiguration.hammerDamage.get(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier",
				tier.toolConfiguration.hammerSwingSpeed.get(), AttributeModifier.Operation.ADDITION));

		return equipmentSlot == EquipmentSlot.MAINHAND ? builder.build()
				: super.getDefaultAttributeModifiers(equipmentSlot);
	}

	public boolean onHitBlockLeftClick(ItemStack stack, Player player, BlockPos pos, Direction face) {
		BlockState hitBlock = player.level.getBlockState(pos);
		boolean hitAnvil = ModBlockTags.matches(BlockTags.ANVIL, hitBlock.getBlock());

		// Check for all items on the block above the one we hit.
		boolean crafted = false;
		BlockPos targetPos = pos.relative(face);
		AABB bounds = new AABB(targetPos.getX(), targetPos.getY(), targetPos.getZ(), targetPos.getX() + 1,
				targetPos.getY() + 1, targetPos.getZ() + 1);
		List<ItemEntity> droppedItems = player.getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, bounds,
				(ItemEntity entity) -> true);
		for (int i = droppedItems.size() - 1; i >= 0; i--) {
			// Get the item entity.
			ItemEntity entity = droppedItems.get(i);
			if (entity == null || entity.getItem().isEmpty()) {
				continue;
			}

			// Create the params and attempt to get the recipe.
			ITeam playerTeam = TeamManager.get(player.getLevel()).getTeamForPlayer(player);
			RecipeMatchParameters params = new RecipeMatchParameters(playerTeam != null ? playerTeam.getId() : null,
					entity.getItem().copy());
			Optional<HammerRecipe> recipe = player.level.getRecipeManager()
					.getRecipeFor(ModRecipeTypes.HAMMER_RECIPE_TYPE.get(), params, player.level);

			// If we have a recipe, craft using the recipe.
			if (recipe.isPresent() && !recipe.get().isBlockType()) {
				if (recipe.get().requiresAnvil() && !hitAnvil) {
					continue;
				}

				// Perform the crafting only on the server.
				if (!player.getCommandSenderWorld().isClientSide()) {
					if (craftRecipe(stack, (Player) player, pos, recipe.get())) {
						entity.getItem().shrink(recipe.get().getInputItem().getCount());
						if (hitAnvil) {
							entity.getCommandSenderWorld().playSound(null, pos, SoundEvents.ANVIL_USE,
									SoundSource.BLOCKS, 0.5F, (float) (0.8F + Math.random() * 0.3));
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.SMOKE,
									entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.00D);
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.LAVA,
									entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.1D);
						} else {
							entity.getCommandSenderWorld().playSound(null, pos, SoundEvents.STONE_BREAK,
									SoundSource.BLOCKS, 0.5F, (float) (0.8F + Math.random() * 0.3));
							((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.ASH,
									entity.getX(), entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.00D);
						}

						((ServerLevel) entity.getCommandSenderWorld()).sendParticles(ParticleTypes.CRIT, entity.getX(),
								entity.getY() + 0.1, entity.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.00D);

						crafted = true;
						break;
					}
				}
			}
		}

		// If we haven't crafted, still play a sound on the server.
		if (!player.getCommandSenderWorld().isClientSide()) {
			if (crafted) {
				// Get the tier.
				StaticCoreTier tier = StaticCoreConfig.getTier(this.tier);

				// Set the cooldown before the player can hammer again.
				player.getCooldowns().addCooldown(stack.getItem(), tier.toolConfiguration.hammerCooldown.get());
			} else {
				if (hitAnvil) {
					player.getCommandSenderWorld().playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS,
							0.3F, (float) (1.2F + Math.random() * 0.3));
				}

				player.getCooldowns().addCooldown(stack.getItem(), 5);
			}
		}
		return crafted;
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		// Only perform on server.
		if (player.getCommandSenderWorld().isClientSide()) {
			return super.onBlockStartBreak(itemstack, pos, player);
		}

		// Get the block state of the block we're trying to break.
		BlockState blockToBreak = player.getCommandSenderWorld().getBlockState(pos);

		// If the block is not air, attempt to get the hammer recipe for it.
		if (!blockToBreak.isAir()) {
			// Create the params and attempt to get the recipe.
			ITeam playerTeam = TeamManager.get(player.getLevel()).getTeamForPlayer(player);
			RecipeMatchParameters params = new RecipeMatchParameters(playerTeam != null ? playerTeam.getId() : null,
					blockToBreak);
			Optional<HammerRecipe> recipe = player.level.getRecipeManager()
					.getRecipeFor(ModRecipeTypes.HAMMER_RECIPE_TYPE.get(), params, player.level);

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
		tooltip.add(Component.literal("Max Uses: " + getMaxDamage(stack)));
		tooltip.add(Component.literal("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
	}

	protected boolean craftRecipe(ItemStack hammer, Player player, BlockPos pos, HammerRecipe recipe) {
		ItemStack output = recipe.getOutput().calculateOutput();
		if (!output.isEmpty()) {
			// Drop the item.
			WorldUtilities.dropItem(player.getCommandSenderWorld(), pos, output);

			// Check if this is a block type recipe.
			if (recipe.isBlockType()) {
				player.getCommandSenderWorld().setBlockAndUpdate(pos,
						net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
			}

			if (hammer.hurt(1, RandomSource.create(), null)) {
				hammer.shrink(1);
				hammer.setDamageValue(0);
			}

			if (recipe.hasExperience()) {
				WorldUtilities.dropExperience(player.getLevel(), pos, (int) recipe.getExperience());
			}

			// Return early.
			return true;
		} else {
			return false;
		}
	}
}
