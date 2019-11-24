package com.unascribed.gumobtainium.proxy;

import java.util.Random;

import com.unascribed.gumobtainium.GumData;
import com.unascribed.gumobtainium.Gumobtainium;
import com.unascribed.gumobtainium.entity.EntityItemSoaking;
import com.unascribed.gumobtainium.render.RenderItemSoaking;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy implements Proxy {
	private static final ResourceLocation GUI = new ResourceLocation(Gumobtainium.MODID, "textures/gui/gui.png");
	
	private Random rand = new Random();
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(this);
		RenderingRegistry.registerEntityRenderingHandler(EntityItemSoaking.class, RenderItemSoaking::new);
	}
	
	@SubscribeEvent
	public void onModelRegister(ModelRegistryEvent e) {
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.LIME_DIAMOND, 0, new ModelResourceLocation(Gumobtainium.MODID+":lime_diamond#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.MAGENTA_DIAMOND, 0, new ModelResourceLocation(Gumobtainium.MODID+":magenta_diamond#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.SOLIDIFIED_GELATIN_BUCKET, 0, new ModelResourceLocation(Gumobtainium.MODID+":solidified_gelatin_bucket#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.GELATIN, 0, new ModelResourceLocation(Gumobtainium.MODID+":gelatin#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.GELATIN_NUGGET, 0, new ModelResourceLocation(Gumobtainium.MODID+":gelatin_nugget#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.GUMBIUM, 0, new ModelResourceLocation(Gumobtainium.MODID+":gumbium#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.GUMOBTAINIUM, 0, new ModelResourceLocation(Gumobtainium.MODID+":gumobtainium#inventory"));
		ModelLoader.setCustomModelResourceLocation(Gumobtainium.VINEGAR, 0, new ModelResourceLocation(Gumobtainium.MODID+":vinegar#inventory"));
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void onRenderOverlayPre(RenderGameOverlayEvent.Pre e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (e.getType() == ElementType.HEALTH) {
			if (GumData.getGumHearts(mc.player) > 0) {
				mc.renderEngine.bindTexture(GUI);
				int containers = GumData.getGumHearts(mc.player);
				int filled = GumData.getFilledGumHearts(mc.player);
				int left = e.getResolution().getScaledWidth() / 2 - 91;
				int top = e.getResolution().getScaledHeight() - GuiIngameForge.left_height;
				GuiIngameForge.left_height += 10;
				GlStateManager.enableAlpha();
				for (int i = 0; i < containers; i++) {
					Gui.drawModalRectWithCustomSizedTexture(left+(i*8), top, 0, 0, 9, 9, 68, 11);
					if (i < filled) {
						Gui.drawModalRectWithCustomSizedTexture(left+(i*8), top, 9, 0, 9, 9, 68, 11);
					}
				}
				mc.renderEngine.bindTexture(GuiIngameForge.ICONS);
			}
		}
	}

	@SubscribeEvent(priority=EventPriority.LOWEST, receiveCanceled = true)
	public void onRenderOverlayPost(RenderGameOverlayEvent.Post e) {
		Minecraft mc = Minecraft.getMinecraft();
		if (e.getType() == ElementType.FOOD) {
			if (GumData.hasGumbium(mc.player)) {
				mc.renderEngine.bindTexture(GUI);
				GlStateManager.enableAlpha();
				int right = (e.getResolution().getScaledWidth() / 2) + 91;
				int top = e.getResolution().getScaledHeight() - (GuiIngameForge.right_height - 10);
				Gui.drawModalRectWithCustomSizedTexture(right-41, top-1, 27, 0, 41, 11, 68, 11);
				rand.setSeed(mc.player.ticksExisted/5);
				if (rand.nextInt(18) != 4) {
					Gui.drawModalRectWithCustomSizedTexture(right-41, top, 18, 1, 9, 9, 68, 11);
				}
				mc.renderEngine.bindTexture(GuiIngameForge.ICONS);
			}
		}
	}
	
}
