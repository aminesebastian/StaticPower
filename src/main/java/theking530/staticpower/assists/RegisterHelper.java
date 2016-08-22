package theking530.staticpower.assists;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.SolderingRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;

public class RegisterHelper 
{
		public static void registerBlock(Block block) {
			GameRegistry.register(block);
		}
		public static void registerItem(Item item) {
			GameRegistry.register(item);
		}
		public static void registerGrinderRecipe(ItemStack itemstack, GrinderOutput... outputs) {
			GrinderRecipeRegistry.Grinding().addRecipe(itemstack, outputs);
		}
		public static void registerInfuserRecipe(ItemStack itemstack1, ItemStack itemstack2, FluidStack fluidStack) {
			if(fluidStack != null && itemstack1 != null && itemstack2 != null) {
				InfuserRecipeRegistry.Infusing().addRecipe(itemstack1, itemstack2, fluidStack);			
			}
		}
		public static void registerFusionRecipe(ItemStack output, ItemStack...inputs) {
			if(output != null && inputs[0] != null) {
				FusionRecipeRegistry.Fusing().addRecipe(output, inputs);			
			}
		}
		public static void registerSqueezerRecipe(ItemStack input, ItemStack result, FluidStack outputFluid) {
			SqueezerRecipeRegistry.Squeezing().addRecipe(input, result, outputFluid);
		}
		public static void registerSolderingRecipe(ItemStack outputStack, Object ... inputParams) {
			SolderingRecipeRegistry.Soldering().addRecipe(outputStack, inputParams);
		}
}
