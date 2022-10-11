package theking530.staticpower.items;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class StaticPowerFood extends StaticPowerItem {

	public StaticPowerFood(FoodProperties food) {
		super(new Item.Properties().food(food));
	}
}
