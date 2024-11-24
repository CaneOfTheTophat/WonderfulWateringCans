package ca.wescook.wateringcans.potions;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ModPotions {
	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<Potion> register)
	{
		register.getRegistry().register(new PotionInvisible(false, 0).setPotionName("usingWateringCan").setRegistryName("using_watering_can"));
		register.getRegistry().register(new PotionInvisible(true, 0).setPotionName("slowPlayer").setRegistryName("slow_player").registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "2a050f8c-07be-4e06-9d39-b6d299e0505f", -0.15D, 2));
		register.getRegistry().register(new PotionInvisible(false, 0).setPotionName("inhibitFOV").setRegistryName("inhibit_fov"));
	}
}
