package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySpider extends EntityMob {
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntitySpider.class, DataSerializers.BYTE);

   protected EntitySpider(EntityType<?> type, World p_i48550_2_) {
      super(type, p_i48550_2_);
      this.setSize(1.4F, 0.9F);
   }

   public EntitySpider(World worldIn) {
      this(EntityType.SPIDER, worldIn);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(4, new EntitySpider.AISpiderAttack(this));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(6, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntitySpider.AISpiderTarget<>(this, EntityPlayer.class));
      this.targetTasks.addTask(3, new EntitySpider.AISpiderTarget<>(this, EntityIronGolem.class));
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return (double)(this.height * 0.5F);
   }

   /**
    * Returns new PathNavigateGround instance
    */
   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateClimber(this, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CLIMBING, (byte)0);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.3F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SPIDER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SPIDER_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SPIDER;
   }

   /**
    * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
    * for AI reasons)
    */
   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   /**
    * Sets the Entity inside a web block.
    */
   public void setInWeb() {
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      if (potioneffectIn.getPotion() == MobEffects.POISON) {
          net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(this, potioneffectIn);
          net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
          return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
      }
      return super.isPotionApplicable(potioneffectIn);
   }

   /**
    * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
    * setBesideClimableBlock.
    */
   public boolean isBesideClimbableBlock() {
      return (this.dataManager.get(CLIMBING) & 1) != 0;
   }

   /**
    * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is
    * false.
    */
   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = this.dataManager.get(CLIMBING);
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, b0);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      if (this.world.rand.nextInt(100) == 0) {
         EntitySkeleton entityskeleton = new EntitySkeleton(this.world);
         entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
         entityskeleton.onInitialSpawn(difficulty, (IEntityLivingData)null, (NBTTagCompound)null);
         this.world.spawnEntity(entityskeleton);
         entityskeleton.startRiding(this);
      }

      if (entityLivingData == null) {
         entityLivingData = new EntitySpider.GroupData();
         if (this.world.getDifficulty() == EnumDifficulty.HARD && this.world.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty()) {
            ((EntitySpider.GroupData)entityLivingData).setRandomEffect(this.world.rand);
         }
      }

      if (entityLivingData instanceof EntitySpider.GroupData) {
         Potion potion = ((EntitySpider.GroupData)entityLivingData).effect;
         if (potion != null) {
            this.addPotionEffect(new PotionEffect(potion, Integer.MAX_VALUE));
         }
      }

      return entityLivingData;
   }

   public float getEyeHeight() {
      return 0.65F;
   }

   static class AISpiderAttack extends EntityAIAttackMelee {
      public AISpiderAttack(EntitySpider spider) {
         super(spider, 1.0D, true);
      }

      /**
       * Returns whether an in-progress EntityAIBase should continue executing
       */
      public boolean shouldContinueExecuting() {
         float f = this.attacker.getBrightness();
         if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
            this.attacker.setAttackTarget((EntityLivingBase)null);
            return false;
         } else {
            return super.shouldContinueExecuting();
         }
      }

      protected double getAttackReachSqr(EntityLivingBase attackTarget) {
         return (double)(4.0F + attackTarget.width);
      }
   }

   static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
      public AISpiderTarget(EntitySpider spider, Class<T> classTarget) {
         super(spider, classTarget, true);
      }

      /**
       * Returns whether the EntityAIBase should begin execution.
       */
      public boolean shouldExecute() {
         float f = this.taskOwner.getBrightness();
         return f >= 0.5F ? false : super.shouldExecute();
      }
   }

   public static class GroupData implements IEntityLivingData {
      public Potion effect;

      public void setRandomEffect(Random rand) {
         int i = rand.nextInt(5);
         if (i <= 1) {
            this.effect = MobEffects.SPEED;
         } else if (i <= 2) {
            this.effect = MobEffects.STRENGTH;
         } else if (i <= 3) {
            this.effect = MobEffects.REGENERATION;
         } else if (i <= 4) {
            this.effect = MobEffects.INVISIBILITY;
         }

      }
   }
}