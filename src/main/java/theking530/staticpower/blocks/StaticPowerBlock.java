package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

/**
 * Basic implmentation of a static power block.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerBlock extends Block implements IItemBlockProvider, IBlockRenderLayerProvider {
	/**
	 * Constructor for a static power block.
	 * 
	 * @param name       The registry name of this block sans namespace.
	 * @param properties The block properties to be used when defining this block.
	 */
	public StaticPowerBlock(String name, Block.Properties properties) {
		super(properties);
		setRegistryName(name);
	}

	@Override
	public BlockItem getItemBlock() {
		return new StaticPowerItemBlock(this);
	}

	/**
	 * Basic constructor for a static power block with a specific material type.
	 * 
	 * @param name     The registry name of this block sans namespace.
	 * @param material The {@link Material} this block is made of.
	 */
	public StaticPowerBlock(String name, Material material) {
		this(name, material, ToolType.PICKAXE, 1, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type.
	 * 
	 * @param name     The registry name of this block sans namespace.
	 * @param material The {@link Material} this block is made of.
	 * @param tool     The {@link ToolType} this block should be harvested by.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool) {
		this(name, material, tool, 1, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level.
	 * 
	 * @param name         The registry name of this block sans namespace.
	 * @param material     The {@link Material} this block is made of.
	 * @param tool         The {@link ToolType} this block should be harvested by.
	 * @param harvestLevel The harvest level of this block.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool, int harvestLevel) {
		this(name, material, tool, harvestLevel, 1.0f);
	}

	/**
	 * Basic constructor for a static power block with a specific material type,
	 * tool type, harvest level, and hardness/resistance.
	 * 
	 * @param name                  The registry name of this block sans namespace.
	 * @param material              The {@link Material} this block is made of.
	 * @param tool                  The {@link ToolType} this block should be
	 *                              harvested by.
	 * @param harvestLevel          The harvest level of this block.
	 * @param hardnessAndResistance The hardness and resistance of this block.
	 */
	public StaticPowerBlock(String name, Material material, ToolType tool, int harvestLevel, float hardnessAndResistance) {
		this(name, Block.Properties.create(material).harvestTool(tool).harvestLevel(harvestLevel).hardnessAndResistance(hardnessAndResistance));
	}

	/**
	 * Gets the {@link RenderLayer} for this block. The default is Solid.
	 * 
	 * @return The render layer for this block.
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getSolid();
	}

	/**
	 * Gets the basic tooltip that is displayed when hovered by the user.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}

	/**
	 * Gets the advanced tooltip that is displayed when hovered by the user and they
	 * are holding shift.
	 * 
	 * @param stack   The item stack hovered by the user.
	 * @param worldIn The world the player was in when hovering the item.
	 * @param tooltip The list of {@link ITextComponent} to add to the tooltip.
	 */
	@OnlyIn(Dist.CLIENT)
	protected void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
	}
}
