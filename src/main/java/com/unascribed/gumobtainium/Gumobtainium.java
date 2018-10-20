package com.unascribed.gumobtainium;

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.elytradev.concrete.network.NetworkContext;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.unascribed.gumobtainium.block.BlockFluidGelatin;
import com.unascribed.gumobtainium.item.ItemGelatin;
import com.unascribed.gumobtainium.item.ItemGelatinNugget;
import com.unascribed.gumobtainium.item.ItemGumbium;
import com.unascribed.gumobtainium.item.ItemGumobtanium;
import com.unascribed.gumobtainium.item.ItemVinegar;
import com.unascribed.gumobtainium.network.MessageHUDUpdate;
import com.unascribed.gumobtainium.network.MessageItemSoaking;
import com.unascribed.gumobtainium.proxy.Proxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid=Gumobtainium.MODID, name=Gumobtainium.NAME, version=Gumobtainium.VERSION)
public class Gumobtainium {
	public static final String MODID = "gumobtainium";
	public static final String NAME = "Gumobtainium";
	public static final String VERSION = "@VERSION@";
	
	public static final Logger log = LogManager.getLogger(NAME);
	
	@SidedProxy(clientSide="com.unascribed.gumobtainium.proxy.ClientProxy", serverSide="com.unascribed.gumobtainium.proxy.ServerProxy")
	public static Proxy proxy;
	
	public static NetworkContext NETWORK;
	
	public static Item LIME_DIAMOND;
	public static Item MAGENTA_DIAMOND;
	public static Item SOLIDIFIED_GELATIN_BUCKET;
	public static ItemGelatin GELATIN;
	public static ItemGelatinNugget GELATIN_NUGGET;
	public static ItemGumbium GUMBIUM;
	public static ItemGumobtanium GUMOBTANIUM;
	public static ItemVinegar VINEGAR;
	
	public static Fluid GELATIN_FLUID;
	public static BlockFluidGelatin GELATIN_FLUID_BLOCK;
	
	public static int craftingTime = 1200;
	public static int maxGumHearts = 5;
	
	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MODID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(GUMOBTANIUM);
		}
	};
	
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
		cfg.load();
		craftingTime = cfg.getInt("soakTime", "General", 1200, 1, 65536, "How many ticks items crafted by soaking in water must soak in water before they finish crafting. Boy that sounds redundant.");
		maxGumHearts = cfg.getInt("maxGumHearts", "General", 5, 1, 10, "The maximum numbers of gum hearts a player can have at one time.");
		cfg.save();
		MinecraftForge.EVENT_BUS.register(this);
		
		NETWORK = NetworkContext.forChannel(MODID);
		NETWORK.register(MessageItemSoaking.class);
		NETWORK.register(MessageHUDUpdate.class);
		
		GELATIN_FLUID = new Fluid(MODID+".gelatin",
				new ResourceLocation(MODID, "blocks/gelatin_still"),
				new ResourceLocation(MODID, "blocks/gelatin_flow"));
		GELATIN_FLUID.setViscosity(1250);
		FluidRegistry.registerFluid(GELATIN_FLUID);
		FluidRegistry.addBucketForFluid(GELATIN_FLUID);
		
		OreDictionary.registerOre("listAllMeatRaw", Items.BEEF);
		OreDictionary.registerOre("listAllMeatRaw", Items.PORKCHOP);
		OreDictionary.registerOre("listAllMeatRaw", Items.MUTTON);
		OreDictionary.registerOre("listAllMeatRaw", Items.CHICKEN);
		OreDictionary.registerOre("listAllMeatRaw", Items.RABBIT);
		
		proxy.preInit();
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e) {
		if (e.phase == Phase.START) {
			Multimap<BlockPos, EntityItem> seenMeatEntities = ArrayListMultimap.create();
			Multimap<BlockPos, EntityItem> seenDyeOrDiamondEntities = ArrayListMultimap.create();
			for (EntityItem ei : e.world.getEntities(EntityItem.class, Predicates.alwaysTrue())) {
				ItemStack is = ei.getItem();
				if (is.isEmpty()) continue;
				int[] oreIds = OreDictionary.getOreIDs(is);
				for (int ore : oreIds) {
					if ("listAllMeatRaw".equals(OreDictionary.getOreName(ore))) {
						BlockPos pos = ei.getPosition();
						IBlockState ibs = ei.world.getBlockState(pos).getActualState(ei.world, pos);
						if (ibs.getBlock() == Blocks.WATER && ibs.getValue(BlockFluidClassic.LEVEL) == 0) {
							seenMeatEntities.put(pos, ei);
						} else if (ei.getEntityData().hasKey(MODID+":ticksInWater")) {
							new MessageItemSoaking(ei, 0).sendToAllWatching(ei);
							ei.getEntityData().removeTag(MODID+":ticksInWater");
						}
						break;
					} else if ("gemDiamond".equals(OreDictionary.getOreName(ore))
							|| "dyeMagenta".equals(OreDictionary.getOreName(ore))
							|| "dyeLime".equals(OreDictionary.getOreName(ore))) {
						BlockPos pos = ei.getPosition();
						IBlockState ibs = ei.world.getBlockState(pos).getActualState(ei.world, pos);
						if (ibs.getBlock() == Blocks.WATER && ibs.getValue(BlockFluidClassic.LEVEL) == 0) {
							seenDyeOrDiamondEntities.put(pos, ei);
						} else if (ei.getEntityData().hasKey(MODID+":ticksInWater")) {
							new MessageItemSoaking(ei, 0).sendToAllWatching(ei);
							ei.getEntityData().removeTag(MODID+":ticksInWater");
						}
						break;
					}
				}
			}
			for (Map.Entry<BlockPos, Collection<EntityItem>> en : seenMeatEntities.asMap().entrySet()) {
				int count = 0;
				for (EntityItem ei : en.getValue()) {
					count += ei.getItem().getCount();
				}
				if (count == 4) {
					boolean allTickedEnough = tickItems(en.getValue());
					if (allTickedEnough) {
						en.getValue().forEach(EntityItem::setDead);
						e.world.setBlockState(en.getKey(), GELATIN_FLUID_BLOCK.getDefaultState());
						e.world.playSound(null, en.getKey().getX()+0.5, en.getKey().getY()+0.5, en.getKey().getZ()+0.5, 
								SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.MASTER, 1, 1);
					}
				}
			}
			for (Map.Entry<BlockPos, Collection<EntityItem>> en : seenDyeOrDiamondEntities.asMap().entrySet()) {
				int dyeMagenta = 0;
				int dyeLime = 0;
				int diamond = 0;
				for (EntityItem ei : en.getValue()) {
					ItemStack is = ei.getItem();
					if (is.isEmpty()) continue;
					int[] oreIds = OreDictionary.getOreIDs(is);
					for (int ore : oreIds) {
						if ("gemDiamond".equals(OreDictionary.getOreName(ore))) {
							diamond += is.getCount();
							break;
						} else if ("dyeMagenta".equals(OreDictionary.getOreName(ore))) {
							dyeMagenta += is.getCount();
							break;
						} else if ("dyeLime".equals(OreDictionary.getOreName(ore))) {
							dyeLime += is.getCount();
							break;
						}
					}
				}
				if (diamond == 1) {
					if (dyeMagenta == 0 && dyeLime == 4) {
						if (tickItems(en.getValue())) {
							spawnResult(e.world, diamond, en.getValue(), LIME_DIAMOND);
							e.world.setBlockToAir(en.getKey());
							e.world.playSound(null, en.getKey().getX()+0.5, en.getKey().getY()+0.5, en.getKey().getZ()+0.5, 
									SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.MASTER, 1, 1);
						}
					} else if (dyeLime == 0 && dyeMagenta == 4) {
						if (tickItems(en.getValue())) {
							spawnResult(e.world, diamond, en.getValue(), MAGENTA_DIAMOND);
							e.world.setBlockToAir(en.getKey());
							e.world.playSound(null, en.getKey().getX()+0.5, en.getKey().getY()+0.5, en.getKey().getZ()+0.5, 
									SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.MASTER, 1, 1);
						}
					}
				}
			}
		}
	}
	
	private void spawnResult(World world, int amount, Collection<EntityItem> items, Item result) {
		double posX = 0;
		double posY = 0;
		double posZ = 0;
		for (EntityItem ei : items) {
			posX = ei.posX;
			posY = ei.posY;
			posZ = ei.posZ;
			ei.setDead();
		}
		world.spawnEntity(new EntityItem(world, posX, posY, posZ, new ItemStack(result, amount)));
	}

	private boolean tickItems(Collection<EntityItem> items) {
		boolean rtrn = true;
		for (EntityItem ei : items) {
			int ticks = ei.getEntityData().getInteger(MODID+":ticksInWater");
			if (ticks % 20 == 0) {
				new MessageItemSoaking(ei, ticks).sendToAllWatching(ei);
			}
			if (ei.world instanceof WorldServer) {
				((WorldServer)ei.world).spawnParticle(EnumParticleTypes.WATER_BUBBLE, ei.posX, ei.posY, ei.posZ, 2, 0.2f, 0.2f, 0.2f, 0);
			}
			if (ticks < craftingTime) {
				ticks++;
				ei.getEntityData().setInteger(MODID+":ticksInWater", ticks);
				rtrn = false;
			}
		}
		return rtrn;
	}

	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent e) {
		GumData.update(e.player);
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onAttack(LivingHurtEvent e) {
		GumData.setTicksSinceLastDamage(e.getEntity(), 0);
		if (e.getSource().isDamageAbsolute()) return;
		if (GumData.getFilledGumHearts(e.getEntity()) > 0) {
			GumData.setFilledGumHearts(e.getEntity(), GumData.getFilledGumHearts(e.getEntity())-1);
			e.getEntity().world.playSound(null, e.getEntity().posX, e.getEntity().posY, e.getEntity().posZ, SoundEvents.ENTITY_SLIME_HURT, SoundCategory.PLAYERS, 1.0f, 1.5f);
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent e) {
		if (e.player.world.isRemote) return;
		if (GumData.hasGumbium(e.player)) {
			if (e.player.getFoodStats().getFoodLevel() < 10) {
				e.player.getFoodStats().setFoodLevel(10);
			}
		}
		int hearts = GumData.getGumHearts(e.player);
		if (hearts > 0) {
			int filled = GumData.getFilledGumHearts(e.player);
			int ticks = GumData.getTicksSinceLastDamage(e.player);
			if (filled < hearts && ticks > 160) {
				ticks = 120;
				e.player.world.playSound(null, e.player.posX, e.player.posY, e.player.posZ, SoundEvents.ENTITY_SLIME_HURT, SoundCategory.PLAYERS, 1.0f, 0.75f);
				GumData.setFilledGumHearts(e.player, filled + 1);
			} else {
				ticks++;
			}
			GumData.setTicksSinceLastDamage(e.player, ticks);
		}
	}
	
	@SubscribeEvent
	public void onWatch(ChunkWatchEvent e) {
		for (ClassInheritanceMultiMap<Entity> cimm : e.getChunkInstance().getEntityLists()) {
			for (EntityItem ei : cimm.getByClass(EntityItem.class)) {
				if (ei.getEntityData().hasKey(MODID+":ticksInWater")) {
					new MessageItemSoaking(ei, ei.getEntityData().getInteger(MODID+":ticksInWater")).sendTo(e.getPlayer());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRegisterBlock(RegistryEvent.Register<Block> e) {
		GELATIN_FLUID_BLOCK = new BlockFluidGelatin(GELATIN_FLUID, Material.WATER);
		GELATIN_FLUID.setBlock(GELATIN_FLUID_BLOCK);
		GELATIN_FLUID_BLOCK.setRegistryName("gelatin_fluid_block");
		e.getRegistry().register(GELATIN_FLUID_BLOCK);
	}
	
	@SubscribeEvent
	public void onRegisterItems(RegistryEvent.Register<Item> e) {
		e.getRegistry().register(LIME_DIAMOND = new Item()
				.setRegistryName("lime_diamond").setTranslationKey(MODID+".lime_diamond")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(MAGENTA_DIAMOND = new Item()
				.setRegistryName("magenta_diamond").setTranslationKey(MODID+".magenta_diamond")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(SOLIDIFIED_GELATIN_BUCKET = new Item()
				.setRegistryName("solidified_gelatin_bucket").setTranslationKey(MODID+".solidified_gelatin_bucket")
				.setContainerItem(Items.BUCKET).setMaxStackSize(1)
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(GELATIN = (ItemGelatin)new ItemGelatin()
				.setRegistryName("gelatin").setTranslationKey(MODID+".gelatin")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(GELATIN_NUGGET = (ItemGelatinNugget)new ItemGelatinNugget()
				.setRegistryName("gelatin_nugget").setTranslationKey(MODID+".gelatin_nugget")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(GUMBIUM = (ItemGumbium)new ItemGumbium()
				.setRegistryName("gumbium").setTranslationKey(MODID+".gumbium")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(GUMOBTANIUM = (ItemGumobtanium)new ItemGumobtanium()
				.setRegistryName("gumobtainium").setTranslationKey(MODID+".gumobtainium")
				.setCreativeTab(CREATIVE_TAB));
		e.getRegistry().register(VINEGAR = (ItemVinegar)new ItemVinegar()
				.setRegistryName("vinegar").setTranslationKey(MODID+".vinegar")
				.setMaxStackSize(1)
				.setCreativeTab(CREATIVE_TAB));
	}
	
	@SubscribeEvent
	public void onRegisterRecipes(RegistryEvent.Register<IRecipe> e) {
		FurnaceRecipes.instance().addSmeltingRecipe(FluidUtil.getFilledBucket(new FluidStack(GELATIN_FLUID, 1)), new ItemStack(SOLIDIFIED_GELATIN_BUCKET), 0.2f);
		e.getRegistry().register(new ShapelessOreRecipe(null, GELATIN,
				GELATIN_NUGGET, GELATIN_NUGGET, GELATIN_NUGGET,
				GELATIN_NUGGET, GELATIN_NUGGET, GELATIN_NUGGET,
				GELATIN_NUGGET, GELATIN_NUGGET, GELATIN_NUGGET).setRegistryName("gelatin_nuggets_to_gelatin"));
		e.getRegistry().register(new ShapelessOreRecipe(null, new ItemStack(GELATIN_NUGGET, 9),
				GELATIN).setRegistryName("gelatin_to_gelatin_nuggets"));
		e.getRegistry().register(new ShapelessOreRecipe(null, new ItemStack(GELATIN_NUGGET, 3),
				SOLIDIFIED_GELATIN_BUCKET).setRegistryName("solidified_gelatin_bucket_to_gelatin_nuggets"));
		e.getRegistry().register(new ShapedOreRecipe(null, GUMBIUM,
				" g ",
				"gdg",
				" g ",
				'g', GELATIN,
				'd', LIME_DIAMOND).setRegistryName("gumbium"));
		e.getRegistry().register(new ShapedOreRecipe(null, GUMOBTANIUM,
				" g ",
				"gdg",
				" g ",
				'g', GELATIN,
				'd', MAGENTA_DIAMOND).setRegistryName("gumobtainum"));
		e.getRegistry().register(new ShapelessOreRecipe(null, VINEGAR,
				PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), Items.SUGAR, Items.APPLE).setRegistryName("vinegar"));
	}
	
}
