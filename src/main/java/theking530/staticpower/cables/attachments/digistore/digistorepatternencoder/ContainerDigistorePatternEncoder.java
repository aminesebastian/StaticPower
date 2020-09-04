package theking530.staticpower.cables.attachments.digistore.digistorepatternencoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.digistorepatternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.AbstractContainerDigistoreTerminal;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.PlayerArmorItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;
import theking530.staticpower.items.DigistorePatternCard.EncodedDigistorePattern;

public class ContainerDigistorePatternEncoder extends AbstractContainerDigistoreTerminal<DigistorePatternEncoder> implements IJEIReipceTransferHandler {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistorePatternEncoder, GuiDigistorePatternEncoder> TYPE = new ContainerTypeAllocator<>("digistore_pattern_terminal",
			ContainerDigistorePatternEncoder::new, GuiDigistorePatternEncoder::new);

	private ItemStackHandler encoderInventory;
	private RecipeEncodingType currentRecipeType;
	private List<StaticPowerContainerSlot> machineOutputSlots;
	private StaticPowerContainerSlot craftingOutputSlot;

	public ContainerDigistorePatternEncoder(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistorePatternEncoder(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Initalize the default values.
		currentRecipeType = RecipeEncodingType.CRAFTING;
		machineOutputSlots = new ArrayList<StaticPowerContainerSlot>();

		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			encoderInventory = (ItemStackHandler) handler;
		});

		// Limit the view to only show 5 rows to make room for the crafting GUI.
		setMaxRows(5);

		// Add the crafting input slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new PhantomSlot(encoderInventory, DigistorePatternEncoder.RECIPE_START_SLOT + x + (y * 3), 8 + x * 18, 118 + y * 18, true) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						updateOutputSlot();
					}
				});
			}
		}

		// Add the pattern slots.
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.PatternCard), 0.3f, encoderInventory, DigistorePatternEncoder.PATTERN_INPUT_SLOT, 152, 118) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				if (stack.isEmpty()) {
					return false;
				}
				return stack.getItem() == ModItems.PatternCard;
			}
		});
		addSlot(new OutputSlot(encoderInventory, ModItems.PatternCard.getBlankEncodedCardForPreview(), DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, 152, 154));

		// Add machine output slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				StaticPowerContainerSlot slot = createOutputSlot(DigistorePatternEncoder.RECIPE_OUTPUT_START_SLOT + x + (y * 3), 90 + x * 18, 118 + y * 18, true);
				machineOutputSlots.add((StaticPowerContainerSlot) slot);
			}
		}

		// Add crafting output slot.
		craftingOutputSlot = createOutputSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, 90, 136, false);

		// Armor
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 39, -18, 109, EquipmentSlotType.HEAD));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 38, -18, 127, EquipmentSlotType.CHEST));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 37, -18, 145, EquipmentSlotType.LEGS));
		addSlot(new PlayerArmorItemSlot(getPlayerInventory(), 36, -18, 163, EquipmentSlotType.FEET));

		// Start in crafting mode.
		setCurrentRecipeType(RecipeEncodingType.CRAFTING);

		super.initializeContainer();
	}

	@Override
	public void consumeJEITransferRecipe(ItemStack[][] recipe) {
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
			for (int i = 0; i < recipe.length; i++) {
				ItemStack[] options = recipe[i];

				// Skip holes in the recipe.
				if (options == null) {
					continue;
				}

				encoderInventory.setStackInSlot(i, options[0]);
			}
			updateOutputSlot();
		}
	}

	/**
	 * Update the crafting output slot's contents.
	 * 
	 * @param slotIndex
	 * 
	 * @param world
	 * 
	 * @param player
	 * 
	 * @param craftingInv
	 * 
	 * @param outputInv
	 */
	protected void updateOutputSlot() {
		if (currentRecipeType != RecipeEncodingType.CRAFTING) {
			return;
		}
		if (!getCableComponent().getWorld().isRemote) {
			// Created a simulated crafting inventory.
			FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				craftingInv.setInventorySlotContents(i, encoderInventory.getStackInSlot(DigistorePatternEncoder.RECIPE_START_SLOT + i));
			}

			// Check for a recipe.
			Optional<ICraftingRecipe> optional = getCableComponent().getWorld().getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInv, getCableComponent().getWorld());

			// If the recipe exists, update the output slots.
			if (optional.isPresent()) {
				ICraftingRecipe icraftingrecipe = optional.get();
				encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, icraftingrecipe.getRecipeOutput());
			} else {
				encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, ItemStack.EMPTY);
			}
		}
	}

	protected StaticPowerContainerSlot createOutputSlot(int index, int x, int y, boolean enableInput) {
		StaticPowerContainerSlot slot = (StaticPowerContainerSlot) addSlot(new StaticPowerContainerSlot(encoderInventory, index, x, y) {
			@Override
			public boolean canTakeStack(PlayerEntity playerIn) {
				if (enableInput) {
					encoderInventory.setStackInSlot(index, ItemStack.EMPTY);
				}
				return false;
			}

			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				if (stack.isEmpty()) {
					return false;
				}
				if (enableInput) {
					putStack(stack.copy());
				}
				return false;
			}
		});
		return slot;
	}

	public RecipeEncodingType getCurrentRecipeType() {
		return currentRecipeType;
	}

	public void setCurrentRecipeType(RecipeEncodingType currentRecipeType) {
		this.currentRecipeType = currentRecipeType;
		if (currentRecipeType == RecipeEncodingType.CRAFTING) {
			for (StaticPowerContainerSlot slot : machineOutputSlots) {
				slot.setEnabledState(false);
			}
			craftingOutputSlot.setEnabledState(true);
		} else {
			for (StaticPowerContainerSlot slot : machineOutputSlots) {
				slot.setEnabledState(true);
			}
			craftingOutputSlot.setEnabledState(false);
		}
	}

	public void attemptEncode() {
		// Do nothing on the client!
		if (getCableComponent().getWorld().isRemote) {
			return;
		}

		// Make sure we have a pattern.
		if (encoderInventory.getStackInSlot(DigistorePatternEncoder.PATTERN_INPUT_SLOT).isEmpty()) {
			return;
		}

		// Mark a flag to make sure an output exists.
		boolean outputPresent = false;

		// Capture the inputs.
		ItemStack[] inputs = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			inputs[i] = encoderInventory.getStackInSlot(i + DigistorePatternEncoder.RECIPE_START_SLOT).copy();
		}

		// Capture the outputs.
		ItemStack[] outputs = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			outputs[i] = encoderInventory.getStackInSlot(i + DigistorePatternEncoder.RECIPE_OUTPUT_START_SLOT).copy();
			if (!outputs[i].isEmpty()) {
				outputPresent = true;
			}
		}

		// If there are no outputs, do nothing.
		if (!outputPresent) {
			return;
		}

		// Create the encoded recipe.
		ItemStack encodedRecipe = ModItems.PatternCard.getEncodedRecipe(new EncodedDigistorePattern(inputs, outputs, currentRecipeType));

		// Check to make sure the output slot can stack with this encoded recipe (odd
		// chance the user may encode the same recipe multiple times).
		ItemStack simulatedInsert = encoderInventory.insertItem(DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, encodedRecipe.copy(), true);
		if (simulatedInsert.isEmpty()) {
			encoderInventory.insertItem(DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, encodedRecipe.copy(), false);
			encoderInventory.extractItem(DigistorePatternEncoder.PATTERN_INPUT_SLOT, 1, false);
		}
	}

	public void clearRecipe() {
		for (int i = 0; i < DigistorePatternEncoder.PATTERN_INPUT_SLOT; i++) {
			encoderInventory.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		clearRecipe();
	}

}
