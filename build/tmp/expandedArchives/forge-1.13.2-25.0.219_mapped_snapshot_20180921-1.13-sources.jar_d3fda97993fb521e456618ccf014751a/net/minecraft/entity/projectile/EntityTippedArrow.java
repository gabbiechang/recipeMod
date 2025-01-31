package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTippedArrow extends EntityArrow {
   private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityTippedArrow.class, DataSerializers.VARINT);
   private PotionType potion = PotionTypes.EMPTY;
   private final Set<PotionEffect> customPotionEffects = Sets.newHashSet();
   private boolean fixedColor;

   public EntityTippedArrow(World worldIn) {
      super(EntityType.ARROW, worldIn);
   }

   public EntityTippedArrow(World worldIn, double x, double y, double z) {
      super(EntityType.ARROW, x, y, z, worldIn);
   }

   public EntityTippedArrow(World worldIn, EntityLivingBase shooter) {
      super(EntityType.ARROW, shooter, worldIn);
   }

   public void setPotionEffect(ItemStack stack) {
      if (stack.getItem() == Items.TIPPED_ARROW) {
         this.potion = PotionUtils.getPotionFromItem(stack);
         Collection<PotionEffect> collection = PotionUtils.getFullEffectsFromItem(stack);
         if (!collection.isEmpty()) {
            for(PotionEffect potioneffect : collection) {
               this.customPotionEffects.add(new PotionEffect(potioneffect));
            }
         }

         int i = getCustomColor(stack);
         if (i == -1) {
            this.refreshColor();
         } else {
            this.setFixedColor(i);
         }
      } else if (stack.getItem() == Items.ARROW) {
         this.potion = PotionTypes.EMPTY;
         this.customPotionEffects.clear();
         this.dataManager.set(COLOR, -1);
      }

   }

   public static int getCustomColor(ItemStack p_191508_0_) {
      NBTTagCompound nbttagcompound = p_191508_0_.getTag();
      return nbttagcompound != null && nbttagcompound.contains("CustomPotionColor", 99) ? nbttagcompound.getInt("CustomPotionColor") : -1;
   }

   private void refreshColor() {
      this.fixedColor = false;
      this.dataManager.set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
   }

   public void addEffect(PotionEffect effect) {
      this.customPotionEffects.add(effect);
      this.getDataManager().set(COLOR, PotionUtils.getPotionColorFromEffectList(PotionUtils.mergeEffects(this.potion, this.customPotionEffects)));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(COLOR, -1);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.inGround) {
            if (this.timeInGround % 5 == 0) {
               this.spawnPotionParticles(1);
            }
         } else {
            this.spawnPotionParticles(2);
         }
      } else if (this.inGround && this.timeInGround != 0 && !this.customPotionEffects.isEmpty() && this.timeInGround >= 600) {
         this.world.setEntityState(this, (byte)0);
         this.potion = PotionTypes.EMPTY;
         this.customPotionEffects.clear();
         this.dataManager.set(COLOR, -1);
      }

   }

   private void spawnPotionParticles(int particleCount) {
      int i = this.getColor();
      if (i != -1 && particleCount > 0) {
         double d0 = (double)(i >> 16 & 255) / 255.0D;
         double d1 = (double)(i >> 8 & 255) / 255.0D;
         double d2 = (double)(i >> 0 & 255) / 255.0D;

         for(int j = 0; j < particleCount; ++j) {
            this.world.spawnParticle(Particles.ENTITY_EFFECT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
         }

      }
   }

   public int getColor() {
      return this.dataManager.get(COLOR);
   }

   private void setFixedColor(int p_191507_1_) {
      this.fixedColor = true;
      this.dataManager.set(COLOR, p_191507_1_);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (this.potion != PotionTypes.EMPTY && this.potion != null) {
         compound.setString("Potion", IRegistry.field_212621_j.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         compound.setInt("Color", this.getColor());
      }

      if (!this.customPotionEffects.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(PotionEffect potioneffect : this.customPotionEffects) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         compound.setTag("CustomPotionEffects", nbttaglist);
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotionTypeFromNBT(compound);
      }

      for(PotionEffect potioneffect : PotionUtils.getFullEffectsFromTag(compound)) {
         this.addEffect(potioneffect);
      }

      if (compound.contains("Color", 99)) {
         this.setFixedColor(compound.getInt("Color"));
      } else {
         this.refreshColor();
      }

   }

   protected void arrowHit(EntityLivingBase living) {
      super.arrowHit(living);

      for(PotionEffect potioneffect : this.potion.getEffects()) {
         living.addPotionEffect(new PotionEffect(potioneffect.getPotion(), Math.max(potioneffect.getDuration() / 8, 1), potioneffect.getAmplifier(), potioneffect.isAmbient(), potioneffect.doesShowParticles()));
      }

      if (!this.customPotionEffects.isEmpty()) {
         for(PotionEffect potioneffect1 : this.customPotionEffects) {
            living.addPotionEffect(potioneffect1);
         }
      }

   }

   protected ItemStack getArrowStack() {
      if (this.customPotionEffects.isEmpty() && this.potion == PotionTypes.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.addPotionToItemStack(itemstack, this.potion);
         PotionUtils.appendEffects(itemstack, this.customPotionEffects);
         if (this.fixedColor) {
            itemstack.getOrCreateTag().setInt("CustomPotionColor", this.getColor());
         }

         return itemstack;
      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 0) {
         int i = this.getColor();
         if (i != -1) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < 20; ++j) {
               this.world.spawnParticle(Particles.ENTITY_EFFECT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
            }
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }
}