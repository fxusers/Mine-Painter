package hx.minepainter.sculpture;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SculptureRenderCompiler {
   public static RenderBlocks rb = new SculptureRenderBlocks();
   int glDisplayList = -1;
   int light;
   public boolean changed = true;
   boolean context = false;
   float[][][] neighborAO = new float[3][3][3];

   public void updateAO(IBlockAccess w, int x, int y, int z) {
      for(int i = 0; i < 27; ++i) {
         int dx = i % 3;
         int dy = i / 3 % 3;
         int dz = i / 9 % 3;
         float ao = w.getBlock(x + dx - 1, y + dy - 1, z + dz - 1).getAmbientOcclusionLightValue();
         if (ao != this.neighborAO[dx][dy][dz]) {
            this.changed = true;
            this.neighborAO[dx][dy][dz] = ao;
         }
      }

      this.context = true;
   }

   public void updateLight(int light) {
      if (light != this.light) {
         this.changed = true;
      }

      this.light = light;
      this.context = true;
   }

   public boolean hasContext() {
      return this.context;
   }

   public boolean update(BlockSlice slice) {
      if (this.glDisplayList != -1 && !this.changed) {
         return false;
      } else {
         if (this.glDisplayList < 0) {
            this.glDisplayList = GLAllocation.generateDisplayLists(1);
         }

         GL11.glPushMatrix();
         GL11.glNewList(this.glDisplayList, 4864);
         this.build(slice);
         GL11.glEndList();
         GL11.glPopMatrix();
         this.changed = false;
         return true;
      }
   }

   public void build(BlockSlice slice) {
      rb.blockAccess = slice;
      rb.renderAllFaces = false;
      SculptureBlock sculpture = (SculptureBlock)ModMinePainter.sculpture.block;
      TextureManager tm = Minecraft.getMinecraft().renderEngine;
      tm.bindTexture(TextureMap.locationBlocksTexture);
      Tessellator tes = Tessellator.instance;
      double[] offs = this.getTesOffsets();
      tes.setTranslation(0.0D, 0.0D, 0.0D);
      tes.startDrawingQuads();

      for(int i = 0; i < 512; ++i) {
         int x = i >> 6 & 7;
         int y = i >> 3 & 7;
         int z = i >> 0 & 7;
         Block b = slice.getBlock(x, y, z);
         if (b != Blocks.air) {
            int meta = slice.getBlockMetadata(x, y, z);
            sculpture.setCurrentBlock(b, meta);
            tes.setTranslation((double)(-x), (double)(-y), (double)(-z));
            sculpture.setBlockBounds((float)x / 8.0F, (float)y / 8.0F, (float)z / 8.0F, (float)(x + 1) / 8.0F, (float)(y + 1) / 8.0F, (float)(z + 1) / 8.0F);
            rb.renderBlockByRenderType(sculpture, x, y, z);
         }
      }

      ((SculptureBlock)ModMinePainter.sculpture.block).setCurrentBlock((Block)null, 0);
      sculpture.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      rb.blockAccess = null;
      tes.draw();
      tes.setTranslation(offs[0], offs[1], offs[2]);
   }

   public void clear() {
      if (this.glDisplayList >= 0) {
         GL11.glDeleteLists(this.glDisplayList, 1);
      }

   }

   public boolean ready() {
      return this.glDisplayList >= 0;
   }

   private double[] getTesOffsets() {
      double[] off = new double[3];
      int count = 0;
      int xoff = 0;
      Field[] fields = Tessellator.class.getDeclaredFields();

      for(int i = 0; i < fields.length; ++i) {
         if (fields[i].getType() == Double.TYPE) {
            ++count;
            if (count == 3) {
               xoff = i - 2;
            }
         } else {
            count = 0;
         }
      }

      off[0] = (Double)ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, xoff);
      off[1] = (Double)ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, xoff + 1);
      off[2] = (Double)ObfuscationReflectionHelper.getPrivateValue(Tessellator.class, Tessellator.instance, xoff + 2);
      return off;
   }

   public void initFromSculptureAndLight(Sculpture sculpture, int light) {
      this.update(BlockSlice.of(sculpture, light));
   }
}
