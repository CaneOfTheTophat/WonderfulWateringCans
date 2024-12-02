package ca.wescook.wateringcans.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

@SuppressWarnings("unused")
public class ConsumeEverythingRecipe extends ShapelessRecipes {

    public ConsumeEverythingRecipe(String group, ItemStack output, NonNullList<Ingredient> ingredients) {
        super(group, output, ingredients);
    }

    // Remove everything in the slots, leave nothing behind
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < nonnulllist.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, ItemStack.EMPTY);
        }

        return nonnulllist;
    }

    public static class RecipeFactory implements IRecipeFactory {
        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            String group = JsonUtils.getString(json, "group", "");
            NonNullList<Ingredient> ingredients = NonNullList.create();

            for (JsonElement elements : JsonUtils.getJsonArray(json, "ingredients"))
                ingredients.add(CraftingHelper.getIngredient(elements, context));

            if (ingredients.isEmpty())
                throw new JsonParseException("No ingredients for shapeless recipe");

            ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

            return new ConsumeEverythingRecipe(group, result, ingredients);
        }
    }
}


