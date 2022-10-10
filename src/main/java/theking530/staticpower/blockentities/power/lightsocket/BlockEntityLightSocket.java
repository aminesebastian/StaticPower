package theking530.staticpower.blockentities.power.lightsocket;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.api.energy.CurrentType;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.utilities.WorldUtilities;

public class BlockEntityLightSocket extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLightSocket> TYPE = new BlockEntityTypeAllocator<BlockEntityLightSocket>(
			(allocator, pos, state) -> new BlockEntityLightSocket(allocator, pos, state), ModBlocks.LightSocket);

	public static final ModelProperty<LightSocketRenderingState> LIGHT_SOCKET_RENDERING_STATE = new ModelProperty<LightSocketRenderingState>();

	public record LightSocketRenderingState(ItemStack bulb) {
	}

	@UpdateSerialize()
	private ItemStack lightbulb;
	@UpdateSerialize()
	private boolean shouldBeOn;
	private boolean clientIsOn;

	public BlockEntityLightSocket(BlockEntityTypeAllocator<BlockEntityLightSocket> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		lightbulb = ItemStack.EMPTY;
		enableFaceInteraction();
		powerStorage.setInputCurrentTypes(CurrentType.ALTERNATING, CurrentType.DIRECT);
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {
			boolean hasEnoughPower = false;
			if (hasLightBulb() && powerStorage.getStoredPower() > 0.5) {
				hasEnoughPower = true;
				this.powerStorage.drainPower(0.5, false);
			} else {
				hasEnoughPower = false;
			}
			if (hasEnoughPower != shouldBeOn) {
				shouldBeOn = hasEnoughPower;
				addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), false);
			}
		} else {
			if (clientIsOn != shouldBeOn) {
				if (shouldBeOn) {
					clientTurnOn();
				} else {
					clientTurnOff();
				}
			}
		}
	}

	public boolean hasLightBulb() {
		return !lightbulb.isEmpty();
	}

	public boolean addLightBulb(ItemStack bulb) {
		if (!hasLightBulb()) {
			ItemStack bulbCopy = bulb.copy();
			bulbCopy.setCount(1);
			lightbulb = bulbCopy;
			addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
			addRenderingUpdateRequest();
			return true;
		}
		return false;
	}

	public ItemStack getLightbulb() {
		return lightbulb;
	}

	public ItemStack removeLightbulb() {
		if (hasLightBulb()) {
			ItemStack output = lightbulb.copy();
			lightbulb = ItemStack.EMPTY;
			addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), true);
			addRenderingUpdateRequest();
			return output;
		}
		return ItemStack.EMPTY;
	}

	public boolean isLit() {
		return shouldBeOn;
	}

	protected void clientTurnOn() {
		clientIsOn = true;
		getLevel().getChunkSource().getLightEngine().checkBlock(getBlockPos());
	}

	protected void clientTurnOff() {
		clientIsOn = false;
		getLevel().getChunkSource().getLightEngine().checkBlock(getBlockPos());
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}

	@Override
	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onBlockBroken(state, newState, isMoving);
		if (hasLightBulb() && !getLevel().isClientSide()) {
			WorldUtilities.dropItem(getLevel(), getBlockPos().offset(0.5, 0.5, 0.5), lightbulb);
		}
	}

	@Nonnull
	@Override
	public ModelData getModelData() {
		return ModelData.builder().with(LIGHT_SOCKET_RENDERING_STATE, new LightSocketRenderingState(lightbulb)).build();
	}
}
