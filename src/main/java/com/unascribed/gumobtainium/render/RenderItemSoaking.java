package com.unascribed.gumobtainium.render;

import com.unascribed.gumobtainium.Gumobtainium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;

public class RenderItemSoaking extends RenderEntityItem {

	public RenderItemSoaking(RenderManager rm) {
		super(rm, Minecraft.getMinecraft().getRenderItem());
	}

	private void setupStipple(Entity entity) {
		int ticks = entity.getEntityData().getInteger(Gumobtainium.MODID+":ticksInWater");
		if (ticks >= 1140) {
			Stipple.grey5();
		} else if (ticks >= 1080) {
			Stipple.grey10();
		} else if (ticks > 1020) {
			Stipple.grey15();
		} else if (ticks >= 960) {
			Stipple.grey20();
		} else if (ticks >= 900) {
			Stipple.grey25();
		} else if (ticks >= 840) {
			Stipple.grey30();
		} else if (ticks >= 780) {
			Stipple.grey35();
		} else if (ticks >= 720) {
			Stipple.grey40();
		} else if (ticks >= 660) {
			Stipple.grey45();
		} else if (ticks >= 600) {
			Stipple.grey50();
		} else if (ticks >= 540) {
			Stipple.grey55();
		} else if (ticks >= 480) {
			Stipple.grey60();
		} else if (ticks >= 420) {
			Stipple.grey65();
		} else if (ticks >= 360) {
			Stipple.grey70();
		} else if (ticks >= 300) {
			Stipple.grey75();
		} else if (ticks >= 240) {
			Stipple.grey80();
		} else if (ticks >= 180) {
			Stipple.grey85();
		} else if (ticks >= 120) {
			Stipple.grey90();
		} else if (ticks >= 60) {
			Stipple.grey95();
		}
		if (ticks >= 60) {
			Stipple.enable();
		}
	}
	
	@Override
	public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
		setupStipple(entity);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		Stipple.disable();
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
		setupStipple(entityIn);
		super.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
		Stipple.disable();
	}
	
}
