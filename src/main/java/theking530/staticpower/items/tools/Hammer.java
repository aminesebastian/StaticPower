package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.WorldUtilities;

public class Hammer extends StaticPowerItem {

	public Hammer(String name, int maxUses) {
		super(name, new Item.Properties().maxStackSize(1).maxDamage(maxUses).setNoRepair());
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		if (repair.getItem() == Items.IRON_INGOT) {
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
			if (recipe.isPresent()) {
				ItemStack output = recipe.get().getOutput().calculateOutput();
				if (!output.isEmpty()) {
					// Drop the item.
					WorldUtilities.dropItem(player.getEntityWorld(), pos, output);
					player.getEntityWorld().setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState());

					// Damage the item.
					if (itemstack.attemptDamageItem(1, random, null)) {
						itemstack.shrink(1);
						itemstack.setDamage(0);
					}

					// Return early.
					return true;
				}
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
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean showAdvanced) {
		if (showAdvanced) {
			tooltip.add(new StringTextComponent("Max Uses: " + getMaxDamage(stack)));
			tooltip.add(new StringTextComponent("Uses Remaining: " + (getMaxDamage(stack) - getDamage(stack))));
		}
	}
}
