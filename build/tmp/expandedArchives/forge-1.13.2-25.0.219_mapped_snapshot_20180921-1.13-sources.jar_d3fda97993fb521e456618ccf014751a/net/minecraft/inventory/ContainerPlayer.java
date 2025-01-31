package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerPlayer extends ContainerRecipeBook {
   private static final String[] field_200829_h = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
   private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
   /** The crafting matrix inventory. */
   public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
   public InventoryCraftResult craftResult = new InventoryCraftResult();
   /** Determines if inventory manipulation should be handled. */
   public boolean isLocalWorld;
   private final EntityPlayer player;

   public ContainerPlayer(InventoryPlayer playerInventory, boolean localWorld, EntityPlayer playerIn) {
      this.isLocalWorld = localWorld;
      this.player = playerIn;
      this.addSlot(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 154, 28));

      for(int i = 0; i < 2; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.addSlot(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
         }
      }

      for(int k = 0; k < 4; ++k) {
         final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
         this.addSlot(new Slot(playerInventory, 39 - k, 8, 8 + k * 18) {
            /**
             * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
             * the case of armor slots)
             */
            public int getSlotStackLimit() {
               return 1;
            }

            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(ItemStack stack) {
               return stack.canEquip(entityequipmentslot, player);
            }

            /**
             * Return whether this slot's stack can be taken from this slot.
             */
            public boolean canTakeStack(EntityPlayer playerIn) {
               ItemStack itemstack = this.getStack();
               return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
            }

            @Nullable
            @OnlyIn(Dist.CLIENT)
            public String getSlotTexture() {
               return ContainerPlayer.field_200829_h[entityequipmentslot.getIndex()];
            }
         });
      }

      for(int l = 0; l < 3; ++l) {
         for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
      }

      this.addSlot(new Slot(playerInventory, 40, 77, 62) {
         @Nullable
         @OnlyIn(Dist.CLIENT)
         public String getSlotTexture() {
            return "item/empty_armor_slot_shield";
         }
      });
   }

   public void func_201771_a(RecipeItemHelper p_201771_1_) {
      this.craftMatrix.fillStackedContents(p_201771_1_);
   }

   public void clear() {
      this.craftResult.clear();
      this.craftMatrix.clear();
   }

   public boolean matches(IRecipe p_201769_1_) {
      return p_201769_1_.matches(this.craftMatrix, this.player.world);
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      this.slotChangedCraftingGrid(this.player.world, this.player, this.craftMatrix, this.craftResult);
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      this.craftResult.clear();
      if (!playerIn.world.isRemote) {
         this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
      }
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
         if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index >= 1 && index < 5) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 5 && index < 9) {
            if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack()) {
            int i = 8 - entityequipmentslot.getIndex();
            if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45).getHasStack()) {
            if (!this.mergeItemStack(itemstack1, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 9 && index < 36) {
            if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 36 && index < 45) {
            if (!this.mergeItemStack(itemstack1, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 9, 45, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
         if (index == 0) {
            playerIn.dropItem(itemstack2, false);
         }
      }

      return itemstack;
   }

   /**
    * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
    * null for the initial slot that was double-clicked.
    */
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
   }

   public int getOutputSlot() {
      return 0;
   }

   public int getWidth() {
      return this.craftMatrix.getWidth();
   }

   public int getHeight() {
      return this.craftMatrix.getHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 5;
   }
}