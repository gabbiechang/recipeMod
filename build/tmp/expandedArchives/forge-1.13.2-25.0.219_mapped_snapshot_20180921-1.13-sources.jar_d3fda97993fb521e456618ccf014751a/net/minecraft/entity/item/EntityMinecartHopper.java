package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   /** Whether this hopper minecart is being blocked by an activator rail. */
   private boolean isBlocked = true;
   private int transferTicker = -1;
   private final BlockPos lastPosition = BlockPos.ORIGIN;

   public EntityMinecartHopper(World worldIn) {
      super(EntityType.HOPPER_MINECART, worldIn);
   }

   public EntityMinecartHopper(World worldIn, double x, double y, double z) {
      super(EntityType.HOPPER_MINECART, x, y, z, worldIn);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.HOPPER;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.HOPPER.getDefaultState();
   }

   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return 5;
   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      if (super.processInitialInteract(player, hand)) return true;
      if (!this.world.isRemote) {
         player.displayGUIChest(this);
      }

      return true;
   }

   /**
    * Called every tick the minecart is on an activator rail.
    */
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      boolean flag = !receivingPower;
      if (flag != this.getBlocked()) {
         this.setBlocked(flag);
      }

   }

   /**
    * Get whether this hopper minecart is being blocked by an activator rail.
    */
   public boolean getBlocked() {
      return this.isBlocked;
   }

   /**
    * Set whether this hopper minecart is being blocked by an activator rail.
    */
   public void setBlocked(boolean p_96110_1_) {
      this.isBlocked = p_96110_1_;
   }

   /**
    * Returns the worldObj for this tileEntity.
    */
   public World getWorld() {
      return this.world;
   }

   /**
    * Gets the world X position for this hopper entity.
    */
   public double getXPos() {
      return this.posX;
   }

   /**
    * Gets the world Y position for this hopper entity.
    */
   public double getYPos() {
      return this.posY + 0.5D;
   }

   /**
    * Gets the world Z position for this hopper entity.
    */
   public double getZPos() {
      return this.posZ;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
         BlockPos blockpos = new BlockPos(this);
         if (blockpos.equals(this.lastPosition)) {
            --this.transferTicker;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.captureDroppedItems()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }

   }

   public boolean captureDroppedItems() {
      if (TileEntityHopper.pullItems(this)) {
         return true;
      } else {
         List<EntityItem> list = this.world.getEntitiesWithinAABB(EntityItem.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntitySelectors.IS_ALIVE);
         if (!list.isEmpty()) {
            TileEntityHopper.captureItem(this, list.get(0));
         }

         return false;
      }
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(Blocks.HOPPER);
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("TransferCooldown", this.transferTicker);
      compound.setBoolean("Enabled", this.isBlocked);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.transferTicker = compound.getInt("TransferCooldown");
      this.isBlocked = compound.hasKey("Enabled") ? compound.getBoolean("Enabled") : true;
   }

   /**
    * Sets the transfer ticker, used to determine the delay between transfers.
    */
   public void setTransferTicker(int p_98042_1_) {
      this.transferTicker = p_98042_1_;
   }

   /**
    * Returns whether the hopper cart can currently transfer an item.
    */
   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   public String getGuiID() {
      return "minecraft:hopper";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }
}