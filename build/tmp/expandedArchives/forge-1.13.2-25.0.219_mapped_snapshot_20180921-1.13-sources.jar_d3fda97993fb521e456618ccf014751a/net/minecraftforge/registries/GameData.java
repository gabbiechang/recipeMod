/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.registries;

import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.fml.LifecycleEventProvider;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupQuery;
import net.minecraftforge.fml.common.EnhancedRuntimeException;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.FMLModIdMappingEvent;
import net.minecraftforge.fml.loading.AdvancedLogMessageAdapter;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

import net.minecraftforge.fml.common.EnhancedRuntimeException.WrappedPrintStream;

/**
 * INTERNAL ONLY
 * MODDERS SHOULD HAVE NO REASON TO USE THIS CLASS
 */
public class GameData
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ResourceLocation BLOCKS       = new ResourceLocation("minecraft:blocks");
    public static final ResourceLocation ITEMS        = new ResourceLocation("minecraft:items");
    public static final ResourceLocation POTIONS      = new ResourceLocation("minecraft:potions");
    public static final ResourceLocation BIOMES       = new ResourceLocation("minecraft:biomes");
    public static final ResourceLocation SOUNDEVENTS  = new ResourceLocation("minecraft:soundevents");
    public static final ResourceLocation POTIONTYPES  = new ResourceLocation("minecraft:potiontypes");
    public static final ResourceLocation ENCHANTMENTS = new ResourceLocation("minecraft:enchantments");
    public static final ResourceLocation ENTITIES     = new ResourceLocation("minecraft:entities");
    public static final ResourceLocation TILEENTITIES = new ResourceLocation("minecraft:tileentities");
    public static final ResourceLocation PROFESSIONS  = new ResourceLocation("minecraft:villagerprofessions");
    public static final ResourceLocation MODDIMENSIONS = new ResourceLocation("forge:moddimensions");
    public static final ResourceLocation SERIALIZERS  = new ResourceLocation("minecraft:dataserializers");
    
    private static final int MAX_REGISTRY_SIZE = Integer.MAX_VALUE >> 5;
    private static final int MAX_BLOCK_ID = 4095;
    private static final int MAX_ITEM_ID = 31999;
    private static final int MAX_POTION_ID = 255; // SPacketEntityEffect sends bytes, we can only use 255
    private static final int MAX_BIOME_ID = 255; // Maximum number in a byte in the chunk
    private static final int MAX_SOUND_ID = Integer.MAX_VALUE >> 5; // Varint (SPacketSoundEffect)
    private static final int MAX_POTIONTYPE_ID = MAX_REGISTRY_SIZE; // Int (SPacketEffect)
    private static final int MAX_ENCHANTMENT_ID = Short.MAX_VALUE - 1; // Short - serialized as a short in ItemStack NBTs.
    private static final int MAX_ENTITY_ID = MAX_REGISTRY_SIZE; // Varint (SPacketSpawnMob)
    private static final int MAX_TILE_ENTITY_ID = Integer.MAX_VALUE; //Doesnt seem to be serialized anywhere, so no max.
    private static final int MAX_PROFESSION_ID = 1024; //TODO: Is this serialized anywhere anymore?
    private static final int MIN_SERIALIZER_ID = 256; // Leave room for vanilla entries
    private static final int MAX_SERIALIZER_ID = Integer.MAX_VALUE >> 5; // Varint (EntityDataManager)

    private static final ResourceLocation BLOCK_TO_ITEM    = new ResourceLocation("minecraft:blocktoitemmap");
    private static final ResourceLocation BLOCKSTATE_TO_ID = new ResourceLocation("minecraft:blockstatetoid");
    private static final ResourceLocation SERIALIZER_TO_ENTRY   = new ResourceLocation("forge:serializer_to_entry");

    private static boolean hasInit = false;
    private static final boolean DISABLE_VANILLA_REGISTRIES = Boolean.parseBoolean(System.getProperty("forge.disableVanillaGameData", "false")); // Use for unit tests/debugging
    private static final BiConsumer<ResourceLocation, ForgeRegistry<?>> LOCK_VANILLA = (name, reg) -> reg.slaves.values().stream().filter(o -> o instanceof ILockableRegistry).forEach(o -> ((ILockableRegistry)o).lock());

    static {
        init();
    }

    @SuppressWarnings("unchecked")
    public static void init()
    {
        if (DISABLE_VANILLA_REGISTRIES)
        {
            LOGGER.warn(REGISTRIES, "DISABLING VANILLA REGISTRY CREATION AS PER SYSTEM VARIABLE SETTING! forge.disableVanillaGameData");
            return;
        }
        if (hasInit)
            return;
        hasInit = true;
        makeRegistry(BLOCKS,       Block.class,       MAX_BLOCK_ID, new ResourceLocation("air")).addCallback(BlockCallbacks.INSTANCE).create();
        makeRegistry(ITEMS,        Item.class,        MAX_ITEM_ID).addCallback(ItemCallbacks.INSTANCE).create();
        makeRegistry(POTIONS,      Potion.class,      MAX_POTION_ID).create();
        makeRegistry(BIOMES,       Biome.class,       MAX_BIOME_ID).create();
        makeRegistry(SOUNDEVENTS,  SoundEvent.class,  MAX_SOUND_ID).create();
        makeRegistry(POTIONTYPES,  PotionType.class,  MAX_POTIONTYPE_ID, new ResourceLocation("empty")).create();
        makeRegistry(ENCHANTMENTS, Enchantment.class, MAX_ENCHANTMENT_ID).create();
        makeRegistry(PROFESSIONS,  VillagerProfession.class, MAX_PROFESSION_ID).create();
        // TODO do we need the callback and the static field anymore?
        makeRegistry(ENTITIES,     EntityType.class, MAX_ENTITY_ID).create();
        makeRegistry(TILEENTITIES, TileEntityType.class, MAX_TILE_ENTITY_ID).disableSaving().create();
        makeRegistry(MODDIMENSIONS, ModDimension.class, MAX_REGISTRY_SIZE).disableSaving().create();
        makeRegistry(SERIALIZERS,  DataSerializerEntry.class, MIN_SERIALIZER_ID, MAX_SERIALIZER_ID).disableSaving().disableOverrides().addCallback(SerializerCallbacks.INSTANCE).create();
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int max)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).addCallback(new NamespacedWrapper.Factory<T>());
    }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int min, int max)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setIDRange(min, max).addCallback(new NamespacedWrapper.Factory<T>());
    }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceLocation name, Class<T> type, int max, ResourceLocation _default)
    {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).addCallback(new NamespacedDefaultedWrapper.Factory<T>()).setDefaultKey(_default);
    }

    public static <V extends IForgeRegistryEntry<V>> RegistryNamespacedDefaultedByKey<V> getWrapperDefaulted(Class<V> cls)
    {
        IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString());
        @SuppressWarnings("unchecked")
        RegistryNamespacedDefaultedByKey<V> ret = reg.getSlaveMap(NamespacedDefaultedWrapper.Factory.ID, NamespacedDefaultedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString());
        return ret;
    }

    public static <V extends IForgeRegistryEntry<V>> RegistryNamespaced<V> getWrapper(Class<V> cls)
    {
        IForgeRegistry<V> reg = RegistryManager.ACTIVE.getRegistry(cls);
        Validate.notNull(reg, "Attempted to get vanilla wrapper for unknown registry: " + cls.toString());
        @SuppressWarnings("unchecked")
        RegistryNamespaced<V> ret = reg.getSlaveMap(NamespacedWrapper.Factory.ID, NamespacedWrapper.class);
        Validate.notNull(ret, "Attempted to get vanilla wrapper for registry created incorrectly: " + cls.toString());
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static Map<Block,Item> getBlockItemMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Item.class).getSlaveMap(BLOCK_TO_ITEM, Map.class);
    }

    @SuppressWarnings("unchecked")
    public static ObjectIntIdentityMap<IBlockState> getBlockStateIDMap()
    {
        return RegistryManager.ACTIVE.getRegistry(Block.class).getSlaveMap(BLOCKSTATE_TO_ID, ObjectIntIdentityMap.class);
    }

    @SuppressWarnings("unchecked")
    public static Map<DataSerializer<?>, DataSerializerEntry> getSerializerMap()
    {
        return RegistryManager.ACTIVE.getRegistry(DataSerializerEntry.class).getSlaveMap(SERIALIZER_TO_ENTRY, Map.class);
    }

    public static <K extends IForgeRegistryEntry<K>> K register_impl(K value)
    {
        Validate.notNull(value, "Attempted to register a null object");
        Validate.notNull(value.getRegistryName(), String.format("Attempt to register object without having set a registry name %s (type %s)", value, value.getClass().getName()));
        final IForgeRegistry<K> registry = RegistryManager.ACTIVE.getRegistry(value.getRegistryType());
        Validate.notNull(registry, "Attempted to registry object without creating registry first: " + value.getRegistryType().getName());
        registry.register(value);
        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void vanillaSnapshot()
    {
        LOGGER.debug(REGISTRIES, "Creating vanilla freeze snapshot");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.VANILLA, clazz, true);
        }
        RegistryManager.VANILLA.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.VANILLA.registries.forEach(LOCK_VANILLA);
        RegistryManager.ACTIVE.registries.forEach(LOCK_VANILLA);
        LOGGER.debug(REGISTRIES, "Vanilla freeze snapshot created");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void freezeData()
    {
        LOGGER.debug(REGISTRIES, "Freezing registries");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.ACTIVE, RegistryManager.FROZEN, clazz, true);
        }
        RegistryManager.FROZEN.registries.forEach((name, reg) ->
        {
            reg.validateContent(name);
            reg.freeze();
        });
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.freeze();
            reg.bake();
            reg.dump(name);
        });

        // the id mapping is finalized, no ids actually changed but this is a good place to tell everyone to 'bake' their stuff.
        fireRemapEvent(ImmutableMap.of(), true);

        LOGGER.debug(REGISTRIES, "All registries frozen");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void revertToFrozen()
    {
        if (RegistryManager.FROZEN.registries.isEmpty())
        {
            LOGGER.warn(REGISTRIES, "Can't revert to frozen GameData state without freezing first.");
            return;
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        LOGGER.debug(REGISTRIES, "Reverting to frozen data state.");
        for (Map.Entry<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(r.getKey());
            loadRegistry(r.getKey(), RegistryManager.FROZEN, RegistryManager.ACTIVE, clazz, true);
        }
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.bake());
        // the id mapping has reverted, fire remap events for those that care about id changes
        fireRemapEvent(ImmutableMap.of(), true);

        ObjectHolderRegistry.applyObjectHolders();
        // the id mapping has reverted, ensure we sync up the object holders
        LOGGER.debug(REGISTRIES, "Frozen state restored.");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void revert(RegistryManager state, ResourceLocation registry, boolean lock)
    {
        LOGGER.debug(REGISTRIES, "Reverting {} to {}", registry, state.getName());
        final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(registry);
        loadRegistry(registry, state, RegistryManager.ACTIVE, clazz, lock);
        LOGGER.debug(REGISTRIES, "Reverting complete");
    }

    //Lets us clear the map so we can rebuild it.
    static class ClearableObjectIntIdentityMap<I> extends ObjectIntIdentityMap<I>
    {
        void clear()
        {
            this.identityMap.clear();
            this.objectList.clear();
            this.nextId = 0;
        }

        void remove(I key)
        {
            Integer prev = this.identityMap.remove(key);
            if (prev != null)
            {
                this.objectList.set(prev, null);
            }
        }
    }

    private static class BlockCallbacks implements IForgeRegistry.AddCallback<Block>, IForgeRegistry.ClearCallback<Block>, IForgeRegistry.BakeCallback<Block>, IForgeRegistry.CreateCallback<Block>, IForgeRegistry.DummyFactory<Block>
    {
        static final BlockCallbacks INSTANCE = new BlockCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<Block> owner, RegistryManager stage, int id, Block block, @Nullable Block oldBlock)
        {
            if (oldBlock != null)
            {
                StateContainer<Block, IBlockState> oldContainer = oldBlock.getStateContainer();
                StateContainer<Block, IBlockState> newContainer = block.getStateContainer();
                ImmutableList<IBlockState> oldValidStates = oldContainer.getValidStates();
                ImmutableList<IBlockState> newValidStates = newContainer.getValidStates();

                // Test vanilla blockstates, if the number matches, make sure they also match in their string representations
                if (block.getRegistryName().getNamespace().equals("minecraft") && (
                        oldValidStates.size() != newValidStates.size() ||
                        !Streams.zip(oldValidStates.stream().map(Object::toString),
                                    newValidStates.stream().map(Object::toString),
                                    String::equals).allMatch(v -> v)))
                {
                    String oldSequence = oldContainer.getProperties().stream()
                            .map(s -> String.format("%s={%s}", s.getName(),
                                    s.getAllowedValues().stream().map(Object::toString).collect(Collectors.joining( "," ))))
                            .collect(Collectors.joining(";"));
                    String newSequence = newContainer.getProperties().stream()
                            .map(s -> String.format("%s={%s}", s.getName(),
                                    s.getAllowedValues().stream().map(Object::toString).collect(Collectors.joining( "," ))))
                            .collect(Collectors.joining(";"));

                    LOGGER.error(REGISTRIES,()-> new AdvancedLogMessageAdapter(sb-> {
                        sb.append("Registry replacements for vanilla block '").append(block.getRegistryName()).
                                append("' must not change the number or order of blockstates.\n");
                        sb.append("\tOld: ").append(oldSequence).append('\n');
                        sb.append("\tNew: ").append(newSequence);
                    }));
                    throw new RuntimeException("Invalid vanilla replacement. See log for details.");
                }
            }
/*

            if (oldBlock != null)
            {
                for (IBlockState state : oldBlock.getBlockState().getValidStates())
                {
                    blockstateMap.remove(state);
                }
            }

            if ("minecraft:tripwire".equals(block.getRegistryName().toString())) //Tripwire is crap so we have to special case whee!
            {
                for (int meta = 0; meta < 15; meta++)
                    blockstateMap.put(block.getStateFromMeta(meta), id << 4 | meta);
            }

            //So, due to blocks having more in-world states then metadata allows, we have to turn the map into a semi-milti-bimap.
            //We can do this however because the implementation of the map is last set wins. So we can add all states, then fix the meta bimap.
            //Multiple states -> meta. But meta to CORRECT state.

            final boolean[] usedMeta = new boolean[16]; //Hold a list of known meta from all states.
            for (IBlockState state : block.getBlockState().getValidStates())
            {
                final int meta = block.getMetaFromState(state);
                blockstateMap.put(state, id << 4 | meta); //Add ALL the things!
                usedMeta[meta] = true;
            }

            for (int meta = 0; meta < 16; meta++)
            {
                if (block.getClass() == BlockObserver.class)
                    continue; //Observers are bad and have non-cyclical states. So we HAVE to use the vanilla logic above.
                if (usedMeta[meta])
                    blockstateMap.put(block.getStateFromMeta(meta), id << 4 | meta); // Put the CORRECT thing!
            }

            if (oldBlock != null)
            {
                @SuppressWarnings("unchecked")
                BiMap<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
                Item item = blockToItem.get(oldBlock);
                if (item != null)
                    blockToItem.forcePut(block, item);
            }
*/
        }

        @Override
        public void onClear(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            final ClearableObjectIntIdentityMap<IBlockState> idMap = new ClearableObjectIntIdentityMap<IBlockState>()
            {
                @Override
                public int get(IBlockState key)
                {
                    Integer integer = (Integer)this.identityMap.get(key);
                    // There are some cases where this map is queried to serialize a state that is valid,
                    //but somehow not in this list, so attempt to get real metadata. Doing this hear saves us 7 patches
                    //if (integer == null && key != null)
                    //    integer = this.identityMap.get(key.getBlock().getStateFromMeta(key.getBlock().getMetaFromState(key)));
                    return integer == null ? -1 : integer.intValue();
                }
            };
            owner.setSlaveMap(BLOCKSTATE_TO_ID, idMap);
            owner.setSlaveMap(BLOCK_TO_ITEM, Maps.newHashMap());
        }

        @Override
        public Block createDummy(ResourceLocation key)
        {
            Block ret = new BlockDummyAir(Block.Properties.create(Material.AIR));
            GameData.forceRegistryName(ret, key);
            return ret;
        }

        @Override
        public void onBake(IForgeRegistryInternal<Block> owner, RegistryManager stage)
        {
            @SuppressWarnings("unchecked")
            ClearableObjectIntIdentityMap<IBlockState> blockstateMap = owner.getSlaveMap(BLOCKSTATE_TO_ID, ClearableObjectIntIdentityMap.class);

            for (Block block : owner)
            {
                for (IBlockState state : block.getStateContainer().getValidStates())
                {
                    blockstateMap.add(state);
                }
            }
        }

        private static class BlockDummyAir extends BlockAir //A named class so DummyBlockReplacementTest can detect if its a dummy
        {
            private BlockDummyAir(Block.Properties properties)
            {
                super(properties);
            }

            /**
             * Returns the unlocalized name of the block with "tile." appended to the front.
             */
            @Override
            public String getTranslationKey()
            {
                return "block.minecraft.air";
            }
        }
    }

    private static class ItemCallbacks implements IForgeRegistry.AddCallback<Item>, IForgeRegistry.ClearCallback<Item>, IForgeRegistry.CreateCallback<Item>
    {
        static final ItemCallbacks INSTANCE = new ItemCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<Item> owner, RegistryManager stage, int id, Item item, @Nullable Item oldItem)
        {
            if (oldItem instanceof ItemBlock)
            {
                @SuppressWarnings("unchecked")
                BiMap<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, BiMap.class);
                blockToItem.remove(((ItemBlock)oldItem).getBlock());
            }
            if (item instanceof ItemBlock)
            {
                @SuppressWarnings("unchecked")
                Map<Block, Item> blockToItem = owner.getSlaveMap(BLOCK_TO_ITEM, Map.class);
                ((ItemBlock)item).addToBlockToItemMap(blockToItem, item);
            }
        }

        @Override
        public void onClear(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            owner.getSlaveMap(BLOCK_TO_ITEM, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<Item> owner, RegistryManager stage)
        {
            // We share the blockItem map between items and blocks registries
            Map<?, ?> map = stage.getRegistry(BLOCKS).getSlaveMap(BLOCK_TO_ITEM, Map.class);
            owner.setSlaveMap(BLOCK_TO_ITEM, map);
        }
    }

/*
    private static class RecipeCallbacks implements IForgeRegistry.ValidateCallback<IRecipe>, IForgeRegistry.MissingFactory<IRecipe>
    {
        static final RecipeCallbacks INSTANCE = new RecipeCallbacks();

        @Override
        public void onValidate(IForgeRegistryInternal<IRecipe> owner, RegistryManager stage, int id, ResourceLocation key, IRecipe obj)
        {
            if (stage != RegistryManager.ACTIVE) return;
            // verify the recipe output yields a registered item
            Item item = obj.getRecipeOutput().getItem();
            if (!stage.getRegistry(Item.class).containsValue(item))
            {
                throw new IllegalStateException(String.format("Recipe %s (%s) produces unregistered item %s (%s)", key, obj, item.getRegistryName(), item));
            }
        }

        @Override
        public IRecipe createMissing(ResourceLocation key, boolean isNetwork)
        {
            return isNetwork ? new DummyRecipe().setRegistryName(key) : null;
        }
        private static class DummyRecipe implements IRecipe
        {
            private static ItemStack result = new ItemStack(Items.DIAMOND, 64);
            private ResourceLocation name;

            @Override
            public IRecipe setRegistryName(ResourceLocation name) {
                this.name = name;
                return this;
            }
            @Override public ResourceLocation getRegistryName() { return name; }
            @Override public Class<IRecipe> getRegistryType() { return IRecipe.class; }
            @Override public boolean matches(InventoryCrafting inv, World worldIn) { return false; } //dirt?
            @Override public ItemStack getCraftingResult(InventoryCrafting inv) { return result; }
            @Override public boolean canFit(int width, int height) { return false; }
            @Override public ItemStack getRecipeOutput() { return result; }
            @Override public boolean isDynamic() { return true; }
        }
    }


    private static ForgeRegistry<EntityEntry> entityRegistry;
    public static ForgeRegistry<EntityEntry> getEntityRegistry() { return entityRegistry; }
    public static void registerEntity(int id, ResourceLocation key, Class<? extends Entity> clazz, String oldName)
    {
        RegistryNamespaced<ResourceLocation, EntityEntry> reg = getWrapper(EntityEntry.class);
        reg.register(id, key, new EntityEntry(clazz, oldName));
    }

    private static class EntityCallbacks implements IForgeRegistry.AddCallback<EntityEntry>
    {
        static final EntityCallbacks INSTANCE = new EntityCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<EntityEntry> owner, RegistryManager stage, int id, EntityEntry entry, @Nullable EntityEntry oldEntry)
        {
            if (entry instanceof EntityEntryBuilder.BuiltEntityEntry)
            {
                ((EntityEntryBuilder.BuiltEntityEntry) entry).addedToRegistry();
            }
            if (entry.getEgg() != null)
                EntityList.ENTITY_EGGS.put(entry.getRegistryName(), entry.getEgg());
            }
        }
    }
*/

    private static class SerializerCallbacks implements IForgeRegistry.AddCallback<DataSerializerEntry>, IForgeRegistry.ClearCallback<DataSerializerEntry>, IForgeRegistry.CreateCallback<DataSerializerEntry>
    {
        static final SerializerCallbacks INSTANCE = new SerializerCallbacks();

        @Override
        public void onAdd(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage, int id, DataSerializerEntry entry, @Nullable DataSerializerEntry oldEntry)
        {
            @SuppressWarnings("unchecked")
            Map<DataSerializer<?>, DataSerializerEntry> map = owner.getSlaveMap(SERIALIZER_TO_ENTRY, Map.class);
            if (oldEntry != null) map.remove(oldEntry.getSerializer());
            map.put(entry.getSerializer(), entry);
        }

        @Override
        public void onClear(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage)
        {
            owner.getSlaveMap(SERIALIZER_TO_ENTRY, Map.class).clear();
        }

        @Override
        public void onCreate(IForgeRegistryInternal<DataSerializerEntry> owner, RegistryManager stage)
        {
            owner.setSlaveMap(SERIALIZER_TO_ENTRY, new IdentityHashMap<>());
        }
    }

    private static <T extends IForgeRegistryEntry<T>> void loadRegistry(final ResourceLocation registryName, final RegistryManager from, final RegistryManager to, final Class<T> regType, boolean freeze)
    {
        ForgeRegistry<T> fromRegistry = from.getRegistry(registryName);
        if (fromRegistry == null)
        {
            ForgeRegistry<T> toRegistry = to.getRegistry(registryName);
            if (toRegistry == null)
            {
                throw new EnhancedRuntimeException("Could not find registry to load: " + registryName){
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void printStackTrace(WrappedPrintStream stream)
                    {
                        stream.println("Looking For: " + registryName);
                        stream.println("Found From:");
                        for (ResourceLocation name : from.registries.keySet())
                            stream.println("  " + name);
                        stream.println("Found To:");
                        for (ResourceLocation name : to.registries.keySet())
                            stream.println("  " + name);
                    }
                };
            }
            // We found it in to, so lets trust to's state...
            // This happens when connecting to a server that doesn't have this registry.
            // Such as a 1.8.0 Forge server with 1.8.8+ Forge.
            // We must however, re-fire the callbacks as some internal data may be corrupted {potions}
            //TODO: With my rework of how registries add callbacks are done.. I don't think this is necessary.
            //fire addCallback for each entry
        }
        else
        {
            ForgeRegistry<T> toRegistry = to.getRegistry(registryName, from);
            toRegistry.sync(registryName, fromRegistry);
            if (freeze)
                toRegistry.isFrozen = true;
        }
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Multimap<ResourceLocation, ResourceLocation> injectSnapshot(Map<ResourceLocation, ForgeRegistry.Snapshot> snapshot, boolean injectFrozenData, boolean isLocalWorld)
    {
        LOGGER.info(REGISTRIES, "Injecting existing registry data into this {} instance", EffectiveSide.get());
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.validateContent(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.dump(name));
        RegistryManager.ACTIVE.registries.forEach((name, reg) -> reg.resetDelegates());

        if (isLocalWorld)
        {
            List<ResourceLocation> missingRegs = snapshot.keySet().stream().filter(name -> !RegistryManager.ACTIVE.registries.containsKey(name)).collect(Collectors.toList());
            if (missingRegs.size() > 0)
            {
                String text = "Forge Mod Loader detected missing/unknown registrie(s).\n\n" +
                        "There are " + missingRegs.size() + " missing registries in this save.\n" +
                        "If you continue the missing registries will get removed.\n" +
                        "This may cause issues, it is advised that you create a world backup before continuing.\n\n" +
                        "Missing Registries:\n";

                for (ResourceLocation s : missingRegs)
                    text += s.toString() + "\n";

                if (!StartupQuery.confirm(text))
                    StartupQuery.abort();
            }
        }

        RegistryManager STAGING = new RegistryManager("STAGING");

        final Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps = Maps.newHashMap();
        final LinkedHashMap<ResourceLocation, Map<ResourceLocation, Integer>> missing = Maps.newLinkedHashMap();
        // Load the snapshot into the "STAGING" registry
        snapshot.forEach((key, value) ->
        {
            final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(key);
            remaps.put(key, Maps.newLinkedHashMap());
            missing.put(key, Maps.newHashMap());
            loadPersistentDataToStagingRegistry(RegistryManager.ACTIVE, STAGING, remaps.get(key), missing.get(key), key, value, clazz);
        });

        snapshot.forEach((key, value) ->
        {
            value.dummied.forEach(dummy ->
            {
                Map<ResourceLocation, Integer> m = missing.get(key);
                ForgeRegistry<?> reg = STAGING.getRegistry(key);

                // Currently missing locally, we just inject and carry on
                if (m.containsKey(dummy))
                {
                    if (reg.markDummy(dummy, m.get(dummy)))
                        m.remove(dummy);
                }
                else if (isLocalWorld)
                {
                   LOGGER.debug(REGISTRIES,"Registry {}: Resuscitating dummy entry {}", key, dummy);
                }
                else
                {
                    // The server believes this is a dummy block identity, but we seem to have one locally. This is likely a conflict
                    // in mod setup - Mark this entry as a dummy
                    int id = reg.getID(dummy);
                    LOGGER.warn(REGISTRIES, "Registry {}: The ID {} @ {} is currently locally mapped - it will be replaced with a dummy for this session", dummy, key, id);
                    reg.markDummy(dummy, id);
                }
            });
        });

        int count = missing.values().stream().mapToInt(Map::size).sum();
        if (count > 0)
        {
            LOGGER.debug(REGISTRIES,"There are {} mappings missing - attempting a mod remap", count);
            Multimap<ResourceLocation, ResourceLocation> defaulted = ArrayListMultimap.create();
            Multimap<ResourceLocation, ResourceLocation> failed = ArrayListMultimap.create();

            missing.entrySet().stream().filter(e -> e.getValue().size() > 0).forEach(m ->
            {
                ResourceLocation name = m.getKey();
                ForgeRegistry<?> reg = STAGING.getRegistry(name);
                RegistryEvent.MissingMappings<?> event = reg.getMissingEvent(name, m.getValue());
                MinecraftForge.EVENT_BUS.post(event);

                List<MissingMappings.Mapping<?>> lst = event.getAllMappings().stream().filter(e -> e.getAction() == MissingMappings.Action.DEFAULT).sorted((a, b) -> a.toString().compareTo(b.toString())).collect(Collectors.toList());
                if (!lst.isEmpty())
                {
                    LOGGER.error(REGISTRIES,()->new AdvancedLogMessageAdapter(sb->{
                       sb.append("Unidentified mapping from registry ").append(name).append('\n');
                       lst.forEach(map->sb.append('\t').append(map.key).append(": ").append(map.id).append('\n'));
                    }));
                }
                event.getAllMappings().stream().filter(e -> e.getAction() == MissingMappings.Action.FAIL).forEach(fail -> failed.put(name, fail.key));

                final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
                processMissing(clazz, name, STAGING, event, m.getValue(), remaps.get(name), defaulted.get(name), failed.get(name), !isLocalWorld);
            });

            if (!defaulted.isEmpty() && !isLocalWorld)
                return defaulted;

            if (!defaulted.isEmpty())
            {
                StringBuilder buf = new StringBuilder();
                buf.append("Forge Mod Loader detected missing registry entries.\n\n")
                   .append("There are ").append(defaulted.size()).append(" missing entries in this save.\n")
                   .append("If you continue the missing entries will get removed.\n")
                   .append("A world backup will be automatically created in your saves directory.\n\n");

                defaulted.asMap().forEach((name, entries) ->
                {
                    buf.append("Missing ").append(name).append(":\n");
                    entries.forEach(rl -> buf.append("    ").append(rl).append("\n"));
                });

                boolean confirmed = StartupQuery.confirm(buf.toString());
                if (!confirmed)
                    StartupQuery.abort();

/*
                try
                {
                    String skip = System.getProperty("fml.doNotBackup");
                    if (skip == null || !"true".equals(skip))
                    {
                        ZipperUtil.backupWorld();
                    }
                    else
                    {
                        for (int x = 0; x < 10; x++)
                            LOGGER.error(GD, "!!!!!!!!!! UPDATING WORLD WITHOUT DOING BACKUP !!!!!!!!!!!!!!!!");
                    }
                }
                catch (IOException e)
                {
                    StartupQuery.notify("The world backup couldn't be created.\n\n" + e);
                    StartupQuery.abort();
                }
*/
            }

            if (!defaulted.isEmpty())
            {
                if (isLocalWorld)
                    LOGGER.error(REGISTRIES, "There are unidentified mappings in this world - we are going to attempt to process anyway");
            }

        }

        if (injectFrozenData)
        {
            // If we're loading from disk, we can actually substitute air in the block map for anything that is otherwise "missing". This keeps the reference in the map, in case
            // the block comes back later
            missing.forEach((name, m) ->
            {
                ForgeRegistry<?> reg = STAGING.getRegistry(name);
                m.forEach((rl, id) -> reg.markDummy(rl, id));
            });


            // If we're loading up the world from disk, we want to add in the new data that might have been provisioned by mods
            // So we load it from the frozen persistent registry
            RegistryManager.ACTIVE.registries.forEach((name, reg) ->
            {
                final Class<? extends IForgeRegistryEntry> clazz = RegistryManager.ACTIVE.getSuperType(name);
                loadFrozenDataToStagingRegistry(STAGING, name, remaps.get(name), clazz);
            });
        }

        // Validate that all the STAGING data is good
        STAGING.registries.forEach((name, reg) -> reg.validateContent(name));

        // Load the STAGING registry into the ACTIVE registry
        //for (Map.Entry<ResourceLocation, IForgeRegistry<? extends IForgeRegistryEntry<?>>> r : RegistryManager.ACTIVE.registries.entrySet())
        RegistryManager.ACTIVE.registries.forEach((key, value) ->
        {
            final Class<? extends IForgeRegistryEntry> registrySuperType = RegistryManager.ACTIVE.getSuperType(key);
            loadRegistry(key, STAGING, RegistryManager.ACTIVE, registrySuperType, true);
        });

        RegistryManager.ACTIVE.registries.forEach((name, reg) -> {
            reg.bake();

            // Dump the active registry
            reg.dump(name);
        });

        // Tell mods that the ids have changed
        fireRemapEvent(remaps, false);

        // The id map changed, ensure we apply object holders
        ObjectHolderRegistry.applyObjectHolders();

        // Return an empty list, because we're good
        return ArrayListMultimap.create();
    }

    private static void fireRemapEvent(final Map<ResourceLocation, Map<ResourceLocation, Integer[]>> remaps, final boolean isFreezing) {
        MinecraftForge.EVENT_BUS.post(new FMLModIdMappingEvent(remaps, isFreezing));
    }

    //Has to be split because of generics, Yay!
    private static <T extends IForgeRegistryEntry<T>> void loadPersistentDataToStagingRegistry(RegistryManager pool, RegistryManager to, Map<ResourceLocation, Integer[]> remaps, Map<ResourceLocation, Integer> missing, ResourceLocation name, ForgeRegistry.Snapshot snap, Class<T> regType)
    {
        ForgeRegistry<T> active  = pool.getRegistry(name);
        if (active == null)
            return; // We've already asked the user if they wish to continue. So if the reg isnt found just assume the user knows and accepted it.
        ForgeRegistry<T> _new = to.getRegistry(name, RegistryManager.ACTIVE);
        snap.aliases.forEach(_new::addAlias);
        snap.blocked.forEach(_new::block);
        // Load current dummies BEFORE the snapshot is loaded so that add() will remove from the list.
        snap.dummied.forEach(_new::addDummy);
        _new.loadIds(snap.ids, snap.overrides, missing, remaps, active, name);
    }

    //Another bouncer for generic reasons
    @SuppressWarnings("unchecked")
    private static <T extends IForgeRegistryEntry<T>> void processMissing(Class<T> clazz, ResourceLocation name, RegistryManager STAGING, MissingMappings<?> e, Map<ResourceLocation, Integer> missing, Map<ResourceLocation, Integer[]> remaps, Collection<ResourceLocation> defaulted, Collection<ResourceLocation> failed, boolean injectNetworkDummies)
    {
        List<MissingMappings.Mapping<T>> mappings = ((MissingMappings<T>)e).getAllMappings();
        ForgeRegistry<T> active = RegistryManager.ACTIVE.getRegistry(name);
        ForgeRegistry<T> staging = STAGING.getRegistry(name);
        staging.processMissingEvent(name, active, mappings, missing, remaps, defaulted, failed, injectNetworkDummies);
    }

    private static <T extends IForgeRegistryEntry<T>> void loadFrozenDataToStagingRegistry(RegistryManager STAGING, ResourceLocation name, Map<ResourceLocation, Integer[]> remaps, Class<T> clazz)
    {
        ForgeRegistry<T> frozen = RegistryManager.FROZEN.getRegistry(name);
        ForgeRegistry<T> newRegistry = STAGING.getRegistry(name, RegistryManager.FROZEN);
        Map<ResourceLocation, Integer> _new = Maps.newHashMap();
        frozen.getKeys().stream().filter(key -> !newRegistry.containsKey(key)).forEach(key -> _new.put(key, frozen.getID(key)));
        newRegistry.loadIds(_new, frozen.getOverrideOwners(), Maps.newLinkedHashMap(), remaps, frozen, name);
    }

    public static void fireCreateRegistryEvents()
    {
        MinecraftForge.EVENT_BUS.post(new RegistryEvent.NewRegistry());
    }

    public static void fireCreateRegistryEvents(final LifecycleEventProvider lifecycleEventProvider, final Consumer<LifecycleEventProvider> eventDispatcher) {
        final RegistryEvent.NewRegistry newRegistryEvent = new RegistryEvent.NewRegistry();
        lifecycleEventProvider.setCustomEventSupplier(()->newRegistryEvent);
        eventDispatcher.accept(lifecycleEventProvider);
    }


    public static void fireRegistryEvents(Predicate<ResourceLocation> filter, final LifecycleEventProvider lifecycleEventProvider, final Consumer<LifecycleEventProvider> eventDispatcher)
    {
        List<ResourceLocation> keys = Lists.newArrayList(RegistryManager.ACTIVE.registries.keySet());
        keys.sort((o1, o2) -> String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2)));

        //Move Blocks to first, and Items to second.
        keys.remove(BLOCKS);
        keys.remove(ITEMS);

        keys.add(0, BLOCKS);
        keys.add(1, ITEMS);
        for (int i = 0, keysSize = keys.size(); i < keysSize; i++) {
            final ResourceLocation rl = keys.get(i);
            if (!filter.test(rl)) continue;
            ForgeRegistry<?> reg = RegistryManager.ACTIVE.getRegistry(rl);
            reg.unfreeze();
            final RegistryEvent.Register<?> registerEvent = reg.getRegisterEvent(rl);
            lifecycleEventProvider.setCustomEventSupplier(() -> registerEvent);
            lifecycleEventProvider.changeProgression(LifecycleEventProvider.LifecycleEvent.Progression.STAY);
            if (i==keysSize-1) lifecycleEventProvider.changeProgression(LifecycleEventProvider.LifecycleEvent.Progression.NEXT);
            eventDispatcher.accept(lifecycleEventProvider);
            reg.freeze();
            LOGGER.debug(REGISTRIES,"Applying holder lookups: {}", rl.toString());
            ObjectHolderRegistry.applyObjectHolders(rl::equals);
            LOGGER.debug(REGISTRIES,"Holder lookups applied: {}", rl.toString());
        }
    }

    /**
     * @deprecated Use {@link #checkPrefix(String, boolean)}.
     */
    @Deprecated
    public static ResourceLocation checkPrefix(String name)
    {
        return checkPrefix(name, true);
    }

    /**
     * Check a name for a domain prefix, and if not present infer it from the
     * current active mod container.
     * 
     * @param name          The name or resource location
     * @param warnOverrides If true, logs a warning if domain differs from that of
     *                      the currently currently active mod container
     * 
     * @return The {@link ResourceLocation} with given or inferred domain
     */
    public static ResourceLocation checkPrefix(String name, boolean warnOverrides)
    {
        int index = name.lastIndexOf(':');
        String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
        name = index == -1 ? name : name.substring(index + 1);
        String prefix = ModLoadingContext.get().getActiveContainer().getNamespace();
        if (warnOverrides && !oldPrefix.equals(prefix) && oldPrefix.length() > 0)
        {
            LogManager.getLogger().info("Potentially Dangerous alternative prefix `{}` for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", oldPrefix, name, prefix);
            prefix = oldPrefix;
        }
        return new ResourceLocation(prefix, name);
    }

    private static Field regName;
    private static void forceRegistryName(IForgeRegistryEntry<?> entry, ResourceLocation name)
    {
        if (regName == null)
        {
            try
            {
                regName = ForgeRegistryEntry.class.getDeclaredField("registryName");
                regName.setAccessible(true);
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                LOGGER.error(REGISTRIES, "Could not get `registryName` field from IForgeRegistryEntry.Impl", e);
                throw new RuntimeException(e);
            }
        }
        try
        {
            regName.set(entry, name);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            LOGGER.error(REGISTRIES,"Could not set `registryName` field in IForgeRegistryEntry.Impl to `{}`", name.toString(), e);
            throw new RuntimeException(e);
        }

    }
}