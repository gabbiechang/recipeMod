package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelRenderer {
   /** The size of the texture file's width in pixels. */
   public float textureWidth = 64.0F;
   /** The size of the texture file's height in pixels. */
   public float textureHeight = 32.0F;
   /** The X offset into the texture used for displaying this model */
   private int textureOffsetX;
   /** The Y offset into the texture used for displaying this model */
   private int textureOffsetY;
   public float rotationPointX;
   public float rotationPointY;
   public float rotationPointZ;
   public float rotateAngleX;
   public float rotateAngleY;
   public float rotateAngleZ;
   private boolean compiled;
   /** The GL display list rendered by the Tessellator for this model */
   private int displayList;
   public boolean mirror;
   public boolean showModel = true;
   /** Hides the model. */
   public boolean isHidden;
   public List<ModelBox> cubeList = Lists.newArrayList();
   public List<ModelRenderer> childModels;
   public final String boxName;
   private final ModelBase baseModel;
   public float offsetX;
   public float offsetY;
   public float offsetZ;

   public ModelRenderer(ModelBase model, String boxNameIn) {
      this.baseModel = model;
      model.boxList.add(this);
      this.boxName = boxNameIn;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   public ModelRenderer(ModelBase model) {
      this(model, (String)null);
   }

   public ModelRenderer(ModelBase model, int texOffX, int texOffY) {
      this(model);
      this.setTextureOffset(texOffX, texOffY);
   }

   /**
    * Sets the current box's rotation points and rotation angles to another box.
    */
   public void addChild(ModelRenderer renderer) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(renderer);
   }

   public ModelRenderer setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
      this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
      this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F)).setBoxName(partName));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth, boolean mirrored) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, mirrored));
      return this;
   }

   /**
    * Creates a textured box.
    */
   public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor));
   }

   public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scaleFactor, boolean mirrorIn) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scaleFactor, mirrorIn));
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public void render(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            GlStateManager.translatef(this.offsetX, this.offsetY, this.offsetZ);
            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int k = 0; k < this.childModels.size(); ++k) {
                        this.childModels.get(k).render(scale);
                     }
                  }
               } else {
                  GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                  GlStateManager.callList(this.displayList);
                  if (this.childModels != null) {
                     for(int j = 0; j < this.childModels.size(); ++j) {
                        this.childModels.get(j).render(scale);
                     }
                  }

                  GlStateManager.translatef(-this.rotationPointX * scale, -this.rotationPointY * scale, -this.rotationPointZ * scale);
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }

               GlStateManager.callList(this.displayList);
               if (this.childModels != null) {
                  for(int i = 0; i < this.childModels.size(); ++i) {
                     this.childModels.get(i).render(scale);
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.translatef(-this.offsetX, -this.offsetY, -this.offsetZ);
         }
      }
   }

   public void renderWithRotation(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            GlStateManager.callList(this.displayList);
            GlStateManager.popMatrix();
         }
      }
   }

   /**
    * Allows the changing of Angles after a box has been rendered
    */
   public void postRender(float scale) {
      if (!this.isHidden) {
         if (this.showModel) {
            if (!this.compiled) {
               this.compileDisplayList(scale);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
               if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                  GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               }
            } else {
               GlStateManager.translatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
               if (this.rotateAngleZ != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
               }

               if (this.rotateAngleY != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
               }

               if (this.rotateAngleX != 0.0F) {
                  GlStateManager.rotatef(this.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
               }
            }

         }
      }
   }

   /**
    * Compiles a GL display list for this model
    */
   private void compileDisplayList(float scale) {
      this.displayList = GLAllocation.generateDisplayLists(1);
      GlStateManager.newList(this.displayList, 4864);
      BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();

      for(int i = 0; i < this.cubeList.size(); ++i) {
         this.cubeList.get(i).render(bufferbuilder, scale);
      }

      GlStateManager.endList();
      this.compiled = true;
   }

   /**
    * Returns the model renderer with the new texture parameters.
    */
   public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = (float)textureWidthIn;
      this.textureHeight = (float)textureHeightIn;
      return this;
   }
}