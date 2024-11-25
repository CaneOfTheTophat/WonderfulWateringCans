package ca.wescook.wateringcans.client;

import ca.wescook.wateringcans.items.ItemWateringCan;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent register) {
        ItemWateringCan.render();
    }
}
