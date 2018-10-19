package com.unascribed.gumobtainium.item;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class ItemGelatinNugget extends ItemFood {

	public ItemGelatinNugget() {
		super(1, 4f, false);
		setAlwaysEdible();
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 8;
	}
	
}
