package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class BrewedPotionTrigger implements ICriterionTrigger<BrewedPotionTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("brewed_potion");
   private final Map<PlayerAdvancements, BrewedPotionTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (brewedpotiontrigger$listeners == null) {
         brewedpotiontrigger$listeners = new BrewedPotionTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, brewedpotiontrigger$listeners);
      }

      brewedpotiontrigger$listeners.addListener(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (brewedpotiontrigger$listeners != null) {
         brewedpotiontrigger$listeners.removeListener(listener);
         if (brewedpotiontrigger$listeners.isEmpty()) {
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
   public BrewedPotionTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      PotionType potiontype = null;
      if (json.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(JsonUtils.getString(json, "potion"));
         if (!IRegistry.field_212621_j.func_212607_c(resourcelocation)) {
            throw new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         }

         potiontype = IRegistry.field_212621_j.get(resourcelocation);
      }

      return new BrewedPotionTrigger.Instance(potiontype);
   }

   public void trigger(EntityPlayerMP player, PotionType potionIn) {
      BrewedPotionTrigger.Listeners brewedpotiontrigger$listeners = this.listeners.get(player.getAdvancements());
      if (brewedpotiontrigger$listeners != null) {
         brewedpotiontrigger$listeners.trigger(potionIn);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final PotionType potion;

      public Instance(@Nullable PotionType potion) {
         super(BrewedPotionTrigger.ID);
         this.potion = potion;
      }

      public static BrewedPotionTrigger.Instance brewedPotion() {
         return new BrewedPotionTrigger.Instance((PotionType)null);
      }

      public boolean test(PotionType potion) {
         return this.potion == null || this.potion == potion;
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.potion != null) {
            jsonobject.addProperty("potion", IRegistry.field_212621_j.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<BrewedPotionTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void removeListener(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(PotionType potion) {
         List<ICriterionTrigger.Listener<BrewedPotionTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(potion)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<BrewedPotionTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}