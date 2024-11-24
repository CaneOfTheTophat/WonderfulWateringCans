package ca.wescook.wateringcans.crafting;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.WateringCans;
import ca.wescook.wateringcans.items.ItemWateringCan;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		// Register NBT uniquely so JEI distinguishes between each watering can
		ISubtypeRegistry.ISubtypeInterpreter wateringCanInterpreter = new ISubtypeRegistry.ISubtypeInterpreter() {
			@Nullable
			@Override
			public String apply(ItemStack itemStack) {
				if (itemStack.getTagCompound() != null)
					return itemStack.getTagCompound().getString("material");
				return null;
			}
		};
		subtypeRegistry.registerSubtypeInterpreter(ModContent.WATERING_CAN, wateringCanInterpreter);
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		// Add description for growth solution bucket
		ItemStack growthBucket = FluidUtil.getFilledBucket(FluidRegistry.getFluidStack("growth_solution", 1000)); // Create instance of growth solution bucket
		registry.addIngredientInfo(growthBucket, VanillaTypes.ITEM,"jei.wateringcans:growth_bucket"); // Create description page for it

		// Add description for watering cans
		for (String material : WateringCans.materials) {
			ItemStack tempItem = new ItemStack(ModContent.WATERING_CAN); // Create ItemStack
			NBTTagCompound nbtCompound = ItemWateringCan.getDefaultNBT(); // Create compound from NBT defaults
			nbtCompound.setString("material", material); // Overwrite material tag
			tempItem.setTagCompound(nbtCompound); // Assign tag to ItemStack
			registry.addIngredientInfo(tempItem, VanillaTypes.ITEM, "jei.wateringcans:watering_can_" + material); // Create description page
		}
	}
}
