package com.unascribed.gumobtainium.compat.jei;

import com.unascribed.gumobtainium.Gumobtainium;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

@JEIPlugin
public class GumobtainiumJEIPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		int secs = Gumobtainium.craftingTime/20;
		String s = secs == 1 ? "" : "s";
		registry.addIngredientInfo(new ItemStack(Gumobtainium.LIME_DIAMOND), ItemStack.class,
				I18n.format("gui.jei.gumobtainium.lime_diamond", secs, s));
		registry.addIngredientInfo(new ItemStack(Gumobtainium.MAGENTA_DIAMOND), ItemStack.class,
				I18n.format("gui.jei.gumobtainium.magenta_diamond", secs, s));
		registry.addIngredientInfo(FluidUtil.getFilledBucket(new FluidStack(Gumobtainium.GELATIN_FLUID, 1000)), ItemStack.class,
				I18n.format("gui.jei.gumobtainium.gelatin", secs, s));
		registry.addIngredientInfo(new FluidStack(Gumobtainium.GELATIN_FLUID, 1000), FluidStack.class,
				I18n.format("gui.jei.gumobtainium.gelatin", secs, s));
		registry.addIngredientInfo(new ItemStack(Gumobtainium.GUMOBTAINIUM), ItemStack.class,
				I18n.format("gui.jei.gumobtainium.gumobtainium", Gumobtainium.maxGumHearts));
		registry.addIngredientInfo(new ItemStack(Gumobtainium.GUMBIUM), ItemStack.class, "gui.jei.gumobtainium.gumbium");
		registry.addIngredientInfo(new ItemStack(Gumobtainium.VINEGAR), ItemStack.class, "gui.jei.gumobtainium.vinegar");
	}
	
}

