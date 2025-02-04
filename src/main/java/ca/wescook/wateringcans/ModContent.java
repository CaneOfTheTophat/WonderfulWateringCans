package ca.wescook.wateringcans;

import ca.wescook.wateringcans.items.ItemWateringCan;
import ca.wescook.wateringcans.potions.PotionInvisible;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.*;

@ObjectHolder(WateringCans.MODID)
@Mod.EventBusSubscriber
public class ModContent {

    public static final Item STONE_WATERING_CAN = null;
    public static final Item IRON_WATERING_CAN = null;
    public static final Item GOLDEN_WATERING_CAN = null;
    public static final Item OBSIDIAN_WATERING_CAN = null;
    public static final Item CREATIVE_WATERING_CAN = null;

    public static final Potion SLOW_PLAYER = null;
    public static final Potion INHIBIT_FOV = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> register) {
        register.getRegistry().register(new BlockFluidClassic(WateringCans.growthSolution, Material.WATER).setRegistryName("growth_solution_block").setUnlocalizedName(new ResourceLocation(WateringCans.MODID, "growth_solution_block").toString()));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> register)
    {
        register.getRegistry().register(new ItemWateringCan("stone", 1F, 3));
        register.getRegistry().register(new ItemWateringCan("iron", 1F, 3));
        register.getRegistry().register(new ItemWateringCan("golden", 2.5F, 3));
        register.getRegistry().register(new ItemWateringCan("obsidian", 1F, 5));
        register.getRegistry().register(new ItemWateringCan("creative", 30F, 15));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> register) {
        register.getRegistry().register(new PotionInvisible(true, 0).setPotionName("slowPlayer").setRegistryName("slow_player").registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "2a050f8c-07be-4e06-9d39-b6d299e0505f", -0.15D, 2));
        register.getRegistry().register(new PotionInvisible(false, 0).setPotionName("inhibitFOV").setRegistryName("inhibit_fov"));
    }
}

