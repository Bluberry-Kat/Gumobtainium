package com.unascribed.gumobtainium.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityItemSoaking extends EntityItem {

	public EntityItemSoaking(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	public EntityItemSoaking(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityItemSoaking(World worldIn) {
		super(worldIn);
	}
	
}
