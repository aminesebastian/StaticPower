package theking530.staticpower.items.tools.miningdrill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.defenitions.FortuneAttributeDefenition;
import theking530.api.attributes.defenitions.GrindingAttributeDefenition;
import theking530.api.attributes.defenitions.HasteAttributeDefenition;
import theking530.api.attributes.defenitions.SilkTouchAttributeDefenition;
import theking530.api.attributes.defenitions.SmeltingAttributeDefenition;
import theking530.api.multipartitem.AbstractMultiPartSlot;
import theking530.api.multipartitem.MultiPartSlots;
import theking530.api.power.ItemStackStaticVoltCapability;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.MiningDrillItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class MiningDrill extends AbstractMultiHarvestTool implements ICustomModelSupplier {
	private static final List<AbstractMultiPartSlot> PARTS = new ArrayList<AbstractMultiPartSlot>();
	public final ResourceLocation tier;

	public MiningDrill(String name, float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), name, attackDamageIn, attackSpeedIn, Arrays.asList(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL));
		this.tier = tier;
		PARTS.add(MultiPartSlots.DRILL_BIT);
	}

	public long getPowerCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get() * 2;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.setEnergy(output, Integer.MAX_VALUE);
		return output;
	}

	@Override
	public ItemStack getPartInSlot(ItemStack stack, AbstractMultiPartSlot slot) {
		if (slot == MultiPartSlots.DRILL_BIT) {
			IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
			if (inventory != null) {
				return inventory.getStackInSlot(0);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public List<AbstractMultiPartSlot> getSlots(ItemStack stack) {
		return PARTS;
	}

	@Override
	public Tier getMiningTier(ItemStack stack) {
		ItemStack bitStack = getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
		if (bitStack.getItem() instanceof DrillBit) {
			DrillBit bit = (DrillBit) bitStack.getItem();
			return bit.getMiningTier(bitStack);
		}
		return Tiers.WOOD;
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return isComplete(itemstack) && EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(1.0f);

		if (isSlotPopulated(itemstack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBitStack = getPartInSlot(itemstack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBit = (DrillBit) drillBitStack.getItem();
			efficiency.set(drillBit.getMiningTier(drillBitStack).getSpeed() * 0.25f);
			drillBitStack.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(HasteAttributeDefenition.ID)) {
					HasteAttributeDefenition hasteDefenition = (HasteAttributeDefenition) attributable.getAttribute(HasteAttributeDefenition.ID);
					efficiency.set(efficiency.get() * (((hasteDefenition.getValue() * 10.0f) / 300.0f) + 1.0f));
				}
			});
		}
		return efficiency.get() * StaticPowerConfig.getTier(tier).drillSpeedMultiplier.get();
	}

	/**
	 * When right clicked, open the drill UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && player.isShiftKeyDown()) {
			NetworkGUI.openGui((ServerPlayer) player, new MiningDrillContainerProvider(item), buff -> {
				buff.writeInt(player.getInventory().selected);
			});
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
	}

	@Override
	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayer player, BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// If the player is in creative, do nothing.
		if (isCreative) {
			return;
		}

		// Get the drill bit attributes.
		IAttributable drillBitAttributes = getPartInSlot(heldItem, MultiPartSlots.DRILL_BIT).getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).orElse(null);

		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getLevel(), pos, tileEntity, player, heldItem);

		// If we have attributes.
		if (drillBitAttributes != null) {
			// Check for the grinder attribute. If we do, we add the grindable items to the
			// list if grindable.
			if (drillBitAttributes.hasAttribute(GrindingAttributeDefenition.ID)) {
				// Get the grinding attribute and check if its enabled.
				GrindingAttributeDefenition grindingAttribute = (GrindingAttributeDefenition) drillBitAttributes.getAttribute(GrindingAttributeDefenition.ID);
				handleGrindingAttribute(grindingAttribute, droppableItems, state, block, pos, player, tileEntity, heldItem, experience, isCreative);
			}

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

	protected boolean handleGrindingAttribute(GrindingAttributeDefenition grindingAttribute, List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos, ServerPlayer player,
			BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was ground.
		boolean wasAnythingGround = false;

		// Capture the ground items.
		List<ItemStack> groundItems = new ArrayList<ItemStack>();

		// Get the grinding attribute and check if its enabled.
		if (grindingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the grinding recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(droppableStack);
				Optional<GrinderRecipe> recipe = StaticPowerRecipeRegistry.getRecipe(GrinderRecipe.RECIPE_TYPE, matchParameters);

				// If the recipe is present, create the ground droppables.
				if (recipe.isPresent()) {
					boolean didGrindingSucceed = false;
					for (ProbabilityItemStackOutput output : recipe.get().getOutputItems()) {
						if (SDMath.diceRoll(output.getOutputChance())) {
							groundItems.add(output.getItem());
							didGrindingSucceed = true;
						}
					}

					// If the grinding of this particular item succeeded, remove it from the
					// droppable list.
					if (didGrindingSucceed) {
						droppableItems.remove(i);
					}
					wasAnythingGround = true;
				}
			}
		}

		// Add the ground items to the output.
		droppableItems.addAll(groundItems);

		return wasAnythingGround;
	}

	protected boolean handleSmeltingAttribute(SmeltingAttributeDefenition smeltingAttribute, List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos, ServerPlayer player,
			BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was smelted.
		boolean wasAnythingSmelted = false;

		// If the smelting attribute is enabled.
		if (smeltingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the furnace recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(droppableStack);
				Optional<SmeltingRecipe> recipe = player.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(matchParameters.getItems()[0]), player.getLevel());

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

	@Override
	protected void onStartingBlockMining(ItemStack stack, List<BlockPos> blocksMined, Player player) {
		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack bit = getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			bit.getCapability(CapabilityAttributable.ATTRIBUTABLE_CAPABILITY).ifPresent(attributable -> {
				if (attributable.hasAttribute(FortuneAttributeDefenition.ID)) {
					FortuneAttributeDefenition fortune = (FortuneAttributeDefenition) attributable.getAttribute(FortuneAttributeDefenition.ID);
					int fLevel = fortune.getFortuneLevelWithChance();
					stack.enchant(Enchantments.BLOCK_FORTUNE, fLevel);
				}
				if (attributable.hasAttribute(SilkTouchAttributeDefenition.ID)) {
					SilkTouchAttributeDefenition silkTouch = (SilkTouchAttributeDefenition) attributable.getAttribute(SilkTouchAttributeDefenition.ID);
					if (silkTouch.getValue()) {
						stack.enchant(Enchantments.SILK_TOUCH, 1);
					}
				}
			});
		}
	}

	/**
	 * This method is only raised on the server.
	 */
	@Override
	protected void onAllBlocksMined(ItemStack stack, List<BlockPos> blocksMined, Player player) {
		// Apply damage to the drill bit.
		stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			handler.getStackInSlot(0).hurtAndBreak(blocksMined.size(), player, (entity) -> {
				entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		});

		// Update the energy usage on client and server. 1 SV per block.
		if (!player.isCreative()) {
			EnergyHandlerItemStackUtilities.drainPower(stack, blocksMined.size() * 1000, false);
		}

		// Remove the enchantments.
		removeEnchantment(stack, Enchantments.BLOCK_FORTUNE);
		removeEnchantment(stack, Enchantments.SILK_TOUCH);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new TextComponent(" ").append(GuiTextUtilities.formatNumberAsString(StaticPowerConfig.getTier(tier).drillSpeedMultiplier.get())).append("x ")
				.append(new TranslatableComponent("gui.staticpower.tool_speed_multiplier")).withStyle(ChatFormatting.DARK_GREEN));

		tooltip.add(new TextComponent(" "));

		long remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		long capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(GuiTextUtilities.formatEnergyToString(remainingCharge, capacity));

		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBit = this.getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBitItem = (DrillBit) drillBit.getItem();
			drillBitItem.getTooltip(drillBit, worldIn, tooltip, isShowingAdvanced);
			tooltip.add(new TranslatableComponent("gui.staticpower.mining_speed").append(" ").append(GuiTextUtilities.formatUnitRateToString(this.getEfficiency(stack))));
			AttributeUtilities.addTooltipsForAttribute(drillBit, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (isSlotPopulated(stack, MultiPartSlots.DRILL_BIT)) {
			ItemStack drillBit = this.getPartInSlot(stack, MultiPartSlots.DRILL_BIT);
			DrillBit drillBitItem = (DrillBit) drillBit.getItem();
			drillBitItem.getAdvancedTooltip(drillBit, worldIn, tooltip);
		}
	}

	/**
	 * Add the inventory capability.
	 */
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ItemStackMultiCapabilityProvider(stack, nbt).addCapability(new ItemStackCapabilityInventory("default", stack, 5))
				.addCapability(new ItemStackStaticVoltCapability("default", stack, getPowerCapacity(), getPowerCapacity(), getPowerCapacity()));
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction) || ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
	}

	@Override
	public int getHorizontalRadius(ItemStack itemstack) {
		return 1;
	}

	@Override
	public int getVerticalRadius(ItemStack itemstack) {
		return 1;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new MiningDrillItemModel(existingModel);
	}

	public class MiningDrillContainerProvider implements MenuProvider {
		public ItemStack targetItemStack;

		public MiningDrillContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
			return new ContainerMiningDrill(windowId, inventory, targetItemStack);
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}

}