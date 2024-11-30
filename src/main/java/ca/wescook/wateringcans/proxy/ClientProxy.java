package ca.wescook.wateringcans.proxy;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.client.EventFOV;
import ca.wescook.wateringcans.items.ItemWateringCan;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(((stack, tintIndex) -> tintIndex == 2 ? ((ItemWateringCan)stack.getItem()).getColor(stack) : -1), ModContent.STONE_WATERING_CAN, ModContent.IRON_WATERING_CAN, ModContent.GOLDEN_WATERING_CAN, ModContent.OBSIDIAN_WATERING_CAN, ModContent.CREATIVE_WATERING_CAN);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

		MinecraftForge.EVENT_BUS.register(new EventFOV());
	}
}
