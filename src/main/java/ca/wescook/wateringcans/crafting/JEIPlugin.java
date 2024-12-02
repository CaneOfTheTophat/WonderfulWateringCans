package ca.wescook.wateringcans.crafting;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.configs.Config;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public void register(@Nonnull IModRegistry registry) {
		// Add description for growth solution bucket
		ItemStack growthBucket = FluidUtil.getFilledBucket(FluidRegistry.getFluidStack("growth_solution", 1000));
		if(Config.bools.get("enableGrowthSolutionRecipe"))
			registry.addIngredientInfo(growthBucket, VanillaTypes.ITEM,"jei.wateringcans:growth_bucket");

		// Add descriptions for watering cans
		if(Config.bools.get("enableStoneWateringCanRecipe"))
			registry.addIngredientInfo(new ItemStack(ModContent.STONE_WATERING_CAN), VanillaTypes.ITEM, "jei.wateringcans:stone_watering_can");
		if(Config.bools.get("enableIronWateringCanRecipe"))
			registry.addIngredientInfo(new ItemStack(ModContent.IRON_WATERING_CAN), VanillaTypes.ITEM, "jei.wateringcans:iron_watering_can");
		if(Config.bools.get("enableGoldenWateringCanRecipe"))
			registry.addIngredientInfo(new ItemStack(ModContent.GOLDEN_WATERING_CAN), VanillaTypes.ITEM, "jei.wateringcans:golden_watering_can");
		if(Config.bools.get("enableObsidianWateringCanRecipe"))
			registry.addIngredientInfo(new ItemStack(ModContent.OBSIDIAN_WATERING_CAN), VanillaTypes.ITEM, "jei.wateringcans:obsidian_watering_can");
		registry.addIngredientInfo(new ItemStack(ModContent.CREATIVE_WATERING_CAN), VanillaTypes.ITEM, "jei.wateringcans:creative_watering_can");
	}
}
