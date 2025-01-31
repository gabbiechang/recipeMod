package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class TileEntityLockableLoot extends TileEntityLockable implements ILootContainer {
   protected ResourceLocation lootTable;
   protected long lootTableSeed;
   protected ITextComponent customName;

   protected TileEntityLockableLoot(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public static void setLootTable(IBlockReader reader, Random rand, BlockPos p_195479_2_, ResourceLocation lootTableIn) {
      TileEntity tileentity = reader.getTileEntity(p_195479_2_);
      if (tileentity instanceof TileEntityLockableLoot) {
         ((TileEntityLockableLoot)tileentity).setLootTable(lootTableIn, rand.nextLong());
      }

   }

   protected boolean checkLootAndRead(NBTTagCompound compound) {
      if (compound.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(compound.getString("LootTable"));
         this.lootTableSeed = compound.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean checkLootAndWrite(NBTTagCompound compound) {
      if (this.lootTable == null) {
         return false;
      } else {
         compound.setString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            compound.setLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void fillWithLoot(@Nullable EntityPlayer player) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         Random random;
         if (this.lootTableSeed == 0L) {
            random = new Random();
         } else {
            random = new Random(this.lootTableSeed);
         }

         LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)this.world);
         lootcontext$builder.withPosition(this.pos);
         lootcontext$builder.withPlayer(player);
         if (player != null) {
            lootcontext$builder.withLuck(player.getLuck());
         }

         loottable.fillInventory(this, random, lootcontext$builder.build());
      }

   }

   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(ResourceLocation lootTableIn, long seedIn) {
      this.lootTable = lootTableIn;
      this.lootTableSeed = seedIn;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.customName = name;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      return this.getItems().get(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      this.fillWithLoot((EntityPlayer)null);
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.getItems(), index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      this.fillWithLoot((EntityPlayer)null);
      return ItemStackHelper.getAndRemove(this.getItems(), index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
      this.fillWithLoot((EntityPlayer)null);
      this.getItems().set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(EntityPlayer player) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> itemsIn);
}