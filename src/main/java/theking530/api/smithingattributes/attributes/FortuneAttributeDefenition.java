package theking530.api.smithingattributes.attributes;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import theking530.api.smithingattributes.attributes.modifiers.IntegerAttributeModifier;

public class FortuneAttributeDefenition extends AbstractAttributeDefenition<Integer, IntegerAttributeModifier> {

	public FortuneAttributeDefenition(ResourceLocation id) {
		super(id, "gui.staticpower.fortune", TextFormatting.BLUE, IntegerAttributeModifier.class);
	}
	
	@Override
	public ITextComponent getTooltipValue() {
		return null;
	}

}
