package com.unascribed.gumobtainium.item;

import com.unascribed.gumobtainium.GumData;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemVinegar extends Item {

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		EntityPlayer ep = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

		if (ep == null || !ep.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
		
		if (GumData.getGumHearts(entityLiving) > 0) {
			GumData.setGumHearts(entityLiving, GumData.getGumHearts(entityLiving)-1);
			if (GumData.getFilledGumHearts(entityLiving) > GumData.getGumHearts(entityLiving)) {
				GumData.setFilledGumHearts(entityLiving,  GumData.getFilledGumHearts(entityLiving)-1);
			}
		} else if (GumData.hasGumbium(entityLiving)) {
			entityLiving.playSound(SoundEvents.ENTITY_RABBIT_DEATH, 1.0f, 0.7f);
			GumData.setHasGumbium(entityLiving, false);
		}
		
		if (ep != null && !ep.capabilities.isCreativeMode) {
			if (stack.isEmpty()) {
				stack = new ItemStack(Items.GLASS_BOTTLE);
			} else {
				ep.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}
		
		return stack;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
}
