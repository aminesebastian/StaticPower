package theking530.staticcore.blockentity.components.control.processing.recipe;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import theking530.staticcore.blockentity.components.control.processing.basic.BasicProcessingComponentSyncPacket;

public class RecipeProcessingComponentSyncPacket extends BasicProcessingComponentSyncPacket {
	private ResourceLocation recipeId;

	public RecipeProcessingComponentSyncPacket() {

	}

	public <T extends RecipeProcessingComponent<K>, K extends Recipe<?>> RecipeProcessingComponentSyncPacket(
			BlockPos pos, T component) {
		super(pos, component);

		Optional<K> optionalRecipe = component.getProcessingRecipe();
		if (optionalRecipe.isPresent()) {
			recipeId = optionalRecipe.get().getId();
		} else {
			recipeId = null;
		}
	}

	public ResourceLocation getRecipeId() {
		return recipeId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		super.encode(buffer);
		buffer.writeBoolean(recipeId != null);
		if (recipeId != null) {
			buffer.writeUtf(recipeId.toString());
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		super.decode(buffer);

		if (buffer.readBoolean()) {
			recipeId = new ResourceLocation(buffer.readUtf());
		}
	}
}
