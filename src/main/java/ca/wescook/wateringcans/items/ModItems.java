package ca.wescook.wateringcans.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ModItems {
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> register)
	{
		register.getRegistry().register(new ItemWateringCan());
	}
}
