package net.minecraft.init;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

@net.minecraftforge.registries.ObjectHolder("minecraft")
public class Items {
   public static final Item AIR;
   public static final Item IRON_SHOVEL;
   public static final Item IRON_PICKAXE;
   public static final Item IRON_AXE;
   public static final Item FLINT_AND_STEEL;
   public static final Item APPLE;
   public static final Item BOW;
   public static final Item ARROW;
   public static final Item SPECTRAL_ARROW;
   public static final Item TIPPED_ARROW;
   public static final Item COAL;
   public static final Item CHARCOAL;
   public static final Item DIAMOND;
   public static final Item IRON_INGOT;
   public static final Item GOLD_INGOT;
   public static final Item IRON_SWORD;
   public static final Item WOODEN_SWORD;
   public static final Item WOODEN_SHOVEL;
   public static final Item WOODEN_PICKAXE;
   public static final Item WOODEN_AXE;
   public static final Item STONE_SWORD;
   public static final Item STONE_SHOVEL;
   public static final Item STONE_PICKAXE;
   public static final Item STONE_AXE;
   public static final Item DIAMOND_SWORD;
   public static final Item DIAMOND_SHOVEL;
   public static final Item DIAMOND_PICKAXE;
   public static final Item DIAMOND_AXE;
   public static final Item STICK;
   public static final Item BOWL;
   public static final Item MUSHROOM_STEW;
   public static final Item GOLDEN_SWORD;
   public static final Item GOLDEN_SHOVEL;
   public static final Item GOLDEN_PICKAXE;
   public static final Item GOLDEN_AXE;
   public static final Item STRING;
   public static final Item FEATHER;
   public static final Item GUNPOWDER;
   public static final Item WOODEN_HOE;
   public static final Item STONE_HOE;
   public static final Item IRON_HOE;
   public static final Item DIAMOND_HOE;
   public static final Item GOLDEN_HOE;
   public static final Item WHEAT_SEEDS;
   public static final Item WHEAT;
   public static final Item BREAD;
   public static final Item LEATHER_HELMET;
   public static final Item LEATHER_CHESTPLATE;
   public static final Item LEATHER_LEGGINGS;
   public static final Item LEATHER_BOOTS;
   public static final Item CHAINMAIL_HELMET;
   public static final Item CHAINMAIL_CHESTPLATE;
   public static final Item CHAINMAIL_LEGGINGS;
   public static final Item CHAINMAIL_BOOTS;
   public static final Item IRON_HELMET;
   public static final Item IRON_CHESTPLATE;
   public static final Item IRON_LEGGINGS;
   public static final Item IRON_BOOTS;
   public static final Item DIAMOND_HELMET;
   public static final Item DIAMOND_CHESTPLATE;
   public static final Item DIAMOND_LEGGINGS;
   public static final Item DIAMOND_BOOTS;
   public static final Item GOLDEN_HELMET;
   public static final Item GOLDEN_CHESTPLATE;
   public static final Item GOLDEN_LEGGINGS;
   public static final Item GOLDEN_BOOTS;
   public static final Item TURTLE_HELMET;
   public static final Item FLINT;
   public static final Item PORKCHOP;
   public static final Item COOKED_PORKCHOP;
   public static final Item PAINTING;
   public static final Item GOLDEN_APPLE;
   public static final Item ENCHANTED_GOLDEN_APPLE;
   public static final Item SIGN;
   public static final Item BUCKET;
   public static final Item WATER_BUCKET;
   public static final Item LAVA_BUCKET;
   public static final Item MINECART;
   public static final Item SADDLE;
   public static final Item REDSTONE;
   public static final Item SNOWBALL;
   public static final Item OAK_BOAT;
   public static final Item SPRUCE_BOAT;
   public static final Item BIRCH_BOAT;
   public static final Item JUNGLE_BOAT;
   public static final Item ACACIA_BOAT;
   public static final Item DARK_OAK_BOAT;
   public static final Item LEATHER;
   public static final Item MILK_BUCKET;
   public static final Item PUFFERFISH_BUCKET;
   public static final Item SALMON_BUCKET;
   public static final Item COD_BUCKET;
   public static final Item TROPICAL_FISH_BUCKET;
   public static final Item BRICK;
   public static final Item CLAY_BALL;
   public static final Item PAPER;
   public static final Item BOOK;
   public static final Item SLIME_BALL;
   public static final Item CHEST_MINECART;
   public static final Item FURNACE_MINECART;
   public static final Item EGG;
   public static final Item COMPASS;
   public static final Item FISHING_ROD;
   public static final Item CLOCK;
   public static final Item GLOWSTONE_DUST;
   public static final Item COD;
   public static final Item SALMON;
   public static final Item TROPICAL_FISH;
   public static final Item PUFFERFISH;
   public static final Item COOKED_COD;
   public static final Item COOKED_SALMON;
   public static final Item BONE_MEAL;
   public static final Item ORANGE_DYE;
   public static final Item MAGENTA_DYE;
   public static final Item LIGHT_BLUE_DYE;
   public static final Item DANDELION_YELLOW;
   public static final Item LIME_DYE;
   public static final Item PINK_DYE;
   public static final Item GRAY_DYE;
   public static final Item LIGHT_GRAY_DYE;
   public static final Item CYAN_DYE;
   public static final Item PURPLE_DYE;
   public static final Item LAPIS_LAZULI;
   public static final Item COCOA_BEANS;
   public static final Item CACTUS_GREEN;
   public static final Item ROSE_RED;
   public static final Item INK_SAC;
   public static final Item BONE;
   public static final Item SUGAR;
   public static final Item WHITE_BED;
   public static final Item ORANGE_BED;
   public static final Item MAGENTA_BED;
   public static final Item LIGHT_BLUE_BED;
   public static final Item YELLOW_BED;
   public static final Item LIME_BED;
   public static final Item PINK_BED;
   public static final Item GRAY_BED;
   public static final Item LIGHT_GRAY_BED;
   public static final Item CYAN_BED;
   public static final Item PURPLE_BED;
   public static final Item BLUE_BED;
   public static final Item BROWN_BED;
   public static final Item GREEN_BED;
   public static final Item RED_BED;
   public static final Item BLACK_BED;
   public static final Item COOKIE;
   public static final Item FILLED_MAP;
   public static final Item SHEARS;
   public static final Item MELON_SLICE;
   public static final Item DRIED_KELP;
   public static final Item PUMPKIN_SEEDS;
   public static final Item MELON_SEEDS;
   public static final Item BEEF;
   public static final Item COOKED_BEEF;
   public static final Item CHICKEN;
   public static final Item COOKED_CHICKEN;
   public static final Item MUTTON;
   public static final Item COOKED_MUTTON;
   public static final Item RABBIT;
   public static final Item COOKED_RABBIT;
   public static final Item RABBIT_STEW;
   public static final Item RABBIT_FOOT;
   public static final Item RABBIT_HIDE;
   public static final Item ROTTEN_FLESH;
   public static final Item ENDER_PEARL;
   public static final Item BLAZE_ROD;
   public static final Item GHAST_TEAR;
   public static final Item GOLD_NUGGET;
   public static final Item NETHER_WART;
   public static final Item POTION;
   public static final Item SPLASH_POTION;
   public static final Item LINGERING_POTION;
   public static final Item GLASS_BOTTLE;
   public static final Item DRAGON_BREATH;
   public static final Item SPIDER_EYE;
   public static final Item FERMENTED_SPIDER_EYE;
   public static final Item BLAZE_POWDER;
   public static final Item MAGMA_CREAM;
   public static final Item ENDER_EYE;
   public static final Item GLISTERING_MELON_SLICE;
   public static final Item BAT_SPAWN_EGG;
   public static final Item BLAZE_SPAWN_EGG;
   public static final Item CAVE_SPIDER_SPAWN_EGG;
   public static final Item CHICKEN_SPAWN_EGG;
   public static final Item COD_SPAWN_EGG;
   public static final Item COW_SPAWN_EGG;
   public static final Item CREEPER_SPAWN_EGG;
   public static final Item DOLPHIN_SPAWN_EGG;
   public static final Item DONKEY_SPAWN_EGG;
   public static final Item ELDER_GUARDIAN_SPAWN_EGG;
   public static final Item ENDERMAN_SPAWN_EGG;
   public static final Item ENDERMITE_SPAWN_EGG;
   public static final Item EVOKER_SPAWN_EGG;
   public static final Item GHAST_SPAWN_EGG;
   public static final Item GUARDIAN_SPAWN_EGG;
   public static final Item HORSE_SPAWN_EGG;
   public static final Item HUSK_SPAWN_EGG;
   public static final Item LLAMA_SPAWN_EGG;
   public static final Item MAGMA_CUBE_SPAWN_EGG;
   public static final Item MOOSHROOM_SPAWN_EGG;
   public static final Item MULE_SPAWN_EGG;
   public static final Item OCELOT_SPAWN_EGG;
   public static final Item PARROT_SPAWN_EGG;
   public static final Item PIG_SPAWN_EGG;
   public static final Item PHANTOM_SPAWN_EGG;
   public static final Item POLAR_BEAR_SPAWN_EGG;
   public static final Item PUFFERFISH_SPAWN_EGG;
   public static final Item RABBIT_SPAWN_EGG;
   public static final Item SALMON_SPAWN_EGG;
   public static final Item SHEEP_SPAWN_EGG;
   public static final Item SHULKER_SPAWN_EGG;
   public static final Item SILVERFISH_SPAWN_EGG;
   public static final Item SKELETON_SPAWN_EGG;
   public static final Item SKELETON_HORSE_SPAWN_EGG;
   public static final Item SLIME_SPAWN_EGG;
   public static final Item SPIDER_SPAWN_EGG;
   public static final Item SQUID_SPAWN_EGG;
   public static final Item STRAY_SPAWN_EGG;
   public static final Item TROPICAL_FISH_SPAWN_EGG;
   public static final Item TURTLE_SPAWN_EGG;
   public static final Item VEX_SPAWN_EGG;
   public static final Item VILLAGER_SPAWN_EGG;
   public static final Item VINDICATOR_SPAWN_EGG;
   public static final Item WITCH_SPAWN_EGG;
   public static final Item WITHER_SKELETON_SPAWN_EGG;
   public static final Item WOLF_SPAWN_EGG;
   public static final Item ZOMBIE_SPAWN_EGG;
   public static final Item ZOMBIE_HORSE_SPAWN_EGG;
   public static final Item ZOMBIE_PIGMAN_SPAWN_EGG;
   public static final Item ZOMBIE_VILLAGER_SPAWN_EGG;
   public static final Item EXPERIENCE_BOTTLE;
   public static final Item FIRE_CHARGE;
   public static final Item WRITABLE_BOOK;
   public static final Item WRITTEN_BOOK;
   public static final Item EMERALD;
   public static final Item ITEM_FRAME;
   public static final Item CARROT;
   public static final Item POTATO;
   public static final Item BAKED_POTATO;
   public static final Item POISONOUS_POTATO;
   public static final Item MAP;
   public static final Item GOLDEN_CARROT;
   public static final Item SKELETON_SKULL;
   public static final Item WITHER_SKELETON_SKULL;
   public static final Item PLAYER_HEAD;
   public static final Item CREEPER_HEAD;
   public static final Item ZOMBIE_HEAD;
   public static final Item DRAGON_HEAD;
   public static final Item CARROT_ON_A_STICK;
   public static final Item NETHER_STAR;
   public static final Item PUMPKIN_PIE;
   public static final Item FIREWORK_ROCKET;
   public static final Item FIREWORK_STAR;
   public static final Item ENCHANTED_BOOK;
   public static final Item NETHER_BRICK;
   public static final Item QUARTZ;
   public static final Item TNT_MINECART;
   public static final Item HOPPER_MINECART;
   public static final Item ARMOR_STAND;
   public static final Item IRON_HORSE_ARMOR;
   public static final Item GOLDEN_HORSE_ARMOR;
   public static final Item DIAMOND_HORSE_ARMOR;
   public static final Item LEAD;
   public static final Item NAME_TAG;
   public static final Item COMMAND_BLOCK_MINECART;
   public static final Item MUSIC_DISC_13;
   public static final Item MUSIC_DISC_CAT;
   public static final Item MUSIC_DISC_BLOCKS;
   public static final Item MUSIC_DISC_CHIRP;
   public static final Item MUSIC_DISC_FAR;
   public static final Item MUSIC_DISC_MALL;
   public static final Item MUSIC_DISC_MELLOHI;
   public static final Item MUSIC_DISC_STAL;
   public static final Item MUSIC_DISC_STRAD;
   public static final Item MUSIC_DISC_WARD;
   public static final Item MUSIC_DISC_11;
   public static final Item MUSIC_DISC_WAIT;
   public static final Item PRISMARINE_SHARD;
   public static final Item PRISMARINE_CRYSTALS;
   public static final Item WHITE_BANNER;
   public static final Item ORANGE_BANNER;
   public static final Item MAGENTA_BANNER;
   public static final Item LIGHT_BLUE_BANNER;
   public static final Item YELLOW_BANNER;
   public static final Item LIME_BANNER;
   public static final Item PINK_BANNER;
   public static final Item GRAY_BANNER;
   public static final Item LIGHT_GRAY_BANNER;
   public static final Item CYAN_BANNER;
   public static final Item PURPLE_BANNER;
   public static final Item BLUE_BANNER;
   public static final Item BROWN_BANNER;
   public static final Item GREEN_BANNER;
   public static final Item RED_BANNER;
   public static final Item BLACK_BANNER;
   public static final Item END_CRYSTAL;
   public static final Item SHIELD;
   public static final Item ELYTRA;
   public static final Item CHORUS_FRUIT;
   public static final Item POPPED_CHORUS_FRUIT;
   public static final Item BEETROOT_SEEDS;
   public static final Item BEETROOT;
   public static final Item BEETROOT_SOUP;
   public static final Item TOTEM_OF_UNDYING;
   public static final Item SHULKER_SHELL;
   public static final Item IRON_NUGGET;
   public static final Item KNOWLEDGE_BOOK;
   public static final Item SCUTE;
   public static final Item DEBUG_STICK;
   public static final Item TRIDENT;
   public static final Item PHANTOM_MEMBRANE;
   public static final Item NAUTILUS_SHELL;
   public static final Item HEART_OF_THE_SEA;

   private static Item getRegisteredItem(String name) {
      Item item = IRegistry.field_212630_s.func_212608_b(new ResourceLocation(name));
      if (item == null) {
         throw new IllegalStateException("Invalid Item requested: " + name);
      } else {
         return item;
      }
   }

   static {
      if (!Bootstrap.isRegistered()) {
         throw new RuntimeException("Accessed Items before Bootstrap!");
      } else {
         AIR = getRegisteredItem("air");
         IRON_SHOVEL = getRegisteredItem("iron_shovel");
         IRON_PICKAXE = getRegisteredItem("iron_pickaxe");
         IRON_AXE = getRegisteredItem("iron_axe");
         FLINT_AND_STEEL = getRegisteredItem("flint_and_steel");
         APPLE = getRegisteredItem("apple");
         BOW = getRegisteredItem("bow");
         ARROW = getRegisteredItem("arrow");
         SPECTRAL_ARROW = getRegisteredItem("spectral_arrow");
         TIPPED_ARROW = getRegisteredItem("tipped_arrow");
         COAL = getRegisteredItem("coal");
         CHARCOAL = getRegisteredItem("charcoal");
         DIAMOND = getRegisteredItem("diamond");
         IRON_INGOT = getRegisteredItem("iron_ingot");
         GOLD_INGOT = getRegisteredItem("gold_ingot");
         IRON_SWORD = getRegisteredItem("iron_sword");
         WOODEN_SWORD = getRegisteredItem("wooden_sword");
         WOODEN_SHOVEL = getRegisteredItem("wooden_shovel");
         WOODEN_PICKAXE = getRegisteredItem("wooden_pickaxe");
         WOODEN_AXE = getRegisteredItem("wooden_axe");
         STONE_SWORD = getRegisteredItem("stone_sword");
         STONE_SHOVEL = getRegisteredItem("stone_shovel");
         STONE_PICKAXE = getRegisteredItem("stone_pickaxe");
         STONE_AXE = getRegisteredItem("stone_axe");
         DIAMOND_SWORD = getRegisteredItem("diamond_sword");
         DIAMOND_SHOVEL = getRegisteredItem("diamond_shovel");
         DIAMOND_PICKAXE = getRegisteredItem("diamond_pickaxe");
         DIAMOND_AXE = getRegisteredItem("diamond_axe");
         STICK = getRegisteredItem("stick");
         BOWL = getRegisteredItem("bowl");
         MUSHROOM_STEW = getRegisteredItem("mushroom_stew");
         GOLDEN_SWORD = getRegisteredItem("golden_sword");
         GOLDEN_SHOVEL = getRegisteredItem("golden_shovel");
         GOLDEN_PICKAXE = getRegisteredItem("golden_pickaxe");
         GOLDEN_AXE = getRegisteredItem("golden_axe");
         STRING = getRegisteredItem("string");
         FEATHER = getRegisteredItem("feather");
         GUNPOWDER = getRegisteredItem("gunpowder");
         WOODEN_HOE = getRegisteredItem("wooden_hoe");
         STONE_HOE = getRegisteredItem("stone_hoe");
         IRON_HOE = getRegisteredItem("iron_hoe");
         DIAMOND_HOE = getRegisteredItem("diamond_hoe");
         GOLDEN_HOE = getRegisteredItem("golden_hoe");
         WHEAT_SEEDS = getRegisteredItem("wheat_seeds");
         WHEAT = getRegisteredItem("wheat");
         BREAD = getRegisteredItem("bread");
         LEATHER_HELMET = getRegisteredItem("leather_helmet");
         LEATHER_CHESTPLATE = getRegisteredItem("leather_chestplate");
         LEATHER_LEGGINGS = getRegisteredItem("leather_leggings");
         LEATHER_BOOTS = getRegisteredItem("leather_boots");
         CHAINMAIL_HELMET = getRegisteredItem("chainmail_helmet");
         CHAINMAIL_CHESTPLATE = getRegisteredItem("chainmail_chestplate");
         CHAINMAIL_LEGGINGS = getRegisteredItem("chainmail_leggings");
         CHAINMAIL_BOOTS = getRegisteredItem("chainmail_boots");
         IRON_HELMET = getRegisteredItem("iron_helmet");
         IRON_CHESTPLATE = getRegisteredItem("iron_chestplate");
         IRON_LEGGINGS = getRegisteredItem("iron_leggings");
         IRON_BOOTS = getRegisteredItem("iron_boots");
         DIAMOND_HELMET = getRegisteredItem("diamond_helmet");
         DIAMOND_CHESTPLATE = getRegisteredItem("diamond_chestplate");
         DIAMOND_LEGGINGS = getRegisteredItem("diamond_leggings");
         DIAMOND_BOOTS = getRegisteredItem("diamond_boots");
         GOLDEN_HELMET = getRegisteredItem("golden_helmet");
         GOLDEN_CHESTPLATE = getRegisteredItem("golden_chestplate");
         GOLDEN_LEGGINGS = getRegisteredItem("golden_leggings");
         GOLDEN_BOOTS = getRegisteredItem("golden_boots");
         TURTLE_HELMET = getRegisteredItem("turtle_helmet");
         FLINT = getRegisteredItem("flint");
         PORKCHOP = getRegisteredItem("porkchop");
         COOKED_PORKCHOP = getRegisteredItem("cooked_porkchop");
         PAINTING = getRegisteredItem("painting");
         GOLDEN_APPLE = getRegisteredItem("golden_apple");
         ENCHANTED_GOLDEN_APPLE = getRegisteredItem("enchanted_golden_apple");
         SIGN = getRegisteredItem("sign");
         BUCKET = getRegisteredItem("bucket");
         WATER_BUCKET = getRegisteredItem("water_bucket");
         LAVA_BUCKET = getRegisteredItem("lava_bucket");
         MINECART = getRegisteredItem("minecart");
         SADDLE = getRegisteredItem("saddle");
         REDSTONE = getRegisteredItem("redstone");
         SNOWBALL = getRegisteredItem("snowball");
         OAK_BOAT = getRegisteredItem("oak_boat");
         SPRUCE_BOAT = getRegisteredItem("spruce_boat");
         BIRCH_BOAT = getRegisteredItem("birch_boat");
         JUNGLE_BOAT = getRegisteredItem("jungle_boat");
         ACACIA_BOAT = getRegisteredItem("acacia_boat");
         DARK_OAK_BOAT = getRegisteredItem("dark_oak_boat");
         LEATHER = getRegisteredItem("leather");
         MILK_BUCKET = getRegisteredItem("milk_bucket");
         PUFFERFISH_BUCKET = getRegisteredItem("pufferfish_bucket");
         SALMON_BUCKET = getRegisteredItem("salmon_bucket");
         COD_BUCKET = getRegisteredItem("cod_bucket");
         TROPICAL_FISH_BUCKET = getRegisteredItem("tropical_fish_bucket");
         BRICK = getRegisteredItem("brick");
         CLAY_BALL = getRegisteredItem("clay_ball");
         PAPER = getRegisteredItem("paper");
         BOOK = getRegisteredItem("book");
         SLIME_BALL = getRegisteredItem("slime_ball");
         CHEST_MINECART = getRegisteredItem("chest_minecart");
         FURNACE_MINECART = getRegisteredItem("furnace_minecart");
         EGG = getRegisteredItem("egg");
         COMPASS = getRegisteredItem("compass");
         FISHING_ROD = getRegisteredItem("fishing_rod");
         CLOCK = getRegisteredItem("clock");
         GLOWSTONE_DUST = getRegisteredItem("glowstone_dust");
         COD = getRegisteredItem("cod");
         SALMON = getRegisteredItem("salmon");
         TROPICAL_FISH = getRegisteredItem("tropical_fish");
         PUFFERFISH = getRegisteredItem("pufferfish");
         COOKED_COD = getRegisteredItem("cooked_cod");
         COOKED_SALMON = getRegisteredItem("cooked_salmon");
         BONE_MEAL = getRegisteredItem("bone_meal");
         ORANGE_DYE = getRegisteredItem("orange_dye");
         MAGENTA_DYE = getRegisteredItem("magenta_dye");
         LIGHT_BLUE_DYE = getRegisteredItem("light_blue_dye");
         DANDELION_YELLOW = getRegisteredItem("dandelion_yellow");
         LIME_DYE = getRegisteredItem("lime_dye");
         PINK_DYE = getRegisteredItem("pink_dye");
         GRAY_DYE = getRegisteredItem("gray_dye");
         LIGHT_GRAY_DYE = getRegisteredItem("light_gray_dye");
         CYAN_DYE = getRegisteredItem("cyan_dye");
         PURPLE_DYE = getRegisteredItem("purple_dye");
         LAPIS_LAZULI = getRegisteredItem("lapis_lazuli");
         COCOA_BEANS = getRegisteredItem("cocoa_beans");
         CACTUS_GREEN = getRegisteredItem("cactus_green");
         ROSE_RED = getRegisteredItem("rose_red");
         INK_SAC = getRegisteredItem("ink_sac");
         BONE = getRegisteredItem("bone");
         SUGAR = getRegisteredItem("sugar");
         WHITE_BED = getRegisteredItem("white_bed");
         ORANGE_BED = getRegisteredItem("orange_bed");
         MAGENTA_BED = getRegisteredItem("magenta_bed");
         LIGHT_BLUE_BED = getRegisteredItem("light_blue_bed");
         YELLOW_BED = getRegisteredItem("yellow_bed");
         LIME_BED = getRegisteredItem("lime_bed");
         PINK_BED = getRegisteredItem("pink_bed");
         GRAY_BED = getRegisteredItem("gray_bed");
         LIGHT_GRAY_BED = getRegisteredItem("light_gray_bed");
         CYAN_BED = getRegisteredItem("cyan_bed");
         PURPLE_BED = getRegisteredItem("purple_bed");
         BLUE_BED = getRegisteredItem("blue_bed");
         BROWN_BED = getRegisteredItem("brown_bed");
         GREEN_BED = getRegisteredItem("green_bed");
         RED_BED = getRegisteredItem("red_bed");
         BLACK_BED = getRegisteredItem("black_bed");
         COOKIE = getRegisteredItem("cookie");
         FILLED_MAP = getRegisteredItem("filled_map");
         SHEARS = getRegisteredItem("shears");
         MELON_SLICE = getRegisteredItem("melon_slice");
         DRIED_KELP = getRegisteredItem("dried_kelp");
         PUMPKIN_SEEDS = getRegisteredItem("pumpkin_seeds");
         MELON_SEEDS = getRegisteredItem("melon_seeds");
         BEEF = getRegisteredItem("beef");
         COOKED_BEEF = getRegisteredItem("cooked_beef");
         CHICKEN = getRegisteredItem("chicken");
         COOKED_CHICKEN = getRegisteredItem("cooked_chicken");
         MUTTON = getRegisteredItem("mutton");
         COOKED_MUTTON = getRegisteredItem("cooked_mutton");
         RABBIT = getRegisteredItem("rabbit");
         COOKED_RABBIT = getRegisteredItem("cooked_rabbit");
         RABBIT_STEW = getRegisteredItem("rabbit_stew");
         RABBIT_FOOT = getRegisteredItem("rabbit_foot");
         RABBIT_HIDE = getRegisteredItem("rabbit_hide");
         ROTTEN_FLESH = getRegisteredItem("rotten_flesh");
         ENDER_PEARL = getRegisteredItem("ender_pearl");
         BLAZE_ROD = getRegisteredItem("blaze_rod");
         GHAST_TEAR = getRegisteredItem("ghast_tear");
         GOLD_NUGGET = getRegisteredItem("gold_nugget");
         NETHER_WART = getRegisteredItem("nether_wart");
         POTION = getRegisteredItem("potion");
         SPLASH_POTION = getRegisteredItem("splash_potion");
         LINGERING_POTION = getRegisteredItem("lingering_potion");
         GLASS_BOTTLE = getRegisteredItem("glass_bottle");
         DRAGON_BREATH = getRegisteredItem("dragon_breath");
         SPIDER_EYE = getRegisteredItem("spider_eye");
         FERMENTED_SPIDER_EYE = getRegisteredItem("fermented_spider_eye");
         BLAZE_POWDER = getRegisteredItem("blaze_powder");
         MAGMA_CREAM = getRegisteredItem("magma_cream");
         ENDER_EYE = getRegisteredItem("ender_eye");
         GLISTERING_MELON_SLICE = getRegisteredItem("glistering_melon_slice");
         BAT_SPAWN_EGG = getRegisteredItem("bat_spawn_egg");
         BLAZE_SPAWN_EGG = getRegisteredItem("blaze_spawn_egg");
         CAVE_SPIDER_SPAWN_EGG = getRegisteredItem("cave_spider_spawn_egg");
         CHICKEN_SPAWN_EGG = getRegisteredItem("chicken_spawn_egg");
         COD_SPAWN_EGG = getRegisteredItem("cod_spawn_egg");
         COW_SPAWN_EGG = getRegisteredItem("cow_spawn_egg");
         CREEPER_SPAWN_EGG = getRegisteredItem("creeper_spawn_egg");
         DOLPHIN_SPAWN_EGG = getRegisteredItem("dolphin_spawn_egg");
         DONKEY_SPAWN_EGG = getRegisteredItem("donkey_spawn_egg");
         ELDER_GUARDIAN_SPAWN_EGG = getRegisteredItem("elder_guardian_spawn_egg");
         ENDERMAN_SPAWN_EGG = getRegisteredItem("enderman_spawn_egg");
         ENDERMITE_SPAWN_EGG = getRegisteredItem("endermite_spawn_egg");
         EVOKER_SPAWN_EGG = getRegisteredItem("evoker_spawn_egg");
         GHAST_SPAWN_EGG = getRegisteredItem("ghast_spawn_egg");
         GUARDIAN_SPAWN_EGG = getRegisteredItem("guardian_spawn_egg");
         HORSE_SPAWN_EGG = getRegisteredItem("horse_spawn_egg");
         HUSK_SPAWN_EGG = getRegisteredItem("husk_spawn_egg");
         LLAMA_SPAWN_EGG = getRegisteredItem("llama_spawn_egg");
         MAGMA_CUBE_SPAWN_EGG = getRegisteredItem("magma_cube_spawn_egg");
         MOOSHROOM_SPAWN_EGG = getRegisteredItem("mooshroom_spawn_egg");
         MULE_SPAWN_EGG = getRegisteredItem("mule_spawn_egg");
         OCELOT_SPAWN_EGG = getRegisteredItem("ocelot_spawn_egg");
         PARROT_SPAWN_EGG = getRegisteredItem("parrot_spawn_egg");
         PIG_SPAWN_EGG = getRegisteredItem("pig_spawn_egg");
         PHANTOM_SPAWN_EGG = getRegisteredItem("phantom_spawn_egg");
         POLAR_BEAR_SPAWN_EGG = getRegisteredItem("polar_bear_spawn_egg");
         PUFFERFISH_SPAWN_EGG = getRegisteredItem("pufferfish_spawn_egg");
         RABBIT_SPAWN_EGG = getRegisteredItem("rabbit_spawn_egg");
         SALMON_SPAWN_EGG = getRegisteredItem("salmon_spawn_egg");
         SHEEP_SPAWN_EGG = getRegisteredItem("sheep_spawn_egg");
         SHULKER_SPAWN_EGG = getRegisteredItem("shulker_spawn_egg");
         SILVERFISH_SPAWN_EGG = getRegisteredItem("silverfish_spawn_egg");
         SKELETON_SPAWN_EGG = getRegisteredItem("skeleton_spawn_egg");
         SKELETON_HORSE_SPAWN_EGG = getRegisteredItem("skeleton_horse_spawn_egg");
         SLIME_SPAWN_EGG = getRegisteredItem("slime_spawn_egg");
         SPIDER_SPAWN_EGG = getRegisteredItem("spider_spawn_egg");
         SQUID_SPAWN_EGG = getRegisteredItem("squid_spawn_egg");
         STRAY_SPAWN_EGG = getRegisteredItem("stray_spawn_egg");
         TROPICAL_FISH_SPAWN_EGG = getRegisteredItem("tropical_fish_spawn_egg");
         TURTLE_SPAWN_EGG = getRegisteredItem("turtle_spawn_egg");
         VEX_SPAWN_EGG = getRegisteredItem("vex_spawn_egg");
         VILLAGER_SPAWN_EGG = getRegisteredItem("villager_spawn_egg");
         VINDICATOR_SPAWN_EGG = getRegisteredItem("vindicator_spawn_egg");
         WITCH_SPAWN_EGG = getRegisteredItem("witch_spawn_egg");
         WITHER_SKELETON_SPAWN_EGG = getRegisteredItem("wither_skeleton_spawn_egg");
         WOLF_SPAWN_EGG = getRegisteredItem("wolf_spawn_egg");
         ZOMBIE_SPAWN_EGG = getRegisteredItem("zombie_spawn_egg");
         ZOMBIE_HORSE_SPAWN_EGG = getRegisteredItem("zombie_horse_spawn_egg");
         ZOMBIE_PIGMAN_SPAWN_EGG = getRegisteredItem("zombie_pigman_spawn_egg");
         ZOMBIE_VILLAGER_SPAWN_EGG = getRegisteredItem("zombie_villager_spawn_egg");
         EXPERIENCE_BOTTLE = getRegisteredItem("experience_bottle");
         FIRE_CHARGE = getRegisteredItem("fire_charge");
         WRITABLE_BOOK = getRegisteredItem("writable_book");
         WRITTEN_BOOK = getRegisteredItem("written_book");
         EMERALD = getRegisteredItem("emerald");
         ITEM_FRAME = getRegisteredItem("item_frame");
         CARROT = getRegisteredItem("carrot");
         POTATO = getRegisteredItem("potato");
         BAKED_POTATO = getRegisteredItem("baked_potato");
         POISONOUS_POTATO = getRegisteredItem("poisonous_potato");
         MAP = getRegisteredItem("map");
         GOLDEN_CARROT = getRegisteredItem("golden_carrot");
         SKELETON_SKULL = getRegisteredItem("skeleton_skull");
         WITHER_SKELETON_SKULL = getRegisteredItem("wither_skeleton_skull");
         PLAYER_HEAD = getRegisteredItem("player_head");
         CREEPER_HEAD = getRegisteredItem("creeper_head");
         ZOMBIE_HEAD = getRegisteredItem("zombie_head");
         DRAGON_HEAD = getRegisteredItem("dragon_head");
         CARROT_ON_A_STICK = getRegisteredItem("carrot_on_a_stick");
         NETHER_STAR = getRegisteredItem("nether_star");
         PUMPKIN_PIE = getRegisteredItem("pumpkin_pie");
         FIREWORK_ROCKET = getRegisteredItem("firework_rocket");
         FIREWORK_STAR = getRegisteredItem("firework_star");
         ENCHANTED_BOOK = getRegisteredItem("enchanted_book");
         NETHER_BRICK = getRegisteredItem("nether_brick");
         QUARTZ = getRegisteredItem("quartz");
         TNT_MINECART = getRegisteredItem("tnt_minecart");
         HOPPER_MINECART = getRegisteredItem("hopper_minecart");
         ARMOR_STAND = getRegisteredItem("armor_stand");
         IRON_HORSE_ARMOR = getRegisteredItem("iron_horse_armor");
         GOLDEN_HORSE_ARMOR = getRegisteredItem("golden_horse_armor");
         DIAMOND_HORSE_ARMOR = getRegisteredItem("diamond_horse_armor");
         LEAD = getRegisteredItem("lead");
         NAME_TAG = getRegisteredItem("name_tag");
         COMMAND_BLOCK_MINECART = getRegisteredItem("command_block_minecart");
         MUSIC_DISC_13 = getRegisteredItem("music_disc_13");
         MUSIC_DISC_CAT = getRegisteredItem("music_disc_cat");
         MUSIC_DISC_BLOCKS = getRegisteredItem("music_disc_blocks");
         MUSIC_DISC_CHIRP = getRegisteredItem("music_disc_chirp");
         MUSIC_DISC_FAR = getRegisteredItem("music_disc_far");
         MUSIC_DISC_MALL = getRegisteredItem("music_disc_mall");
         MUSIC_DISC_MELLOHI = getRegisteredItem("music_disc_mellohi");
         MUSIC_DISC_STAL = getRegisteredItem("music_disc_stal");
         MUSIC_DISC_STRAD = getRegisteredItem("music_disc_strad");
         MUSIC_DISC_WARD = getRegisteredItem("music_disc_ward");
         MUSIC_DISC_11 = getRegisteredItem("music_disc_11");
         MUSIC_DISC_WAIT = getRegisteredItem("music_disc_wait");
         PRISMARINE_SHARD = getRegisteredItem("prismarine_shard");
         PRISMARINE_CRYSTALS = getRegisteredItem("prismarine_crystals");
         WHITE_BANNER = getRegisteredItem("white_banner");
         ORANGE_BANNER = getRegisteredItem("orange_banner");
         MAGENTA_BANNER = getRegisteredItem("magenta_banner");
         LIGHT_BLUE_BANNER = getRegisteredItem("light_blue_banner");
         YELLOW_BANNER = getRegisteredItem("yellow_banner");
         LIME_BANNER = getRegisteredItem("lime_banner");
         PINK_BANNER = getRegisteredItem("pink_banner");
         GRAY_BANNER = getRegisteredItem("gray_banner");
         LIGHT_GRAY_BANNER = getRegisteredItem("light_gray_banner");
         CYAN_BANNER = getRegisteredItem("cyan_banner");
         PURPLE_BANNER = getRegisteredItem("purple_banner");
         BLUE_BANNER = getRegisteredItem("blue_banner");
         BROWN_BANNER = getRegisteredItem("brown_banner");
         GREEN_BANNER = getRegisteredItem("green_banner");
         RED_BANNER = getRegisteredItem("red_banner");
         BLACK_BANNER = getRegisteredItem("black_banner");
         END_CRYSTAL = getRegisteredItem("end_crystal");
         SHIELD = getRegisteredItem("shield");
         ELYTRA = getRegisteredItem("elytra");
         CHORUS_FRUIT = getRegisteredItem("chorus_fruit");
         POPPED_CHORUS_FRUIT = getRegisteredItem("popped_chorus_fruit");
         BEETROOT_SEEDS = getRegisteredItem("beetroot_seeds");
         BEETROOT = getRegisteredItem("beetroot");
         BEETROOT_SOUP = getRegisteredItem("beetroot_soup");
         TOTEM_OF_UNDYING = getRegisteredItem("totem_of_undying");
         SHULKER_SHELL = getRegisteredItem("shulker_shell");
         IRON_NUGGET = getRegisteredItem("iron_nugget");
         KNOWLEDGE_BOOK = getRegisteredItem("knowledge_book");
         SCUTE = getRegisteredItem("scute");
         DEBUG_STICK = getRegisteredItem("debug_stick");
         TRIDENT = getRegisteredItem("trident");
         PHANTOM_MEMBRANE = getRegisteredItem("phantom_membrane");
         NAUTILUS_SHELL = getRegisteredItem("nautilus_shell");
         HEART_OF_THE_SEA = getRegisteredItem("heart_of_the_sea");
      }
   }
}