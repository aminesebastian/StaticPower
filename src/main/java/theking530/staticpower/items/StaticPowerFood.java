package theking530.staticpower.items;

import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class StaticPowerFood extends StaticPowerItem {

	public StaticPowerFood(String name, Food food) {
		super(name, new Item.Properties().food(food));
	}
}
