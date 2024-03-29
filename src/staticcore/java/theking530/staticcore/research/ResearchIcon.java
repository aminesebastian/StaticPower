package theking530.staticcore.research;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.JsonUtilities;

public class ResearchIcon {
	private static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation("", "");
	public static final ResearchIcon EMPTY = new ResearchIcon(ItemStack.EMPTY, EMPTY_TEXTURE);
	private final ItemStack itemIcon;
	private final ResourceLocation textureIcon;

	public static final Codec<ResearchIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			JsonUtilities.ITEMSTACK_CODEC.optionalFieldOf("item", ItemStack.EMPTY)
					.forGetter(icon -> icon.getItemIcon()),
			ResourceLocation.CODEC.optionalFieldOf("texture", EMPTY_TEXTURE).forGetter(icon -> icon.getTextureIcon()))
			.apply(instance, ResearchIcon::new));

	public ResearchIcon(ItemStack itemIcon, ResourceLocation textureIcon) {
		this.itemIcon = itemIcon;
		this.textureIcon = textureIcon;
	}

	public ItemStack getItemIcon() {
		return itemIcon == null ? ItemStack.EMPTY : itemIcon;
	}

	public ResourceLocation getTextureIcon() {
		return textureIcon == null ? EMPTY_TEXTURE : textureIcon;
	}

	public boolean isItemIcon() {
		return itemIcon != null && !itemIcon.isEmpty();
	}

	public static ResearchIcon fromItem(ItemStack stack) {
		return new ResearchIcon(stack, null);
	}

	public static ResearchIcon fromItem(Item item) {
		return new ResearchIcon(new ItemStack(item), null);
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
			itemIcon = JsonUtilities.itemStackFromJson(element.getAsJsonObject());
		} else {
			textureIcon = new ResourceLocation(element.getAsString());
		}
		return new ResearchIcon(itemIcon, textureIcon);
	}
}
