package hx.minepainter.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import hx.minepainter.painting.ExpirablePool;
import hx.minepainter.sculpture.BlockSlice;
import hx.minepainter.sculpture.Sculpture;
import hx.minepainter.sculpture.SculptureBlock;
import hx.minepainter.sculpture.SculptureRenderBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DroppedSculptureRenderer implements IItemRenderer {
   SculptureRenderBlocks rb = new SculptureRenderBlocks();
   RenderItem renderItem = new RenderItem();
   ItemStack is;
   ExpirablePool renders = new ExpirablePool(12) {
      public void release(DroppedSculptureRenderer.CompiledRender v) {
         v.clear();
      }

      public DroppedSculptureRenderer.CompiledRender get() {
         return DroppedSculptureRenderer.this.new CompiledRender();
      }
   };

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return type == ItemRenderType.ENTITY;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      DroppedSculptureRenderer.CompiledRender cr = (DroppedSculptureRenderer.CompiledRender)this.renders.get(item);
      if (!cr.compiled(type)) {
         cr.compile(item.getTagCompound(), type, data);
      }

      TextureManager tm = Minecraft.getMinecraft().renderEngine;
      tm.bindTexture(TextureMap.locationBlocksTexture);
      GL11.glCallList(cr.glDispList);
   }

   private class CompiledRender {
      int glDispList;
      ItemRenderType type;
      Sculpture sculpture;

      private CompiledRender() {
         this.glDispList = -1;
         this.type = null;
         this.sculpture = new Sculpture();
      }

      public boolean compiled(ItemRenderType type) {
         return this.glDispList >= 0 && this.type == type;
      }

      public void clear() {
         if (this.glDispList >= 0) {
            GLAllocation.deleteDisplayLists(this.glDispList);
         }

      }

      public void compile(NBTTagCompound nbt, ItemRenderType type, Object... data) {
         this.type = type;
         this.sculpture.read(nbt);
         if (this.glDispList < 0) {
            this.glDispList = GLAllocation.generateDisplayLists(1);
         }

         if (DroppedSculptureRenderer.this.is == null) {
            DroppedSculptureRenderer.this.is = new ItemStack(ModMinePainter.sculpture.block);
         }

         GL11.glNewList(this.glDispList, 4864);
         TextureManager tm = Minecraft.getMinecraft().renderEngine;
         tm.bindTexture(TextureMap.locationBlocksTexture);
         SculptureBlock sb = (SculptureBlock)ModMinePainter.sculpture.block;
         if (type == ItemRenderType.INVENTORY) {
            RenderHelper.enableGUIStandardItemLighting();
         }

         for(int i = 0; i < 512; ++i) {
            int x = i >> 6 & 7;
            int y = i >> 3 & 7;
            int z = i >> 0 & 7;
            if (this.sculpture.getBlockAt(x, y, z, (BlockSlice)null) != Blocks.air) {
               sb.setCurrentBlock(this.sculpture.getBlockAt(x, y, z, (BlockSlice)null), this.sculpture.getMetaAt(x, y, z, (BlockSlice)null));
               sb.setBlockBounds((float)x / 8.0F, (float)y / 8.0F, (float)z / 8.0F, (float)(x + 1) / 8.0F, (float)(y + 1) / 8.0F, (float)(z + 1) / 8.0F);
               if (type == ItemRenderType.INVENTORY) {
                  GL11.glPushMatrix();
                  GL11.glEnable(3042);
                  OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                  GL11.glTranslatef(-2.0F, 3.0F, 47.0F);
                  GL11.glScalef(10.0F, 10.0F, 10.0F);
                  GL11.glTranslatef(1.0F, 0.5F, 1.0F);
                  GL11.glScalef(1.0F, 1.0F, -1.0F);
                  GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
                  GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                  GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                  DroppedSculptureRenderer.this.rb.cull(this.sculpture, x, y, z);
                  DroppedSculptureRenderer.this.rb.renderBlockAsItem(sb, 0, 1.0F);
                  GL11.glEnable(2884);
                  GL11.glPopMatrix();
               } else {
                  GL11.glPushMatrix();
                  DroppedSculptureRenderer.this.rb.cull(this.sculpture, x, y, z);
                  DroppedSculptureRenderer.this.rb.renderBlockAsItem(sb, 0, 1.0F);
                  GL11.glPopMatrix();
               }
            }
         }

         GL11.glEndList();
         sb.setCurrentBlock((Block)null, 0);
         sb.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

      // $FF: synthetic method
      CompiledRender(Object x1) {
         this();
      }
   }
}
