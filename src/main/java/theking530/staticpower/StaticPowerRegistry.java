package theking530.staticpower;

import java.util.HashSet;
import java.util.LinkedHashSet;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import theking530.staticcore.initialization.StaticCoreRegistry;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.world.trees.AbstractStaticPowerTree;

/**
 * Main registry class responsible for preparing entities for registration and
 * then registering them.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerRegistry {
	public static final HashSet<AbstractStaticPowerTree> TREES = new LinkedHashSet<>();

	/**
	 * Pre-registers a tree for registration through the init method.
	 * 
	 * @param tree The tree to pre-register.
	 * @return The tree that was passed.
	 */
	public static AbstractStaticPowerTree preRegisterTree(AbstractStaticPowerTree tree) {
		TREES.add(tree);
		return tree;
	}

	public static void onRegisterTileEntityTypes(RegistryEvent.Register<BlockEntityType<?>> event) {
		StaticCoreRegistry.registerTileEntityTypes(event);
	}

	public static void onRegisterContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
		StaticCoreRegistry.registerContainerTypes(event);
	}

	public static void onRegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		ModRecipeSerializers.onRegisterRecipeSerializers(event);
	}
}
