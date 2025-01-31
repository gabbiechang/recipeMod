package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityVex extends EntityMob {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityVex.class, DataSerializers.BYTE);
   private EntityLiving owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean limitedLifespan;
   private int limitedLifeTicks;

   public EntityVex(World worldIn) {
      super(EntityType.VEX, worldIn);
      this.isImmuneToFire = true;
      this.moveHelper = new EntityVex.AIMoveControl(this);
      this.setSize(0.4F, 0.8F);
      this.experienceValue = 3;
   }

   /**
    * Tries to move the entity towards the specified location.
    */
   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.noClip = true;
      super.tick();
      this.noClip = false;
      this.setNoGravity(true);
      if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.attackEntityFrom(DamageSource.STARVE, 1.0F);
      }

   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(4, new EntityVex.AIChargeAttack());
      this.tasks.addTask(8, new EntityVex.AIMoveRandom());
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityVex.class));
      this.targetTasks.addTask(2, new EntityVex.AICopyOwnerTarget(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(VEX_FLAGS, (byte)0);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.hasKey("BoundX")) {
         this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
      }

      if (compound.hasKey("LifeTicks")) {
         this.setLimitedLife(compound.getInt("LifeTicks"));
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (this.boundOrigin != null) {
         compound.setInt("BoundX", this.boundOrigin.getX());
         compound.setInt("BoundY", this.boundOrigin.getY());
         compound.setInt("BoundZ", this.boundOrigin.getZ());
      }

      if (this.limitedLifespan) {
         compound.setInt("LifeTicks", this.limitedLifeTicks);
      }

   }

   public EntityLiving getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
      this.boundOrigin = boundOriginIn;
   }

   private boolean getVexFlag(int mask) {
      int i = this.dataManager.get(VEX_FLAGS);
      return (i & mask) != 0;
   }

   private void setVexFlag(int mask, boolean value) {
      int i = this.dataManager.get(VEX_FLAGS);
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(VEX_FLAGS, (byte)(i & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setCharging(boolean charging) {
      this.setVexFlag(1, charging);
   }

   public void setOwner(EntityLiving ownerIn) {
      this.owner = ownerIn;
   }

   public void setLimitedLife(int limitedLifeTicksIn) {
      this.limitedLifespan = true;
      this.limitedLifeTicks = limitedLifeTicksIn;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_VEX_HURT;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_VEX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.setEquipmentBasedOnDifficulty(difficulty);
      this.setEnchantmentBasedOnDifficulty(difficulty);
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   /**
    * Gives armor or weapon for entity based on given DifficultyInstance
    */
   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.0F);
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         if (EntityVex.this.getAttackTarget() != null && !EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.rand.nextInt(7) == 0) {
            return EntityVex.this.getDistanceSq(EntityVex.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.isCharging() && EntityVex.this.getAttackTarget() != null && EntityVex.this.getAttackTarget().isAlive();
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityVex.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
         EntityVex.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
         EntityVex.this.setCharging(true);
         EntityVex.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         EntityVex.this.setCharging(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         EntityLivingBase entitylivingbase = EntityVex.this.getAttackTarget();
         if (EntityVex.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
            EntityVex.this.attackEntityAsMob(entitylivingbase);
            EntityVex.this.setCharging(false);
         } else {
            double d0 = EntityVex.this.getDistanceSq(entitylivingbase);
            if (d0 < 9.0D) {
               Vec3d vec3d = entitylivingbase.getEyePosition(1.0F);
               EntityVex.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
         }

      }
   }

   class AICopyOwnerTarget extends EntityAITarget {
      public AICopyOwnerTarget(EntityCreature creature) {
         super(creature, false);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return EntityVex.this.owner != null && EntityVex.this.owner.getAttackTarget() != null && this.isSuitableTarget(EntityVex.this.owner.getAttackTarget(), false);
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         EntityVex.this.setAttackTarget(EntityVex.this.owner.getAttackTarget());
         super.startExecuting();
      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityVex vex) {
         super(vex);
      }

      public void tick() {
         if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            double d0 = this.posX - EntityVex.this.posX;
            double d1 = this.posY - EntityVex.this.posY;
            double d2 = this.posZ - EntityVex.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityVex.this.getBoundingBox().getAverageEdgeLength()) {
               this.action = EntityMoveHelper.Action.WAIT;
               EntityVex.this.motionX *= 0.5D;
               EntityVex.this.motionY *= 0.5D;
               EntityVex.this.motionZ *= 0.5D;
            } else {
               EntityVex.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityVex.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityVex.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityVex.this.getAttackTarget() == null) {
                  EntityVex.this.rotationYaw = -((float)MathHelper.atan2(EntityVex.this.motionX, EntityVex.this.motionZ)) * (180F / (float)Math.PI);
                  EntityVex.this.renderYawOffset = EntityVex.this.rotationYaw;
               } else {
                  double d4 = EntityVex.this.getAttackTarget().posX - EntityVex.this.posX;
                  double d5 = EntityVex.this.getAttackTarget().posZ - EntityVex.this.posZ;
                  EntityVex.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                  EntityVex.this.renderYawOffset = EntityVex.this.rotationYaw;
               }
            }

         }
      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         this.setMutexBits(1);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return !EntityVex.this.getMoveHelper().isUpdating() && EntityVex.this.rand.nextInt(7) == 0;
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return false;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         BlockPos blockpos = EntityVex.this.getBoundOrigin();
         if (blockpos == null) {
            blockpos = new BlockPos(EntityVex.this);
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityVex.this.rand.nextInt(15) - 7, EntityVex.this.rand.nextInt(11) - 5, EntityVex.this.rand.nextInt(15) - 7);
            if (EntityVex.this.world.isAirBlock(blockpos1)) {
               EntityVex.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
               if (EntityVex.this.getAttackTarget() == null) {
                  EntityVex.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}