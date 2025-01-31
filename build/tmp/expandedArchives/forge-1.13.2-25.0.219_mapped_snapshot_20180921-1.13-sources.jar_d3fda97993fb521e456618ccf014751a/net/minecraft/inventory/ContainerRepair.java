package net.minecraft.inventory;

import java.util.Map;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContainerRepair extends Container {
   private static final Logger LOGGER = LogManager.getLogger();
   /** Here comes out item you merged and/or renamed. */
   private final IInventory outputSlot = new InventoryCraftResult();
   /** The 2slots where you put your items in that you want to merge and/or rename. */
   private final IInventory inputSlots = new InventoryBasic(new TextComponentString("Repair"), 2) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         ContainerRepair.this.onCraftMatrixChanged(this);
      }
   };
   private final World world;
   private final BlockPos pos;
   /** The maximum cost of repairing/renaming in the anvil. */
   public int maximumCost;
   /** determined by damage of input item and stackSize of repair materials */
   public int materialCost;
   private String repairedItemName;
   /** The player that has this container open. */
   private final EntityPlayer player;

   @OnlyIn(Dist.CLIENT)
   public ContainerRepair(InventoryPlayer playerInventory, World worldIn, EntityPlayer player) {
      this(playerInventory, worldIn, BlockPos.ORIGIN, player);
   }

   public ContainerRepair(InventoryPlayer playerInventory, final World worldIn, final BlockPos blockPosIn, EntityPlayer player) {
      this.pos = blockPosIn;
      this.world = worldIn;
      this.player = player;
      this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlot(new Slot(this.outputSlot, 2, 134, 47) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return false;
         }

         /**
          * Return whether this slot's stack can be taken from this slot.
          */
         public boolean canTakeStack(EntityPlayer playerIn) {
            return (playerIn.abilities.isCreativeMode || playerIn.experienceLevel >= ContainerRepair.this.maximumCost) && ContainerRepair.this.maximumCost > 0 && this.getHasStack();
         }

         public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
            if (!thePlayer.abilities.isCreativeMode) {
               thePlayer.addExperienceLevel(-ContainerRepair.this.maximumCost);
            }

            float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(thePlayer, stack, ContainerRepair.this.inputSlots.getStackInSlot(0), ContainerRepair.this.inputSlots.getStackInSlot(1));

            ContainerRepair.this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
            if (ContainerRepair.this.materialCost > 0) {
               ItemStack itemstack = ContainerRepair.this.inputSlots.getStackInSlot(1);
               if (!itemstack.isEmpty() && itemstack.getCount() > ContainerRepair.this.materialCost) {
                  itemstack.shrink(ContainerRepair.this.materialCost);
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, itemstack);
               } else {
                  ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
               }
            } else {
               ContainerRepair.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);
            }

            ContainerRepair.this.maximumCost = 0;
            IBlockState iblockstate1 = worldIn.getBlockState(blockPosIn);
            if (!worldIn.isRemote) {
               if (!thePlayer.abilities.isCreativeMode && iblockstate1.isIn(BlockTags.ANVIL) && thePlayer.getRNG().nextFloat() < breakChance) {
                  IBlockState iblockstate = BlockAnvil.damage(iblockstate1);
                  if (iblockstate == null) {
                     worldIn.removeBlock(blockPosIn);
                     worldIn.playEvent(1029, blockPosIn, 0);
                  } else {
                     worldIn.setBlockState(blockPosIn, iblockstate, 2);
                     worldIn.playEvent(1030, blockPosIn, 0);
                  }
               } else {
                  worldIn.playEvent(1030, blockPosIn, 0);
               }
            }

            return stack;
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
      }

   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      super.onCraftMatrixChanged(inventoryIn);
      if (inventoryIn == this.inputSlots) {
         this.updateRepairOutput();
      }

   }

   /**
    * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
    */
   public void updateRepairOutput() {
      ItemStack itemstack = this.inputSlots.getStackInSlot(0);
      this.maximumCost = 1;
      int i = 0;
      int j = 0;
      int k = 0;
      if (itemstack.isEmpty()) {
         this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
         this.maximumCost = 0;
      } else {
         ItemStack itemstack1 = itemstack.copy();
         ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
         j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
         this.materialCost = 0;
         boolean flag = false;

         if (!itemstack2.isEmpty()) {
            if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, itemstack, itemstack2, outputSlot, repairedItemName, j)) return;
            flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).isEmpty();
            if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
               int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               if (l2 <= 0) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }

               int i3;
               for(i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                  int j3 = itemstack1.getDamage() - l2;
                  itemstack1.setDamage(j3);
                  ++i;
                  l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
               }

               this.materialCost = i3;
            } else {
               if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }

               if (itemstack1.isDamageable() && !flag) {
                  int l = itemstack.getMaxDamage() - itemstack.getDamage();
                  int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
                  int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                  int k1 = l + j1;
                  int l1 = itemstack1.getMaxDamage() - k1;
                  if (l1 < 0) {
                     l1 = 0;
                  }

                  if (l1 < itemstack1.getDamage()) {
                     itemstack1.setDamage(l1);
                     i += 2;
                  }
               }

               Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
               boolean flag2 = false;
               boolean flag3 = false;

               for(Enchantment enchantment1 : map1.keySet()) {
                  if (enchantment1 != null) {
                     int i2 = map.containsKey(enchantment1) ? map.get(enchantment1) : 0;
                     int j2 = map1.get(enchantment1);
                     j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                     boolean flag1 = enchantment1.canApply(itemstack);
                     if (this.player.abilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                        flag1 = true;
                     }

                     for(Enchantment enchantment : map.keySet()) {
                        if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                           flag1 = false;
                           ++i;
                        }
                     }

                     if (!flag1) {
                        flag3 = true;
                     } else {
                        flag2 = true;
                        if (j2 > enchantment1.getMaxLevel()) {
                           j2 = enchantment1.getMaxLevel();
                        }

                        map.put(enchantment1, j2);
                        int k3 = 0;
                        switch(enchantment1.getRarity()) {
                        case COMMON:
                           k3 = 1;
                           break;
                        case UNCOMMON:
                           k3 = 2;
                           break;
                        case RARE:
                           k3 = 4;
                           break;
                        case VERY_RARE:
                           k3 = 8;
                        }

                        if (flag) {
                           k3 = Math.max(1, k3 / 2);
                        }

                        i += k3 * j2;
                        if (itemstack.getCount() > 1) {
                           i = 40;
                        }
                     }
                  }
               }

               if (flag3 && !flag2) {
                  this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                  this.maximumCost = 0;
                  return;
               }
            }
         }

         if (StringUtils.isBlank(this.repairedItemName)) {
            if (itemstack.hasDisplayName()) {
               k = 1;
               i += k;
               itemstack1.clearCustomName();
            }
         } else if (!this.repairedItemName.equals(itemstack.getDisplayName().getString())) {
            k = 1;
            i += k;
            itemstack1.setDisplayName(new TextComponentString(this.repairedItemName));
         }
         if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

         this.maximumCost = j + i;
         if (i <= 0) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (k == i && k > 0 && this.maximumCost >= 40) {
            this.maximumCost = 39;
         }

         if (this.maximumCost >= 40 && !this.player.abilities.isCreativeMode) {
            itemstack1 = ItemStack.EMPTY;
         }

         if (!itemstack1.isEmpty()) {
            int k2 = itemstack1.getRepairCost();
            if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
               k2 = itemstack2.getRepairCost();
            }

            if (k != i || k == 0) {
               k2 = k2 * 2 + 1;
            }

            itemstack1.setRepairCost(k2);
            EnchantmentHelper.setEnchantments(map, itemstack1);
         }

         this.outputSlot.setInventorySlotContents(0, itemstack1);
         this.detectAndSendChanges();
      }
   }

   public void addListener(IContainerListener listener) {
      super.addListener(listener);
      listener.sendWindowProperty(this, 0, this.maximumCost);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateProgressBar(int id, int data) {
      if (id == 0) {
         this.maximumCost = data;
      }

   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(EntityPlayer playerIn) {
      super.onContainerClosed(playerIn);
      if (!this.world.isRemote) {
         this.clearContainer(playerIn, this.world, this.inputSlots);
      }
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(EntityPlayer playerIn) {
      if (!this.world.getBlockState(this.pos).isIn(BlockTags.ANVIL)) {
         return false;
      } else {
         return playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
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
         if (index == 2) {
            if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index != 0 && index != 1) {
            if (index >= 3 && index < 39 && !this.mergeItemStack(itemstack1, 0, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
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

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }

   /**
    * used by the Anvil GUI to update the Item Name being typed by the player
    */
   public void updateItemName(String newName) {
      this.repairedItemName = newName;
      if (this.getSlot(2).getHasStack()) {
         ItemStack itemstack = this.getSlot(2).getStack();
         if (StringUtils.isBlank(newName)) {
            itemstack.clearCustomName();
         } else {
            itemstack.setDisplayName(new TextComponentString(this.repairedItemName));
         }
      }

      this.updateRepairOutput();
   }
}