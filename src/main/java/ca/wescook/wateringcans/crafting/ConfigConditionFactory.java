package ca.wescook.wateringcans.crafting;

import ca.wescook.wateringcans.configs.Config;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class ConfigConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        String config = JsonUtils.getString(json, "config");
        return () -> Config.bools.get(config);
    }
}
