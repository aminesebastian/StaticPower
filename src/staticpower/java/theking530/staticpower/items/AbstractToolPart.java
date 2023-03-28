package theking530.staticpower.items;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.type.AttributeType;
import theking530.api.item.compound.part.ICompoundItemPart;
import theking530.api.item.compound.slot.CompoundItemSlot;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.item.StaticCoreItem;
import theking530.staticpower.attributes.AbstractHardenedAttributeType;

public abstract class AbstractToolPart extends StaticCoreItem implements ICustomModelProvider, ICompoundItemPart {
	protected final ResourceLocation tier;
	private final Set<Supplier<CompoundItemSlot>> slots = new HashSet<>();

	@SuppressWarnings("unchecked")
	public AbstractToolPart(ResourceLocation tier, Properties properties, Supplier<CompoundItemSlot>... slots) {
		super(properties);
		this.tier = tier;
		for (Supplier<CompoundItemSlot> slot : slots) {
			this.slots.add(slot);
		}
	}

	public ResourceLocation getTier() {
		return this.tier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getMaxDamage(ItemStack stack) {
		// Get the base durability.
		AtomicInteger baseDurability = new AtomicInteger(getBaseDurability());

		// Apply hardening if possible.
		stack.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).ifPresent(attributable -> {

			// Check to see if we have an activated hardening attribute.
			for (AttributeType<?> attribute : attributable.getAllAttributes()) {
				// Get the instance.
				AttributeInstance<?> attributeInstance = attributable.getAttribute(attribute);

				// If it is an abstract hardened defenition and it IS activated.
				if (attribute instanceof AbstractHardenedAttributeType && attributeInstance.isActive()) {
					// Get an instance.
					AbstractHardenedAttributeType attrib = (AbstractHardenedAttributeType) attribute;

					// Apply the durability change. Then break the loop.
					baseDurability.set(attrib.applyHardening((AttributeInstance<Boolean>) attributeInstance, baseDurability.get()));
					break;
				}
			}
		});

		// Return the durability.
		return baseDurability.get();
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return this.tier == StaticCoreTiers.CREATIVE;
	}

	@Override
	public boolean canApplyToItem(ItemStack item, ItemStack part, CompoundItemSlot slot) {
		for (Supplier<CompoundItemSlot> registeredSlot : slots) {
			if (registeredSlot.get() == slot) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<Supplier<CompoundItemSlot>> getFullfilledSlots(ItemStack part) {
		return slots;
	}

	protected abstract int getBaseDurability();
}
