package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerBipedArmor extends LayerArmorBase<ModelBiped> {
   public LayerBipedArmor(RenderLivingBase<?> rendererIn) {
      super(rendererIn);
   }

   protected void initArmor() {
      this.modelLeggings = new ModelBiped(0.5F);
      this.modelArmor = new ModelBiped(1.0F);
   }

   protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
      this.setModelVisible(p_188359_1_);
      switch(slotIn) {
      case HEAD:
         p_188359_1_.bipedHead.showModel = true;
         p_188359_1_.bipedHeadwear.showModel = true;
         break;
      case CHEST:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightArm.showModel = true;
         p_188359_1_.bipedLeftArm.showModel = true;
         break;
      case LEGS:
         p_188359_1_.bipedBody.showModel = true;
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
         break;
      case FEET:
         p_188359_1_.bipedRightLeg.showModel = true;
         p_188359_1_.bipedLeftLeg.showModel = true;
      }

   }

   protected void setModelVisible(ModelBiped model) {
      model.setVisible(false);
   }
   
   @Override
   protected ModelBiped getArmorModelHook(net.minecraft.entity.EntityLivingBase entity, net.minecraft.item.ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }
}