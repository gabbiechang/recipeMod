package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityGhast extends EntityFlying implements IMob {
   private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityGhast.class, DataSerializers.BOOLEAN);
   /** The explosion radius of spawned fireballs. */
   private int explosionStrength = 1;

   public EntityGhast(World worldIn) {
      super(EntityType.GHAST, worldIn);
      this.setSize(4.0F, 4.0F);
      this.isImmuneToFire = true;
      this.experienceValue = 5;
      this.moveHelper = new EntityGhast.GhastMoveHelper(this);
   }

   protected void initEntityAI() {
      this.tasks.addTask(5, new EntityGhast.AIRandomFly(this));
      this.tasks.addTask(7, new EntityGhast.AILookAround(this));
      this.tasks.addTask(7, new EntityGhast.AIFireballAttack(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAttacking() {
      return this.dataManager.get(ATTACKING);
   }

   public void setAttacking(boolean attacking) {
      this.dataManager.set(ATTACKING, attacking);
   }

   public int getFireballStrength() {
      return this.explosionStrength;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.remove();
      }

   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getImmediateSource() instanceof EntityLargeFireball && source.getTrueSource() instanceof EntityPlayer) {
         super.attackEntityFrom(source, 1000.0F);
         return true;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(ATTACKING, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GHAST_DEATH;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_GHAST;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 10.0F;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      return this.rand.nextInt(20) == 0 && super.canSpawn(worldIn, p_205020_2_) && worldIn.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("ExplosionPower", this.explosionStrength);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("ExplosionPower", 99)) {
         this.explosionStrength = compound.getInt("ExplosionPower");
      }

   }

   public float getEyeHeight() {
      return 2.6F;
   }

   static class AIFireballAttack extends EntityAIBase {
      private final EntityGhast parentEntity;
      public int attackTimer;

      public AIFireballAttack(EntityGhast ghast) {
         this.parentEntity = ghast;
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.attackTimer = 0;
      }

      /**
       * Reset the task's internal state. Called when this task is interrupted by another one
       */
      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
         double d0 = 64.0D;
         if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(entitylivingbase)) {
            World world = this.parentEntity.world;
            ++this.attackTimer;
            if (this.attackTimer == 10) {
               world.playEvent((EntityPlayer)null, 1015, new BlockPos(this.parentEntity), 0);
            }

            if (this.attackTimer == 20) {
               double d1 = 4.0D;
               Vec3d vec3d = this.parentEntity.getLook(1.0F);
               double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
               double d3 = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (0.5D + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F));
               double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
               world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.parentEntity), 0);
               EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.parentEntity, d2, d3, d4);
               entitylargefireball.explosionPower = this.parentEntity.getFireballStrength();
               entitylargefireball.posX = this.parentEntity.posX + vec3d.x * 4.0D;
               entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D;
               entitylargefireball.posZ = this.parentEntity.posZ + vec3d.z * 4.0D;
               world.spawnEntity(entitylargefireball);
               this.attackTimer = -40;
            }
         } else if (this.attackTimer > 0) {
            --this.attackTimer;
         }

         this.parentEntity.setAttacking(this.attackTimer > 10);
      }
   }

   static class AILookAround extends EntityAIBase {
      private final EntityGhast parentEntity;

      public AILookAround(EntityGhast ghast) {
         this.parentEntity = ghast;
         this.setMutexBits(2);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return true;
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.parentEntity.getAttackTarget() == null) {
            this.parentEntity.rotationYaw = -((float)MathHelper.atan2(this.parentEntity.motionX, this.parentEntity.motionZ)) * (180F / (float)Math.PI);
            this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
         } else {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            double d0 = 64.0D;
            if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D) {
               double d1 = entitylivingbase.posX - this.parentEntity.posX;
               double d2 = entitylivingbase.posZ - this.parentEntity.posZ;
               this.parentEntity.rotationYaw = -((float)MathHelper.atan2(d1, d2)) * (180F / (float)Math.PI);
               this.parentEntity.renderYawOffset = this.parentEntity.rotationYaw;
            }
         }

      }
   }

   static class AIRandomFly extends EntityAIBase {
      private final EntityGhast parentEntity;

      public AIRandomFly(EntityGhast ghast) {
         this.parentEntity = ghast;
         this.setMutexBits(1);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();
         if (!entitymovehelper.isUpdating()) {
            return true;
         } else {
            double d0 = entitymovehelper.getX() - this.parentEntity.posX;
            double d1 = entitymovehelper.getY() - this.parentEntity.posY;
            double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         return false;
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         Random random = this.parentEntity.getRNG();
         double d0 = this.parentEntity.posX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d1 = this.parentEntity.posY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double d2 = this.parentEntity.posZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.parentEntity.getMoveHelper().setMoveTo(d0, d1, d2, 1.0D);
      }
   }

   static class GhastMoveHelper extends EntityMoveHelper {
      private final EntityGhast parentEntity;
      private int courseChangeCooldown;

      public GhastMoveHelper(EntityGhast ghast) {
         super(ghast);
         this.parentEntity = ghast;
      }

      public void tick() {
         if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            double d0 = this.posX - this.parentEntity.posX;
            double d1 = this.posY - this.parentEntity.posY;
            double d2 = this.posZ - this.parentEntity.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (this.courseChangeCooldown-- <= 0) {
               this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
               d3 = (double)MathHelper.sqrt(d3);
               if (this.isNotColliding(this.posX, this.posY, this.posZ, d3)) {
                  this.parentEntity.motionX += d0 / d3 * 0.1D;
                  this.parentEntity.motionY += d1 / d3 * 0.1D;
                  this.parentEntity.motionZ += d2 / d3 * 0.1D;
               } else {
                  this.action = EntityMoveHelper.Action.WAIT;
               }
            }

         }
      }

      /**
       * Checks if entity bounding box is not colliding with terrain
       */
      private boolean isNotColliding(double x, double y, double z, double p_179926_7_) {
         double d0 = (x - this.parentEntity.posX) / p_179926_7_;
         double d1 = (y - this.parentEntity.posY) / p_179926_7_;
         double d2 = (z - this.parentEntity.posZ) / p_179926_7_;
         AxisAlignedBB axisalignedbb = this.parentEntity.getBoundingBox();

         for(int i = 1; (double)i < p_179926_7_; ++i) {
            axisalignedbb = axisalignedbb.offset(d0, d1, d2);
            if (!this.parentEntity.world.isCollisionBoxesEmpty(this.parentEntity, axisalignedbb)) {
               return false;
            }
         }

         return true;
      }
   }
}