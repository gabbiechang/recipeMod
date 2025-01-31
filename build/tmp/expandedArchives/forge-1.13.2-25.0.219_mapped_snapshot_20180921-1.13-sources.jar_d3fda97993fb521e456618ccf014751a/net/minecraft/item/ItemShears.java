package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   public ItemShears(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    */
   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (!worldIn.isRemote) {
         stack.damageItem(1, entityLiving);
      }

      Block block = state.getBlock();
      if (block instanceof net.minecraftforge.common.IShearable) return true;
      return !state.isIn(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.isIn(BlockTags.WOOL) ? super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) : true;
   }

   /**
    * Check whether this Item can harvest the given Block
    */
   public boolean canHarvestBlock(IBlockState blockIn) {
      Block block = blockIn.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      Block block = state.getBlock();
      if (block != Blocks.COBWEB && !state.isIn(BlockTags.LEAVES)) {
         return block.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(stack, state);
      } else {
         return 15.0F;
      }
   }


   /**
    * Returns true if the item can be used on the given entity, e.g. shears on sheep.
    */
   @Override
   public boolean itemInteractionForEntity(ItemStack itemstack, net.minecraft.entity.player.EntityPlayer player, EntityLivingBase entity, net.minecraft.util.EnumHand hand) {
      if (entity.world.isRemote)
         return false;
      if (entity instanceof net.minecraftforge.common.IShearable) {
         net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)entity;
         BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
         if (target.isShearable(itemstack, entity.world, pos)) {
            java.util.List<ItemStack> drops = target.onSheared(itemstack, entity.world, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));
            java.util.Random rand = new java.util.Random();
            for (ItemStack stack : drops) {
               net.minecraft.entity.item.EntityItem ent = entity.entityDropItem(stack, 1.0F);
               ent.motionY += rand.nextFloat() * 0.05F;
               ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
               ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
            }
            itemstack.damageItem(1, entity);
         }
         return true;
      }
      return false;
   }

   @Override
   public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
      if (player.world.isRemote || player.abilities.isCreativeMode)
         return false;
      Block block = player.world.getBlockState(pos).getBlock();
      if (block instanceof net.minecraftforge.common.IShearable) {
         net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable)block;
         if (target.isShearable(itemstack, player.world, pos)) {
            java.util.List<ItemStack> drops = target.onSheared(itemstack, player.world, pos,
                    net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));
            java.util.Random rand = new java.util.Random();
            for (ItemStack stack : drops) {
               float f = 0.7F;
               double d  = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               double d1 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               double d2 = (double)(rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
               net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(player.world, (double)pos.getX() + d, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
               entityitem.setDefaultPickupDelay();
               player.world.spawnEntity(entityitem);
            }
            itemstack.damageItem(1, player);
            player.addStat(net.minecraft.stats.StatList.BLOCK_MINED.get(block));
            return true;
         }
      }
      return false;
   }
}