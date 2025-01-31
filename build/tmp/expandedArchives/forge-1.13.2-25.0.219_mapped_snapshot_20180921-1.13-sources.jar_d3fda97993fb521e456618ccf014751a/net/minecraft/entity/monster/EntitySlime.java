package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySlime extends EntityLiving implements IMob {
   private static final DataParameter<Integer> SLIME_SIZE = EntityDataManager.createKey(EntitySlime.class, DataSerializers.VARINT);
   public float squishAmount;
   public float squishFactor;
   public float prevSquishFactor;
   private boolean wasOnGround;

   protected EntitySlime(EntityType<?> p_i48552_1_, World p_i48552_2_) {
      super(p_i48552_1_, p_i48552_2_);
      this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
   }

   public EntitySlime(World worldIn) {
      this(EntityType.SLIME, worldIn);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
      this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
      this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
      this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
      this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
      this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SLIME_SIZE, 1);
   }

   protected void setSlimeSize(int size, boolean resetHealth) {
      this.dataManager.set(SLIME_SIZE, size);
      this.setSize(0.51000005F * (float)size, 0.51000005F * (float)size);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(size * size));
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
      if (resetHealth) {
         this.setHealth(this.getMaxHealth());
      }

      this.experienceValue = size;
   }

   /**
    * Returns the size of the slime.
    */
   public int getSlimeSize() {
      return this.dataManager.get(SLIME_SIZE);
   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.setInt("Size", this.getSlimeSize() - 1);
      compound.setBoolean("wasOnGround", this.wasOnGround);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      int i = compound.getInt("Size");
      if (i < 0) {
         i = 0;
      }

      this.setSlimeSize(i + 1, false);
      this.wasOnGround = compound.getBoolean("wasOnGround");
   }

   public boolean isSmallSlime() {
      return this.getSlimeSize() <= 1;
   }

   protected IParticleData func_195404_m() {
      return Particles.ITEM_SLIME;
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0) {
         this.remove(); //Forge: Kill entity with notification to caps/subclasses.
      }

      this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
      this.prevSquishFactor = this.squishFactor;
      super.tick();
      if (this.onGround && !this.wasOnGround) {
         int i = this.getSlimeSize();

         if (spawnCustomParticles()) i = 0; // don't spawn particles if it's handled by the implementation itself
         for(int j = 0; j < i * 8; ++j) {
            float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
            float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
            World world = this.world;
            IParticleData iparticledata = this.func_195404_m();
            double d0 = this.posX + (double)f2;
            double d1 = this.posZ + (double)f3;
            world.spawnParticle(iparticledata, d0, this.getBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.squishAmount = -0.5F;
      } else if (!this.onGround && this.wasOnGround) {
         this.squishAmount = 1.0F;
      }

      this.wasOnGround = this.onGround;
      this.alterSquishAmount();
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.6F;
   }

   /**
    * Gets the amount of time the slime needs to wait between jumps.
    */
   protected int getJumpDelay() {
      return this.rand.nextInt(20) + 10;
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (SLIME_SIZE.equals(key)) {
         int i = this.getSlimeSize();
         this.setSize(0.51000005F * (float)i, 0.51000005F * (float)i);
         this.rotationYaw = this.rotationYawHead;
         this.renderYawOffset = this.rotationYawHead;
         if (this.isInWater() && this.rand.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.notifyDataManagerChange(key);
   }

   public EntityType<? extends EntitySlime> getType() {
      return (EntityType<? extends EntitySlime>) super.getType();
   }

   /**
    * Queues the entity for removal from the world on the next tick.
    */
   public void remove() {
      int i = this.getSlimeSize();
      if (!this.world.isRemote && i > 1 && this.getHealth() <= 0.0F) {
         int j = 2 + this.rand.nextInt(3);

         for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
            EntitySlime entityslime = this.getType().create(this.world);
            if (this.hasCustomName()) {
               entityslime.setCustomName(this.getCustomName());
            }

            if (this.isNoDespawnRequired()) {
               entityslime.enablePersistence();
            }

            entityslime.setSlimeSize(i / 2, true);
            entityslime.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
            this.world.spawnEntity(entityslime);
         }
      }

      super.remove();
   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
      super.applyEntityCollision(entityIn);
      if (entityIn instanceof EntityIronGolem && this.canDamagePlayer()) {
         this.dealDamage((EntityLivingBase)entityIn);
      }

   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (this.canDamagePlayer()) {
         this.dealDamage(entityIn);
      }

   }

   protected void dealDamage(EntityLivingBase entityIn) {
      int i = this.getSlimeSize();
      if (this.canEntityBeSeen(entityIn) && this.getDistanceSq(entityIn) < 0.6D * (double)i * 0.6D * (double)i && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength())) {
         this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.applyEnchantments(this, entityIn);
      }

   }

   public float getEyeHeight() {
      return 0.625F * this.height;
   }

   /**
    * Indicates weather the slime is able to damage the player (based upon the slime's size)
    */
   protected boolean canDamagePlayer() {
      return !this.isSmallSlime() && this.isServerWorld();
   }

   /**
    * Gets the amount of damage dealt to the player when "attacked" by the slime.
    */
   protected int getAttackStrength() {
      return this.getSlimeSize();
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_HURT_SMALL : SoundEvents.ENTITY_SLIME_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_DEATH_SMALL : SoundEvents.ENTITY_SLIME_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_SQUISH_SMALL : SoundEvents.ENTITY_SLIME_SQUISH;
   }

   protected Item getDropItem() {
      return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.getSlimeSize() == 1 ? LootTableList.ENTITIES_SLIME : LootTableList.EMPTY;
   }

   public boolean canSpawn(IWorld worldIn, boolean p_205020_2_) {
      BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
      if (worldIn.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, world)) {
         return false;
      } else {
         if (worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
            Biome biome = worldIn.getBiome(blockpos);
            if (biome == Biomes.SWAMP && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < worldIn.getCurrentMoonPhaseFactor() && worldIn.getLight(new BlockPos(this)) <= this.rand.nextInt(8)) {
               return super.canSpawn(worldIn, p_205020_2_);
            }

            ChunkPos chunkpos = new ChunkPos(blockpos);
            boolean flag = SharedSeedRandom.seedSlimeChunk(chunkpos.x, chunkpos.z, worldIn.getSeed(), 987234911L).nextInt(10) == 0;
            if (this.rand.nextInt(10) == 0 && flag && this.posY < 40.0D) {
               return super.canSpawn(worldIn, p_205020_2_);
            }
         }

         return false;
      }
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 0.4F * (float)this.getSlimeSize();
   }

   /**
    * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
    * use in wolves.
    */
   public int getVerticalFaceSpeed() {
      return 0;
   }

   /**
    * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
    */
   protected boolean makesSoundOnJump() {
      return this.getSlimeSize() > 0;
   }

   /**
    * Causes this entity to do an upwards motion (jumping).
    */
   protected void jump() {
      this.motionY = (double)0.42F;
      this.isAirBorne = true;
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      int i = this.rand.nextInt(3);
      if (i < 2 && this.rand.nextFloat() < 0.5F * difficulty.getClampedAdditionalDifficulty()) {
         ++i;
      }

      int j = 1 << i;
      this.setSlimeSize(j, true);
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   protected SoundEvent getJumpSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_SLIME_JUMP_SMALL : SoundEvents.ENTITY_SLIME_JUMP;
   }

   /**
    * Called when the slime spawns particles on landing, see onUpdate.
    * Return true to prevent the spawning of the default particles.
    */
   protected boolean spawnCustomParticles() { return false; }

   static class AISlimeAttack extends EntityAIBase {
      private final EntitySlime slime;
      private int growTieredTimer;

      public AISlimeAttack(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(2);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isAlive()) {
            return false;
         } else {
            return !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).abilities.disableDamage;
         }
      }

      /**
       * Execute a one shot task or start executing a continuous task
       */
      public void startExecuting() {
         this.growTieredTimer = 300;
         super.startExecuting();
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
         if (entitylivingbase == null) {
            return false;
         } else if (!entitylivingbase.isAlive()) {
            return false;
         } else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).abilities.disableDamage) {
            return false;
         } else {
            return --this.growTieredTimer > 0;
         }
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setDirection(this.slime.rotationYaw, this.slime.canDamagePlayer());
      }
   }

   static class AISlimeFaceRandom extends EntityAIBase {
      private final EntitySlime slime;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public AISlimeFaceRandom(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(2);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(MobEffects.LEVITATION));
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = 40 + this.slime.getRNG().nextInt(60);
            this.chosenDegrees = (float)this.slime.getRNG().nextInt(360);
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setDirection(this.chosenDegrees, false);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private final EntitySlime slime;

      public AISlimeFloat(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(5);
         ((PathNavigateGround)slimeIn.getNavigator()).setCanSwim(true);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         return this.slime.isInWater() || this.slime.isInLava();
      }

      /**
       * Keep ticking a continuous task that has already been started
       */
      public void tick() {
         if (this.slime.getRNG().nextFloat() < 0.8F) {
            this.slime.getJumpHelper().setJumping();
         }

         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.2D);
      }
   }

   static class AISlimeHop extends EntityAIBase {
      private final EntitySlime slime;

      public AISlimeHop(EntitySlime slimeIn) {
         this.slime = slimeIn;
         this.setMutexBits(5);
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
         ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
      }
   }

   static class SlimeMoveHelper extends EntityMoveHelper {
      private float yRot;
      private int jumpDelay;
      private final EntitySlime slime;
      private boolean isAggressive;

      public SlimeMoveHelper(EntitySlime slimeIn) {
         super(slimeIn);
         this.slime = slimeIn;
         this.yRot = 180.0F * slimeIn.rotationYaw / (float)Math.PI;
      }

      public void setDirection(float p_179920_1_, boolean p_179920_2_) {
         this.yRot = p_179920_1_;
         this.isAggressive = p_179920_2_;
      }

      public void setSpeed(double speedIn) {
         this.speed = speedIn;
         this.action = EntityMoveHelper.Action.MOVE_TO;
      }

      public void tick() {
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.yRot, 90.0F);
         this.entity.rotationYawHead = this.entity.rotationYaw;
         this.entity.renderYawOffset = this.entity.rotationYaw;
         if (this.action != EntityMoveHelper.Action.MOVE_TO) {
            this.entity.setMoveForward(0.0F);
         } else {
            this.action = EntityMoveHelper.Action.WAIT;
            if (this.entity.onGround) {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpHelper().setJumping();
                  if (this.slime.makesSoundOnJump()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.slime.moveStrafing = 0.0F;
                  this.slime.moveForward = 0.0F;
                  this.entity.setAIMoveSpeed(0.0F);
               }
            } else {
               this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue()));
            }

         }
      }
   }
}