package hx.minepainter.painting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.IIcon;

public class PaintingIcon implements IIcon {
   final PaintingSheet sheet;
   int index;
   float umax;
   float umin;
   float vmax;
   float vmin;

   public PaintingIcon(PaintingSheet sheet, int index) {
      this.index = index;
      this.sheet = sheet;
      int slots = sheet.resolution / 16;
      int xind = index / slots;
      int yind = index % slots;
      float unit = 1.0F / (float)slots;
      this.umin = 1.0F * (float)xind / (float)slots;
      this.vmin = 1.0F * (float)yind / (float)slots;
      this.umax = this.umin + unit;
      this.vmax = this.vmin + unit;
   }

   @SideOnly(Side.CLIENT)
   public int getIconWidth() {
      return 16;
   }

   @SideOnly(Side.CLIENT)
   public int getIconHeight() {
      return 16;
   }

   @SideOnly(Side.CLIENT)
   public float getMinU() {
      return this.umin;
   }

   @SideOnly(Side.CLIENT)
   public float getMaxU() {
      return this.umax;
   }

   @SideOnly(Side.CLIENT)
   public float getInterpolatedU(double var1) {
      return (float)((double)this.umin + (double)(this.umax - this.umin) * var1 / 16.0D);
   }

   @SideOnly(Side.CLIENT)
   public float getMinV() {
      return this.vmin;
   }

   @SideOnly(Side.CLIENT)
   public float getMaxV() {
      return this.vmax;
   }

   @SideOnly(Side.CLIENT)
   public float getInterpolatedV(double var1) {
      return (float)((double)this.vmin + (double)(this.vmax - this.vmin) * var1 / 16.0D);
   }

   @SideOnly(Side.CLIENT)
   public String getIconName() {
      return "painting";
   }

   public void fill(BufferedImage img) {
      TextureUtil.uploadTextureImageSub(this.sheet.glTexId, img, (int)(this.umin * (float)this.sheet.resolution), (int)(this.vmin * (float)this.sheet.resolution), false, false);
   }

   public void release() {
      this.sheet.icons.add(this);
   }

   public int glTexId() {
      return this.sheet.glTexId;
   }
}
