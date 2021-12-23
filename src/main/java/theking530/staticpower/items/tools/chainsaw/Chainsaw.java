package theking530.staticpower.items.tools.chainsaw;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.multipartitem.AbstractMultiPartSlot;
import theking530.api.multipartitem.MultiPartSlots;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.ChainsawItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class Chainsaw extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final Set<Block> FULL_SPEED_BLOCKS = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON,
			Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);

	private static final Set<ToolType> TOOL_TYPES = new HashSet<ToolType>();
	private static final List<AbstractMultiPartSlot> PARTS = new ArrayList<AbstractMultiPartSlot>();
	private static final int MAX_RECURSION = 100;
	private Ingredient woodIngredient;
	public final ResourceLocation tier;

	public Chainsaw(String name, float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), name, attackDamageIn, attackSpeedIn);
		this.tier = tier;
		TOOL_TYPES.add(ToolType.AXE);
		PARTS.add(MultiPartSlots.CHAINSAW_BLADE);
	}

	public long getCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 2;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	@Override
	public List<AbstractMultiPartSlot> getSlots(ItemStack stack) {
		return PARTS;
	}

	@Override
	public ItemStack getPartInSlot(ItemStack stack, AbstractMultiPartSlot slot) {
		if (slot == MultiPartSlots.CHAINSAW_BLADE) {
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inventory != null) {
				return inventory.getStackInSlot(0);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return isComplete(itemstack) && EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(5.0f);

		if (isSlotPopulated(itemstack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(itemstack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			efficiency.set(bladeItem.getMiningTier(blade).getSpeed() * 0.25f);
			blade.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(HasteAttributeDefenition.ID)) {
					HasteAttributeDefenition hasteDefenition = (HasteAttributeDefenition) attributable.getAttribute(HasteAttributeDefenition.ID);
					efficiency.set(efficiency.get() * (((hasteDefenition.getValue() * 10.0f) / 300.0f) + 1.0f));
				}
			});
		}
		return efficiency.get();
	}

	@Override
	public int getHarvestLevel(ItemStack stack, ToolType tool, @Nullable Player player, @Nullable BlockState blockState) {
		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			return bladeItem.getMiningTier(blade).getLevel();
		}
		return super.getHarvestLevel(stack, tool, player, blockState);
	}

	/**
	 * When right clicked, open the UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && player.isShiftKeyDown()) {
			NetworkGUI.openGui((ServerPlayer) player, new ChainsawContainerProvider(item), buff -> {
				buff.writeInt(player.inventory.selected);
			});
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
	}

	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayer player, BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// If the player is in creative, do nothing.
		if (isCreative) {
			return;
		}

		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getLevel(), pos, tileEntity, player, heldItem);

		// Get the drill bit attributes.
		IAttributable drillBitAttributes = getPartInSlot(heldItem, MultiPartSlots.CHAINSAW_BLADE).getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);

		// If we have attributes.
		if (drillBitAttributes != null) {
			// Check for the smelting attribute. If we do, handle it.
			if (drillBitAttributes.hasAttribute(SmeltingAttributeDefenition.ID)) {
				// Get the smelting attribute.
				SmeltingAttributeDefenition smeltingAttribute = (SmeltingAttributeDefenition) drillBitAttributes.getAttribute(SmeltingAttributeDefenition.ID);
				handleSmeltingAttribute(smeltingAttribute, droppableItems, state, block, pos, player, tileEntity, heldItem, experience, isCreative);
			}
		}

		// Drop all the droppable stacks.
		for (ItemStack stack : droppableItems) {
			WorldUtilities.dropItem(player.getLevel(), pos, stack);
		}

		// Drop the XP.
		if (experience > 0) {
			state.getBlock().popExperience((ServerLevel) player.getCommandSenderWorld(), pos, experience);
		}

		// Spawn any additional drops.
		state.spawnAfterBreak((ServerLevel) player.getCommandSenderWorld(), pos, heldItem);
	}

	protected boolean handleSmeltingAttribute(SmeltingAttributeDefenition smeltingAttribute, List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos,
			ServerPlayer player, BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was smelted.
		boolean wasAnythingSmelted = false;

		// If the smelting attribute is enabled.
		if (smeltingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the furnace recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(droppableStack);
				Optional<SmeltingRecipe> recipe = player.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(matchParameters.getItems()[0]),
						player.getLevel());

				// Replace the spot the droppable list with the smelting output if it exists.
				if (recipe.isPresent()) {
					droppableItems.set(i, recipe.get().getResultItem());
					wasAnythingSmelted = true;
				}
			}
		}

		// Return the flag.
		return wasAnythingSmelted;
	}

	/**
	 * This method is only raised on the server.
	 */
	@Override
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, Player player) {
		// Apply damage to the bit.
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			handler.getStackInSlot(0).hurtAndBreak(blocksMined.size(), player, (entity) -> {
				entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		});

		// Update the energy usage on client and server. 1SV per block.
		if (!player.isCreative()) {
			EnergyHandlerItemStackUtilities.drainPower(stack, blocksMined.size() * 1000, false);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		long remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));

		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getTooltip(blade, worldIn, tooltip, isShowingAdvanced);
			tooltip.add(new TranslatableComponent("gui.staticpower.mining_speed").append(" ").append(String.valueOf(getEfficiency(stack))));
			AttributeUtilities.addTooltipsForAttribute(blade, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (isSlotPopulated(stack, MultiPartSlots.CHAINSAW_BLADE)) {
			ItemStack blade = getPartInSlot(stack, MultiPartSlots.CHAINSAW_BLADE);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getAdvancedTooltip(blade, worldIn, tooltip);
		}
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 5))
				.addCapability(new ItemStackStaticVoltCapability("default", stack, getCapacity(), getCapacity(), getCapacity()));
	}

	@Override
	protected boolean canHarvestBlockInternal(ItemStack stack, BlockState state) {
		// Check the tool.
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) {
			return true;
		}

		// If we are able to harvest the block full speed, no need to further check.
		if (FULL_SPEED_BLOCKS.contains(state.getBlock())) {
			return true;
		}

		// Then check by harvest level
		if (state.getHarvestTool() != null) {
			int i = this.getHarvestLevel(stack, state.getHarvestTool(), null, state);
			return i >= state.getHarvestLevel();
		}

		Material material = state.getMaterial();
		return material == Material.WOOD || material == Material.NETHER_WOOD;
	}

	@Override
	public boolean canHarvestAtFullSpeed(ItemStack stack, BlockState state) {
		// If we are able to harvest the block full speed, do so.
		if (FULL_SPEED_BLOCKS.contains(state.getBlock())) {
			return true;
		}

		// Check the tool.
		if (getToolTypes(stack).stream().anyMatch(e -> state.isToolEffective(e))) {
			return true;
		}

		// Finally, check the materials.
		Material material = state.getMaterial();
		return material == Material.WOOD || material == Material.NETHER_WOOD;
	}

	@Override
	public Set<ToolType> getToolTypes(ItemStack stack) {
		return TOOL_TYPES;
	}

	@Override
	public int getWidth(ItemStack itemstack) {
		return 1;
	}

	@Override
	public int getHeight(ItemStack itemstack) {
		return 1;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public List<BlockPos> getMineableExtraBlocks(ItemStack itemstack, BlockPos pos, Player player) {
		// Capture the list of all the blocks to mine.
		List<BlockPos> minableBlocks = new ArrayList<BlockPos>();

		// If we can't mine, just return the single block.
		if (!isReadyToMine(itemstack)) {
			minableBlocks.add(pos);
			return minableBlocks;
		}

		// Perform the recursive analysis.
		recursiveTreeAnalyzer(itemstack, pos, player, minableBlocks, new HashSet<BlockPos>(), 0);

		// Return the list of mineable blocks.
		return minableBlocks;
	}

	@SuppressWarnings("deprecation")
	private void recursiveTreeAnalyzer(ItemStack itemstack, BlockPos pos, Player player, List<BlockPos> positions, Set<BlockPos> visited, int currentDepth) {
		// If we maxed out on depth, stop.
		if (currentDepth > MAX_RECURSION) {
			return;
		}

		// If we have already visited this spot, stop.
		if (visited.contains(pos)) {
			return;
		}

		// Mark this spot as visited.
		visited.add(pos);

		// Get the state and block here, and check if they are air.
		BlockState state = player.getCommandSenderWorld().getBlockState(pos);
		Block block = state.getBlock();
		if (!block.isAir(state, player.getCommandSenderWorld(), pos)) {
			// Make an itemstack for the block.
			ItemStack blockStack = new ItemStack(Item.byBlock(block));

			// If not air, check to see if it is wood. If it is, and its harvestable,
			// harvest it.
			if (getLogTag().test(blockStack)) {
				if (this.canHarvestBlockInternal(itemstack, state)) {
					positions.add(pos);

					// Recurse.
					for (Direction dir : Direction.values()) {
						recursiveTreeAnalyzer(itemstack, pos.relative(dir), player, positions, visited, currentDepth + 1);
					}
				}
			}
		}
	}

	private Ingredient getLogTag() {
		if (woodIngredient == null) {
			woodIngredient = Ingredient.of(ModTags.LOG);
		}
		return woodIngredient;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new ChainsawItemModel(existingModel);
	}

	public class ChainsawContainerProvider implements MenuProvider {
		public ItemStack targetItemStack;

		public ChainsawContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			return new ContainerChainsaw(windowId, inventory, targetItemStack);
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}
}