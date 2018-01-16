package theking530.staticpower.handlers.crafting.wrappers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class SolderingRecipeWrapper {
	public final int recipeWidth;
    public final int recipeHeight;
    public final ItemStack[] recipeItems;
    private final ItemStack recipeOutput;
    private boolean field_92101_f;

    public SolderingRecipeWrapper(int x, int y, ItemStack[] inputs, ItemStack output) {
        this.recipeWidth = x;
        this.recipeHeight = y;
        this.recipeItems = inputs;
        this.recipeOutput = output;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(IItemHandler inventory, World world) {
        for (int i = 0; i <= 3 - this.recipeWidth; ++i){
            for (int j = 0; j <= 3 - this.recipeHeight; ++j){
                if (this.checkMatch(inventory, i, j, true)){
                    return true;
                }
                if (this.checkMatch(inventory, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(IItemHandler inventory, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
        for (int k = 0; k < 3; ++k) {
            for (int l = 0; l < 3; ++l) {
                int i1 = k - p_77573_2_;
                int j1 = l - p_77573_3_;
                ItemStack itemstack = ItemStack.EMPTY;

                if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight){
                    if (p_77573_4_){
                        itemstack = this.recipeItems[this.recipeWidth - i1 - 1 + j1 * this.recipeWidth];
                    }else{
                        itemstack = this.recipeItems[i1 + j1 * this.recipeWidth];
                    }
                }

                ItemStack itemstack1 = getStackInRowAndColumn(inventory, k, l);

                if (itemstack1 != ItemStack.EMPTY || itemstack != ItemStack.EMPTY){
                   // if (itemstack1 == ItemStack.EMPTY && itemstack != ItemStack.EMPTY || itemstack1 != ItemStack.EMPTY && itemstack == ItemStack.EMPTY){
                       // return false;
                    //}

                    if (itemstack.getItem() != itemstack1.getItem()){
                        return false;
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage()){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public ItemStack getStackInRowAndColumn(IItemHandler inventory, int p_70463_1_, int p_70463_2_) {
        if (p_70463_1_ >= 0 && p_70463_1_ < 3){
            int k = p_70463_1_ + p_70463_2_ * 3;
            return inventory.getStackInSlot(k);
        }else{
            return ItemStack.EMPTY;
        }
    }
    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(IItemHandler inventory){
        ItemStack itemstack = this.getRecipeOutput().copy();
        if (this.field_92101_f) {
            for (int i = 0; i < inventory.getSlots(); ++i){
                ItemStack itemstack1 = inventory.getStackInSlot(i);

                if (itemstack1 != ItemStack.EMPTY && itemstack1.hasTagCompound()){
                    itemstack.setTagCompound((NBTTagCompound)itemstack1.getTagCompound().copy());
                }
            }
        }
        return itemstack;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize(){
        return this.recipeWidth * this.recipeHeight;
    }

    public SolderingRecipeWrapper func_92100_c(){
        this.field_92101_f = true;
        return this;
    }
}