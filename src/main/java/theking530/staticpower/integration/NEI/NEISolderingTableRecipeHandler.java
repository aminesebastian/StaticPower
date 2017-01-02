package theking530.staticpower.integration.NEI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import codechicken.core.ReflectionManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.util.NEIServerUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import theking530.staticpower.client.gui.widgets.GuiDrawItem;
import theking530.staticpower.handlers.crafting.registries.SolderingRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.SolderingRecipeWrapper;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.machines.solderingtable.GuiSolderingTable;
import theking530.staticpower.utils.GuiTextures;

public class NEISolderingTableRecipeHandler extends TemplateRecipeHandler {
	
	public GuiDrawItem DRAW_ITEM = new GuiDrawItem(true);
	
    public class CachedShapedRecipe extends CachedRecipe {
        public ArrayList<PositionedStack> ingredients;
        public PositionedStack result;

        public CachedShapedRecipe(int width, int height, Object[] items, ItemStack out) {
            result = new PositionedStack(out, 135, 28);
            PositionedStack stack = new PositionedStack(new ItemStack(ModItems.SolderingIron), 30, 30);
            ingredients = new ArrayList<PositionedStack>();
            setIngredients(width, height, items);
        }

        public CachedShapedRecipe(SolderingRecipeWrapper recipe) {
            this(recipe.recipeWidth, recipe.recipeHeight, recipe.recipeItems, recipe.getRecipeOutput());
        }

        /**
         * @param width
         * @param height
         * @param items  an ItemStack[] or ItemStack[][]
         */
        public void setIngredients(int width, int height, Object[] items) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (items[y * width + x] == null)
                        continue;

                    PositionedStack stack = new PositionedStack(items[y * width + x], 57 + x * 18, 6 + y * 18, false);
                    stack.setMaxSize(1);
                    ingredients.add(stack);
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        public PositionedStack getResult() {
            return result;
        }

        public void computeVisuals() {
            for (PositionedStack p : ingredients)
                p.generatePermutations();
        }
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(110, 23, 24, 18), "soldering"));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiSolderingTable.class;
    }

    @Override
    public String getRecipeName() {
        return "Soldering";
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("soldering") && getClass() == NEISolderingTableRecipeHandler.class) {
            for (SolderingRecipeWrapper irecipe : (List<SolderingRecipeWrapper>) SolderingRecipeRegistry.Soldering().getRecipeList()) {
                CachedShapedRecipe recipe = null;
                recipe = new CachedShapedRecipe(irecipe);
                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (SolderingRecipeWrapper irecipe : (List<SolderingRecipeWrapper>) SolderingRecipeRegistry.Soldering().getRecipeList()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                CachedShapedRecipe recipe = null;
                recipe = new CachedShapedRecipe(irecipe);

                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (SolderingRecipeWrapper irecipe : (List<SolderingRecipeWrapper>) SolderingRecipeRegistry.Soldering().getRecipeList()) {
            CachedShapedRecipe recipe = null;
            recipe = new CachedShapedRecipe(irecipe);

            if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem()))
                continue;

            recipe.computeVisuals();
            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                arecipes.add(recipe);
            }
        }
    }

    public CachedShapedRecipe forgeShapedRecipe(ShapedOreRecipe recipe) {
        try {
            int width = ReflectionManager.getField(ShapedOreRecipe.class, Integer.class, recipe, 4);
            int height = ReflectionManager.getField(ShapedOreRecipe.class, Integer.class, recipe, 5);

            Object[] items = recipe.getInput();
            for (Object item : items)
                if (item instanceof List && ((List<?>) item).isEmpty())//ore handler, no ores
                    return null;

            return new CachedShapedRecipe(width, height, items, recipe.getRecipeOutput());
        } catch (Exception e) {
            //NEIClientConfig.error("Error loading recipe: ", e);
            return null;
        }
    }

    @Override
    public String getGuiTexture() {
        return GuiTextures.SOLDERING_TABLE_GUI.toString();
    }

    @Override
    public String getOverlayIdentifier() {
        return "soldering";
    }

    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return super.hasOverlay(gui, container, recipe) ||
                isRecipe2x2(recipe) && RecipeInfo.hasDefaultOverlay(gui, "crafting2x2");
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
        IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
        if (renderer != null)
            return renderer;

        IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "crafting2x2");
        if (positioner == null)
            return null;
        return new DefaultOverlayRenderer(getIngredientStacks(recipe), positioner);
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
        if (handler != null)
            return handler;

        return RecipeInfo.getOverlayHandler(gui, "crafting2x2");
    }

    public boolean isRecipe2x2(int recipe) {
        for (PositionedStack stack : getIngredientStacks(recipe))
            if (stack.relx > 43 || stack.rely > 24)
                return false;

        return true;
    }

    @Override
    public void drawExtras(int recipe) {
    	CachedShapedRecipe tempRecipe = (CachedShapedRecipe)(arecipes.get(recipe));   
    	
    	DRAW_ITEM.drawItem(ModItems.SolderingIron, 0, 0, 6, 6, 1);
        
    }
}

