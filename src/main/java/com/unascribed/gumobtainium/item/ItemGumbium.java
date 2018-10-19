package com.unascribed.gumobtainium.item;

import java.util.List;

import javax.annotation.Nullable;

import com.unascribed.gumobtainium.GumData;
import com.unascribed.gumobtainium.Gumobtainium;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGumbium extends ItemFood {

	public ItemGumbium() {
		super(10, 0, false);
		setAlwaysEdible();
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		ItemStack is = super.onItemUseFinish(stack, worldIn, entityLiving);
		entityLiving.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0f, 0.5f);
		entityLiving.playSound(SoundEvents.ENTITY_RABBIT_HURT, 1.0f, 0.7f);
		GumData.setHasGumbium(entityLiving, true);
		return is;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (GumData.hasGumbium(playerIn)) {
			playerIn.playSound(SoundEvents.ENTITY_SLIME_DEATH, 1.0f, 1.5f);
			playerIn.playSound(SoundEvents.ENTITY_RABBIT_HURT, 1.0f, 1.5f);
			if (!worldIn.isRemote) {
				ItemStack stack = playerIn.getHeldItem(handIn);
				playerIn.dropItem(stack, true);
				playerIn.sendStatusMessage(new TextComponentTranslation("msg."+Gumobtainium.MODID+".alreadyHaveGumbium"), true);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, ItemStack.EMPTY);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int i = 0;
		while (I18n.hasKey(getTranslationKey()+".tooltip."+i)) {
			tooltip.add("§5§o"+I18n.format(getTranslationKey()+".tooltip."+i));
			i++;
		}
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return "§a"+super.getItemStackDisplayName(stack);
	}
	
}
