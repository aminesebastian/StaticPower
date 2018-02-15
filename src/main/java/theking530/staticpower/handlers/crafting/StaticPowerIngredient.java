package theking530.staticpower.handlers.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class StaticPowerIngredient extends Ingredient {

    public StaticPowerIngredient(ItemStack... stacks) {
        super(stacks);      
    }
    
    @Override
    public boolean apply(@Nullable ItemStack input) {
        if (input == null || !input.isEmpty()) {
            return false;
        }
        for(ItemStack matchItem : getMatchingItemStacks()) {
          if(ItemStack.areItemStacksEqual(matchItem, input) && input.getCount() == matchItem.getCount()) {
            if(matchItem.hasTagCompound()) {
              if(input.hasTagCompound() && matchItem.getTagCompound().equals(input.getTagCompound()) {
                return true;
              }else{
                continue;
              }
            }
            return true;
          }
        }
        return false;
    }
}