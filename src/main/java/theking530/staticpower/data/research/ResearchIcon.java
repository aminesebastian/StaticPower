package theking530.staticpower.data.research;

import com.google.gson.JsonElement;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class ResearchIcon {
	private final ItemStack itemIcon;
	private final ResourceLocation textureIcon;

	public ResearchIcon(ItemStack itemIcon, ResourceLocation textureIcon) {
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
	}

	public ItemStack getItemIcon() {
		return itemIcon;
	}

	public ResourceLocation getTextureIcon() {
		return textureIcon;
	}

	public boolean isItemIcon() {
		return itemIcon != null;
	}

	public void toBuffer(FriendlyByteBuf buffer) {
		buffer.writeBoolean(isItemIcon());
		if (isItemIcon()) {
			buffer.writeItem(getItemIcon());
		} else {
			buffer.writeUtf(getTextureIcon().toString());
		}
	}

	public static ResearchIcon fromBuffer(FriendlyByteBuf buffer) {
		ItemStack itemIcon = null;
		ResourceLocation textureIcon = null;
		if (buffer.readBoolean()) {
			itemIcon = buffer.readItem();
		} else {
			textureIcon = new ResourceLocation(buffer.readUtf());
		}
		return new ResearchIcon(itemIcon, textureIcon);
	}

	public static ResearchIcon fromJson(JsonElement element) {
		ItemStack itemIcon = null;
		ResourceLocation textureIcon = null;
		if (element.isJsonObject()) {
			itemIcon = ShapedRecipe.itemStackFromJson(element.getAsJsonObject());
		} else {
			textureIcon = new ResourceLocation(element.getAsString());
		}
		return new ResearchIcon(itemIcon, textureIcon);
	}
}
