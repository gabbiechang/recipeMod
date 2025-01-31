package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryCraftResult implements IInventory, IRecipeHolder {
   /** A list of one item containing the result of the crafting formula */
   private final NonNullList<ItemStack> stackResult = NonNullList.withSize(1, ItemStack.EMPTY);
   private IRecipe recipeUsed;

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stackResult) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return this.stackResult.get(0);
   }

   public ITextComponent getName() {
      return new TextComponentString("Result");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stackResult.set(0, stack);
   }

   /**
    * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
    */
   public int getInventoryStackLimit() {
      return 64;
   }

   /**
    * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
    * hasn't changed and skip it.
    */
   public void markDirty() {
   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
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
      this.stackResult.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe recipe) {
      this.recipeUsed = recipe;
   }

   @Nullable
   public IRecipe getRecipeUsed() {
      return this.recipeUsed;
   }
}