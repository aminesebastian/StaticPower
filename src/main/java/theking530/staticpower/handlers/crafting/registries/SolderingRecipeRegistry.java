package theking530.staticpower.handlers.crafting.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.wrappers.FluidInfuserOutputWrapper;
import theking530.staticpower.handlers.crafting.wrappers.SolderingRecipeWrapper;
import theking530.staticpower.items.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class SolderingRecipeRegistry {

    private ArrayList<SolderingRecipeWrapper> RECIPES = new ArrayList();
    private static final SolderingRecipeRegistry SOLDERING_BASE = new SolderingRecipeRegistry();
	
    public static final SolderingRecipeRegistry Soldering() {
    	return SOLDERING_BASE;
    }
    public SolderingRecipeWrapper addRecipe(ItemStack outputStack, Object ... inputParams) {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (inputParams[i] instanceof String[]){
            String[] astring = (String[])((String[])inputParams[i++]);

            for (int l = 0; l < astring.length; ++l){
                String s1 = astring[l];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }else{
            while (inputParams[i] instanceof String){
                String s2 = (String)inputParams[i++];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < inputParams.length; i += 2){
            Character character = (Character)inputParams[i];
            ItemStack itemstack1 = null;

            if (inputParams[i + 1] instanceof Item){
                itemstack1 = new ItemStack((Item)inputParams[i + 1]);
            }else if (inputParams[i + 1] instanceof Block){
                itemstack1 = new ItemStack((Block)inputParams[i + 1], 1, 32767);
            }else if (inputParams[i + 1] instanceof ItemStack){
                itemstack1 = (ItemStack)inputParams[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0))){
                aitemstack[i1] = ((ItemStack)hashmap.get(Character.valueOf(c0))).copy();
            }else{
                aitemstack[i1] = null;
            }
        }

        SolderingRecipeWrapper solderingRecipe = new SolderingRecipeWrapper(j, k, aitemstack, outputStack);
        RECIPES.add(solderingRecipe);
        return solderingRecipe;
    }  
    public ItemStack findSolderingOutput(IInventory invCrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;
        for (j = 0; j < invCrafting.getSizeInventory(); ++j){
            ItemStack itemstack2 = invCrafting.getStackInSlot(j);

            if (itemstack2 != null){
                if (i == 0){
                    itemstack = itemstack2;
                }
                if (i == 1){
                    itemstack1 = itemstack2;
                }
                ++i;
            }
        }

        if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable()) {
            Item item = itemstack.getItem();
            int j1 = item.getMaxDamage() - itemstack.getItemDamage();
            int k = item.getMaxDamage() - itemstack1.getItemDamage();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;

            if (i1 < 0){
                i1 = 0;
            }

            return new ItemStack(itemstack.getItem(), 1, i1);
        }else{
            for (j = 0; j < RECIPES.size(); ++j){
                SolderingRecipeWrapper recipe = RECIPES.get(j);
                if (recipe.matches(invCrafting, world)){
                    return recipe.getCraftingResult(invCrafting);
                }
            }
            return null;
        }
    }
    public List getRecipeList(){
        return RECIPES;
    }
}
