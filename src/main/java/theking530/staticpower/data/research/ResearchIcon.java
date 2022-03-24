package theking530.staticpower.data.research;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import theking530.staticcore.gui.GuiDrawUtilities;

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

	public static ResearchIcon fromItem(ItemStack stack) {
		return new ResearchIcon(stack, null);
	}

	public static ResearchIcon fromTexture(ResourceLocation textureIcon) {
		return new ResearchIcon(null, textureIcon);
	}
	public static void draw(ResearchIcon icon, PoseStack pose, float x, float y, float z, float width, float height) {
		if (icon.isItemIcon()) {
			GuiDrawUtilities.drawItem(pose, icon.getItemIcon(), x, y, z, width, height);
		} else if (icon.getTextureIcon() != null) {
			GuiDrawUtilities.drawTexture(pose, icon.getTextureIcon(), width, height, x, y, z, 0, 0, 1, 1);
		}
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
