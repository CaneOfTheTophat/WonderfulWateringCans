package ca.wescook.wateringcans.client;

import ca.wescook.wateringcans.ModContent;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelRegistry {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent register) {
        setModel(ModContent.STONE_WATERING_CAN);
        setModel(ModContent.IRON_WATERING_CAN);
        setModel(ModContent.GOLDEN_WATERING_CAN);
        setModel(ModContent.OBSIDIAN_WATERING_CAN);
        setModel(ModContent.CREATIVE_WATERING_CAN);
    }

    public static void setModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
