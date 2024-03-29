package theking530.staticpower.items.tools.chainsaw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import theking530.api.attributes.AttributeInstance;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.api.energy.item.ItemStackStaticPowerEnergyCapability;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.api.item.compound.capability.CompoundItemHandlerCapabilityProvider.CompoundItemHandlerCapabilityBuilder;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticcore.item.ItemStackCapabilityInventory;
import theking530.staticcore.item.ItemStackMultiCapabilityProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.ChainsawItemModel;
import theking530.staticpower.init.ModAttributes;
import theking530.staticpower.init.ModItemSlots;
import theking530.staticpower.items.tools.AbstractMultiHarvestTool;
import theking530.staticpower.items.tools.miningdrill.DrillBit;

public class Chainsaw extends AbstractMultiHarvestTool implements ICustomModelProvider {
	private static final int CHAINSAW_BLADE_SLOT = 0;
	private static final int MAX_RECURSION = 100;
	public final ResourceLocation tier;

	public Chainsaw(float attackDamageIn, float attackSpeedIn, ResourceLocation tier) {
		super(new Item.Properties().setNoRepair(), attackDamageIn, attackSpeedIn,
				Arrays.asList(BlockTags.MINEABLE_WITH_AXE));
		this.tier = tier;
	}

	@Override
	protected void addSlots(CompoundItemHandlerCapabilityBuilder builder, ItemStack stack) {
		builder.addSlot(ModItemSlots.CHAINSAW_BLADE.get(), false);
	}

	public double getCapacity() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryCapacity.get() * 2;
	}

	public StaticVoltageRange getInputVoltageRange() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.getPortableBatteryChargingVoltage();
	}

	public double getMaximumInputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerInput.get();
	}

	public StaticPowerVoltage getOutputVoltage() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryOutputVoltage.get();
	}

	public double getMaximumOutputPower() {
		return StaticCoreConfig.getTier(tier).powerConfiguration.portableBatteryMaximumPowerOutput.get();
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		// Only show the animation if the stored power is the same (didn't change).
		// This is so we don't SPAM the animation on charge or discharge.
		return super.shouldCauseBlockBreakReset(oldStack, newStack) && EnergyHandlerItemStackUtilities
				.getStoredPower(newStack) == EnergyHandlerItemStackUtilities.getStoredPower(oldStack);
	}

	@Override
	public Tier getMiningTier(ItemStack stack) {
		ItemStack bitStack = getCompoundItemCapability(stack).getPartInSlot(CHAINSAW_BLADE_SLOT);
		if (bitStack.getItem() instanceof DrillBit) {
			DrillBit bit = (DrillBit) bitStack.getItem();
			return bit.getMiningTier(bitStack);
		}
		return Tiers.WOOD;
	}

	public ItemStack getFilledVariant() {
		ItemStack output = new ItemStack(this, 1);
		EnergyHandlerItemStackUtilities.getEnergyContainer(output).ifPresent((cap) -> {
			cap.setStoredPower(StaticPowerEnergyUtilities.getMaximumPower());
		});
		return output;
	}

	@Override
	public boolean isReadyToMine(ItemStack itemstack) {
		return getCompoundItemCapability(itemstack).isComplete()
				&& EnergyHandlerItemStackUtilities.getStoredPower(itemstack) > 0;
	}

	@Override
	protected float getEfficiency(ItemStack itemstack) {
		AtomicReference<Float> efficiency = new AtomicReference<Float>(5.0f);

		if (getCompoundItemCapability(itemstack).isSlotPopulated(CHAINSAW_BLADE_SLOT)) {
			ItemStack blade = getCompoundItemCapability(itemstack).getPartInSlot(CHAINSAW_BLADE_SLOT);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			efficiency.set(bladeItem.getMiningTier(blade).getSpeed() * 0.5f);
			blade.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).ifPresent(attributable -> {
				if (attributable.hasAttribute(ModAttributes.Haste.get())) {
					AttributeInstance<Number> hasteDefenition = attributable.getAttribute(ModAttributes.Haste.get());
					efficiency.set(
							efficiency.get() * (((hasteDefenition.getValue().floatValue() * 10.0f) / 300.0f) + 1.0f));
				}
			});
		}
		return efficiency.get() * StaticCoreConfig.getTier(tier).toolConfiguration.chainsawSpeedMultiplier.get();
	}

	/**
	 * When right clicked, open the UI.
	 */
	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player,
			InteractionHand hand, ItemStack item) {
		if (!world.isClientSide && player.isShiftKeyDown()) {
			NetworkGUI.openScreen((ServerPlayer) player, new ChainsawContainerProvider(item), buff -> {
				buff.writeInt(player.getInventory().selected);
			});
			return InteractionResultHolder.success(item);
		}
		return InteractionResultHolder.pass(item);
	}

	protected void harvestBlockDrops(BlockState state, Block block, BlockPos pos, ServerPlayer player,
			BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {
		// If the player is in creative, do nothing.
		if (isCreative) {
			return;
		}

		// Allocate a list of the items that would be dropped.
		List<ItemStack> droppableItems = Block.getDrops(state, player.getLevel(), pos, tileEntity, player, heldItem);

		// Get the drill bit attributes.
		IAttributable drillBitAttributes = getCompoundItemCapability(heldItem).getPartInSlot(CHAINSAW_BLADE_SLOT)
				.getCapability(CapabilityAttributable.CAPABILITY_ATTRIBUTABLE).orElse(null);

		// If we have attributes.
		if (drillBitAttributes != null) {
			// Check for the smelting attribute. If we do, handle it.
			if (drillBitAttributes.hasAttribute(ModAttributes.Smelting.get())) {
				// Get the smelting attribute.
				AttributeInstance<Boolean> smeltingAttribute = drillBitAttributes
						.getAttribute(ModAttributes.Smelting.get());
				handleSmeltingAttribute(smeltingAttribute, droppableItems, state, block, pos, player, tileEntity,
						heldItem, experience, isCreative);
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
		state.spawnAfterBreak((ServerLevel) player.getCommandSenderWorld(), pos, heldItem, true);
	}

	protected boolean handleSmeltingAttribute(AttributeInstance<Boolean> smeltingAttribute,
			List<ItemStack> droppableItems, BlockState state, Block block, BlockPos pos, ServerPlayer player,
			BlockEntity tileEntity, ItemStack heldItem, int experience, boolean isCreative) {

		// Allocate a flag to check if anything was smelted.
		boolean wasAnythingSmelted = false;

		// If the smelting attribute is enabled.
		if (smeltingAttribute.getValue()) {
			// Iterate through all the items that were going to be dropped.
			for (int i = droppableItems.size() - 1; i >= 0; i--) {
				// Get the droppable stack and get the furnace recipe for it if it exists.
				ItemStack droppableStack = droppableItems.get(i);
				ITeam playerTeam = TeamManager.get(player.getLevel()).getTeamForPlayer(player);
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(
						playerTeam != null ? playerTeam.getId() : null, droppableStack);
				Optional<SmeltingRecipe> recipe = player.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING,
						new SimpleContainer(matchParameters.getItems()[0]), player.getLevel());

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
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			handler.getStackInSlot(0).hurtAndBreak(blocksMined.size(), player, (entity) -> {
				entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		});

		// Update the energy usage on client and server. 1SV per block.
		if (!player.isCreative()) {
			EnergyHandlerItemStackUtilities.drainPower(stack,
					blocksMined.size() * StaticPowerConfig.SERVER.chainsawPowerUsePerBlock.get(), false);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			boolean isShowingAdvanced) {
		tooltip.add(Component.literal(" ")
				.append(GuiTextUtilities.formatNumberAsString(
						StaticCoreConfig.getTier(tier).toolConfiguration.chainsawSpeedMultiplier.get()))
				.append("x ").append(Component.translatable("gui.staticpower.tool_speed_multiplier"))
				.withStyle(ChatFormatting.DARK_GREEN));

		tooltip.add(Component.literal(" "));

		double remainingCharge = EnergyHandlerItemStackUtilities.getStoredPower(stack);
		double capacity = EnergyHandlerItemStackUtilities.getCapacity(stack);
		tooltip.add(PowerTextFormatting.formatPowerToString(remainingCharge, capacity));

		if (getCompoundItemCapability(stack).isSlotPopulated(CHAINSAW_BLADE_SLOT)) {
			ItemStack blade = getCompoundItemCapability(stack).getPartInSlot(CHAINSAW_BLADE_SLOT);
			ChainsawBlade bladeItem = (ChainsawBlade) blade.getItem();
			bladeItem.getTooltip(blade, worldIn, tooltip, isShowingAdvanced);
			tooltip.add(Component.translatable("gui.staticcore.mining_speed").append(" ")
					.append(String.valueOf(getEfficiency(stack))));
			AttributeUtilities.addTooltipsForAttribute(blade, tooltip, isShowingAdvanced);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (getCompoundItemCapability(stack).isSlotPopulated(CHAINSAW_BLADE_SLOT)) {
			ItemStack blade = getCompoundItemCapability(stack).getPartInSlot(CHAINSAW_BLADE_SLOT);
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
		if (StaticPowerConfig.SERVER_SPEC.isLoaded()) {
			return new ItemStackMultiCapabilityProvider(stack, nbt)
					.addCapability(new ItemStackCapabilityInventory("default", stack, 5))
					.addCapability(new ItemStackStaticPowerEnergyCapability("default", stack, getCapacity(),
							getInputVoltageRange(), getMaximumInputPower(), getOutputVoltage(), getMaximumOutputPower(),
							true, false))
					.addCapability(createCompoundItemCapability(stack, nbt));
		}
		return null;
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

	private void recursiveTreeAnalyzer(ItemStack itemstack, BlockPos pos, Player player, List<BlockPos> positions,
			Set<BlockPos> visited, int currentDepth) {
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
		if (!state.isAir()) {
			// If not air, check to see if it is wood. If it is, and its harvestable,
			// harvest it.
			if (state.is(BlockTags.LOGS)) {
				if (isCorrectToolForDrops(itemstack, state)) {
					positions.add(pos);

					// Recurse.
					for (Direction dir : Direction.values()) {
						recursiveTreeAnalyzer(itemstack, pos.relative(dir), player, positions, visited,
								currentDepth + 1);
					}
				}
			}
		}
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel,
			ModelEvent.BakingCompleted event) {
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