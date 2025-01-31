package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class BredAnimalsTrigger implements ICriterionTrigger<BredAnimalsTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("bred_animals");
   private final Map<PlayerAdvancements, BredAnimalsTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (bredanimalstrigger$listeners == null) {
         bredanimalstrigger$listeners = new BredAnimalsTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, bredanimalstrigger$listeners);
      }

      bredanimalstrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (bredanimalstrigger$listeners != null) {
         bredanimalstrigger$listeners.remove(listener);
         if (bredanimalstrigger$listeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public BredAnimalsTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("parent"));
      EntityPredicate entitypredicate1 = EntityPredicate.deserialize(json.get("partner"));
      EntityPredicate entitypredicate2 = EntityPredicate.deserialize(json.get("child"));
      return new BredAnimalsTrigger.Instance(entitypredicate, entitypredicate1, entitypredicate2);
   }

   public void trigger(EntityPlayerMP player, EntityAnimal parent1, EntityAnimal parent2, @Nullable EntityAgeable child) {
      BredAnimalsTrigger.Listeners bredanimalstrigger$listeners = this.listeners.get(player.getAdvancements());
      if (bredanimalstrigger$listeners != null) {
         bredanimalstrigger$listeners.trigger(player, parent1, parent2, child);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final EntityPredicate parent;
      private final EntityPredicate partner;
      private final EntityPredicate child;

      public Instance(EntityPredicate parent, EntityPredicate partner, EntityPredicate child) {
         super(BredAnimalsTrigger.ID);
         this.parent = parent;
         this.partner = partner;
         this.child = child;
      }

      public static BredAnimalsTrigger.Instance func_203908_c() {
         return new BredAnimalsTrigger.Instance(EntityPredicate.ANY, EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public static BredAnimalsTrigger.Instance func_203909_a(EntityPredicate.Builder p_203909_0_) {
         return new BredAnimalsTrigger.Instance(p_203909_0_.build(), EntityPredicate.ANY, EntityPredicate.ANY);
      }

      public boolean test(EntityPlayerMP player, EntityAnimal parent1In, EntityAnimal parent2In, @Nullable EntityAgeable childIn) {
         if (!this.child.test(player, childIn)) {
            return false;
         } else {
            return this.parent.test(player, parent1In) && this.partner.test(player, parent2In) || this.parent.test(player, parent2In) && this.partner.test(player, parent1In);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("parent", this.parent.serialize());
         jsonobject.add("partner", this.partner.serialize());
         jsonobject.add("child", this.child.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<BredAnimalsTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(EntityPlayerMP player, EntityAnimal parent1, EntityAnimal parent2, @Nullable EntityAgeable child) {
         List<ICriterionTrigger.Listener<BredAnimalsTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(player, parent1, parent2, child)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<BredAnimalsTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}