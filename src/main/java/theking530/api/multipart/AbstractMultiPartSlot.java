package theking530.api.multipart;

import net.minecraft.item.ItemStack;

public abstract class AbstractMultiPartSlot {
	private final String name;
	private final String unlocalizedName;
	private final boolean isOptional;

	public AbstractMultiPartSlot(String name, String unlocalizedName, boolean isOptional) {
		this.name = name;
		this.unlocalizedName = unlocalizedName;
		this.isOptional = isOptional;
	}

	public String getName() {
		return name;
	}

	public String getUnlocalizedName() {
		return unlocalizedName;
	}

	public boolean isOptional() {
		return this.isOptional;
	}

	public abstract boolean canAcceptItem(ItemStack stack);
}
