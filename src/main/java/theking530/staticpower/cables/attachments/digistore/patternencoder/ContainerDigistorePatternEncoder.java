package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal.TerminalViewType;
import theking530.staticpower.cables.digistore.crafting.EncodedDigistorePattern;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.container.slots.EncodedPatternSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;

public class ContainerDigistorePatternEncoder extends AbstractContainerDigistoreTerminal<DigistorePatternEncoder> implements IJEIReipceTransferHandler {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistorePatternEncoder, GuiDigistorePatternEncoder> TYPE = new ContainerTypeAllocator<>("digistore_pattern_terminal",
			ContainerDigistorePatternEncoder::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistorePatternEncoder::new);
		}
	}

	private ItemStackHandler encoderInventory;
	private RecipeEncodingType currentRecipeType;
	private List<PhantomSlot> inputSlots;
	private PhantomSlot outputSlot;

	public ContainerDigistorePatternEncoder(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistorePatternEncoder(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		// Initalize the default values.
		currentRecipeType = RecipeEncodingType.CRAFTING_TABLE;
		inputSlots = new ArrayList<PhantomSlot>();

		// Attempt to get the item filter inventory.
		getAttachment().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((handler) -> {
			encoderInventory = (ItemStackHandler) handler;
		});


		// Add the crafting input slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				Slot slot = addSlot(new PhantomSlot(encoderInventory, DigistorePatternEncoder.RECIPE_START_SLOT + x + (y * 3), 8 + x * 18, 118 + y * 18, true) {
					@Override
					public void setChanged() {
						super.setChanged();
						updateOutputSlot();
					}

					@Override
					public boolean isActive() {
						return getViewType() == TerminalViewType.ITEMS;
					}
				});
				inputSlots.add((PhantomSlot) slot);
			}
		}

		// Add the pattern slots.
		addSlot(new EncodedPatternSlot(encoderInventory, new ItemStack(ModItems.PatternCard), DigistorePatternEncoder.PATTERN_INPUT_SLOT, 145, 116) {
			@Override
			public boolean isActive() {
				return getViewType() == TerminalViewType.ITEMS;
			}
		});
		addSlot(new OutputSlot(encoderInventory, ModItems.PatternCard.getBlankEncodedCardForPreview(), DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, 145, 156) {
			@Override
			public boolean isActive() {
				return getViewType() == TerminalViewType.ITEMS;
			}
		});

		// Add crafting output slot.
		outputSlot = createOutputSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, 90, 136, false);

		// Start in crafting mode.
		setCurrentRecipeType(RecipeEncodingType.CRAFTING_TABLE);

		// This HAS to come last.
		super.initializeContainer();
	}

	@Override
	public void consumeJEITransferRecipe(Player player, ItemStack[][] recipe) {
		clearRecipe();
		if (!getCableComponent().getWorld().isClientSide && getCableComponent().isManagerPresent()) {
			for (int i = 0; i < recipe.length; i++) {
				// Get the options.
				ItemStack[] options = recipe[i];

				// Skip holes in the recipe.
				if (options == null) {
					encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_START_SLOT + i, ItemStack.EMPTY);
				} else {
					encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_START_SLOT + i, options[0]);
				}
			}
			markForResync();
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
		// This can only happen on the server.
		if (getCableComponent().getWorld().isClientSide) {
			return;
		}

		// If we're not in crafting mode, do nothing.
		if (currentRecipeType != RecipeEncodingType.CRAFTING_TABLE) {
			return;
		}

		// Created a simulated crafting inventory.
		FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
		for (int i = 0; i < 9; i++) {
			craftingInv.setItem(i, encoderInventory.getStackInSlot(DigistorePatternEncoder.RECIPE_START_SLOT + i));
		}

		// Check for a recipe.
		Optional<CraftingRecipe> optional = getCableComponent().getWorld().getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInv, getCableComponent().getWorld());

		// If the recipe exists, update the output slots.
		if (optional.isPresent()) {
			CraftingRecipe icraftingrecipe = optional.get();
			encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, icraftingrecipe.getResultItem());
		} else {
			encoderInventory.setStackInSlot(DigistorePatternEncoder.RECIPE_SINGLE_SLOT, ItemStack.EMPTY);
		}

		markForResync();
	}

	protected PhantomSlot createOutputSlot(int index, int x, int y, boolean enableInput) {
		PhantomSlot slot = (PhantomSlot) addSlot(new PhantomSlot(encoderInventory, index, x, y, true) {
			@Override
			public void insertPhantomItem(ItemStack stack, int amount) {
				if (currentRecipeType == RecipeEncodingType.MACHINE) {
					super.insertPhantomItem(stack, amount);
				}
			}

			@Override
			public void decreasePhantomCount(int amount) {
				if (currentRecipeType == RecipeEncodingType.MACHINE) {
					super.decreasePhantomCount(amount);
				}
			}

			@Override
			public void clearPhantom() {
				if (currentRecipeType == RecipeEncodingType.MACHINE) {
					super.clearPhantom();
				}
			}

			@Override
			public boolean isActive() {
				return getViewType() == TerminalViewType.ITEMS;
			}
		});
		return slot;
	}

	public RecipeEncodingType getCurrentRecipeType() {
		return currentRecipeType;
	}

	public void setCurrentRecipeType(RecipeEncodingType currentRecipeType) {
		// Update the recipe type.
		this.currentRecipeType = currentRecipeType;

		// If this is a crafting recipe type, limit the input slot item counts to 1. If
		// this is a machine recipe, un-limit the counts.
		if (currentRecipeType == RecipeEncodingType.CRAFTING_TABLE) {
			// Limit the inputs to a stack size of 1.
			for (PhantomSlot slot : inputSlots) {
				slot.setLimitToSingleItem(true);
			}
			outputSlot.setLimitToSingleItem(true);
		} else {
			// Allow the inputs to grow past 1 stack size.
			for (PhantomSlot slot : inputSlots) {
				slot.setLimitToSingleItem(false);
			}
			outputSlot.setLimitToSingleItem(false);
		}
	}

	public void attemptEncode() {
		// Do nothing on the client!
		if (getCableComponent().getWorld().isClientSide) {
			return;
		}

		// Do nothing if the manager is not present.
		if (!getCableComponent().isManagerPresent()) {
			return;
		}

		// Make sure we have a pattern.
		if (encoderInventory.getStackInSlot(DigistorePatternEncoder.PATTERN_INPUT_SLOT).isEmpty()) {
			return;
		}

		// Capture the inputs.
		ItemStack[] inputs = new ItemStack[9];
		for (int i = 0; i < 9; i++) {
			inputs[i] = encoderInventory.getStackInSlot(i + DigistorePatternEncoder.RECIPE_START_SLOT).copy();
		}

		// Capture the outputs.
		ItemStack output = encoderInventory.getStackInSlot(DigistorePatternEncoder.RECIPE_OUTPUT_SLOT).copy();

		// If there are no output, do nothing.
		if (output.isEmpty()) {
			return;
		}

		// Create the encoded recipe.
		ItemStack encodedRecipe = ItemStack.EMPTY;

		// Get the pattern id.
		long id = CableNetworkManager.get(getCableComponent().getWorld()).getCurrentPatternId();

		// If we're in crafting mode, also cache the recipe Id. If not, just use the
		// inputs and outputs.
		if (currentRecipeType == RecipeEncodingType.CRAFTING_TABLE) {
			// Created a simulated crafting inventory.
			FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);
			for (int i = 0; i < 9; i++) {
				craftingInv.setItem(i, encoderInventory.getStackInSlot(DigistorePatternEncoder.RECIPE_START_SLOT + i));
			}

			// Check for a recipe.
			CraftingRecipe recipe = getCableComponent().getWorld().getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInv, getCableComponent().getWorld()).orElse(null);
			if (recipe != null) {
				encodedRecipe = ModItems.PatternCard.getPatternForRecipe(new EncodedDigistorePattern(id, inputs, recipe));
				CableNetworkManager.get(getCableComponent().getWorld()).incrementCurrentPatternId();
			}
		} else {
			encodedRecipe = ModItems.PatternCard.getPatternForRecipe(new EncodedDigistorePattern(id, inputs, output, currentRecipeType));
			CableNetworkManager.get(getCableComponent().getWorld()).incrementCurrentPatternId();
		}

		// Check to make sure the output slot can stack with this encoded recipe (odd
		// chance the user may encode the same recipe multiple times).
		if (!encodedRecipe.isEmpty()) {
			ItemStack simulatedInsert = encoderInventory.insertItem(DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, encodedRecipe.copy(), true);
			if (simulatedInsert.isEmpty()) {
				encoderInventory.insertItem(DigistorePatternEncoder.PATTERN_OUTPUT_SLOT, encodedRecipe.copy(), false);
				encoderInventory.extractItem(DigistorePatternEncoder.PATTERN_INPUT_SLOT, 1, false);
			}
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
	public void removed(Player playerIn) {
		super.removed(playerIn);
		clearRecipe();
	}

}
