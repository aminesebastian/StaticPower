package theking530.staticpower.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import theking530.staticpower.assists.Reference;

public class MultiItem extends ItemBase {
	
	protected Map<Integer, MultiItemWrapper> itemMap;
	
	public MultiItem(String name) {
		super(name);
		itemMap = new HashMap<Integer, MultiItemWrapper>();
		setHasSubtypes(true);
		registerSubItems();
	}
	protected void registerSubItems() {
		
	}
	public ItemStack createSubOreItem(int metadata, String itemName) {
		ItemStack stack = createSubItem(metadata, itemName);
		OreDictionary.registerOre(itemName, stack);
		return stack;
	}
	public ItemStack createSubOreItem(int metadata, String itemName, String oreDictName) {
		ItemStack stack = createSubItem(metadata, itemName);
		OreDictionary.registerOre(oreDictName, stack);
		return stack;
	}
	public ItemStack createSubItem(int metadata, String itemName) {
		ItemStack temp = new ItemStack(this, 1, metadata);
		itemMap.put(metadata, new MultiItemWrapper(temp, metadata, itemName));
		return temp;
	}
	
	public Map<Integer, MultiItemWrapper> getSubItemMap() {
		return itemMap;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			for(MultiItemWrapper itemWrapper : itemMap.values()) {
				items.add(itemWrapper.getItem());
			}
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getItemDamage();
		if (!itemMap.containsKey(i)) {
			return "**ERROR**";
		}
		MultiItemWrapper wrapper = itemMap.get(i);
		return Reference.MOD_ID + "." + NAME + "." + wrapper.getName();
	}
	
	public class MultiItemWrapper {
		private String unlocalizedName;
		private int metadata;
		private ItemStack item;
		
		
		public MultiItemWrapper(ItemStack item, int metadata, String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
			this.metadata = metadata;
			this.item = item;
		}
		public String getName() {
			return unlocalizedName;
		}
		public int getMetadata() {
			return metadata;
		}
		public ItemStack getItem() {
			return item;
		}
	}
}
