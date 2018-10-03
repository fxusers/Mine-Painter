package hx.minepainter.painting;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

public class PaintingRenderer extends TileEntitySpecialRenderer {
   public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partial) {
      PaintingEntity pe = (PaintingEntity)entity;
      Tessellator tes = Tessellator.instance;
      PaintingIcon icon = pe.getIcon();
      PaintingPlacement placement = PaintingPlacement.of(pe.getBlockMetadata());
      GL11.glPushMatrix();
      GL11.glBindTexture(3553, icon.sheet.glTexId);
      RenderHelper.disableStandardItemLighting();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(3042);
      GL11.glTranslated(x, y, z);
      tes.startDrawingQuads();
      this.addPoint(placement, 0, 0, icon);
      this.addPoint(placement, 0, 1, icon);
      this.addPoint(placement, 1, 1, icon);
      this.addPoint(placement, 1, 0, icon);
      tes.draw();
      GL11.glPopMatrix();
      GL11.glDisable(3042);
   }

   private void addPoint(PaintingPlacement pp, int x, int y, IIcon icon) {
      float[] pos = pp.painting2blockWithShift((float)x, (float)y, 0.003F);
      Tessellator.instance.addVertexWithUV((double)pos[0], (double)pos[1], (double)pos[2], x == 0 ? (double)icon.getMinU() : (double)icon.getMaxU(), y == 0 ? (double)icon.getMinV() : (double)icon.getMaxV());
   }

   private void drawSides(Tessellator tes, PaintingPlacement placement) {
      tes.startDrawingQuads();
      this.addColoredPoint(placement, 0, 0, 0.0625F);
      this.addColoredPoint(placement, 1, 0, 0.0625F);
      this.addColoredPoint(placement, 1, 0, 0.0F);
      this.addColoredPoint(placement, 0, 0, 0.0F);
      tes.draw();
      tes.startDrawingQuads();
      this.addColoredPoint(placement, 1, 0, 0.0625F);
      this.addColoredPoint(placement, 1, 1, 0.0625F);
      this.addColoredPoint(placement, 1, 1, 0.0F);
      this.addColoredPoint(placement, 1, 0, 0.0F);
      tes.draw();
      tes.startDrawingQuads();
      this.addColoredPoint(placement, 1, 1, 0.0625F);
      this.addColoredPoint(placement, 0, 1, 0.0625F);
      this.addColoredPoint(placement, 0, 1, 0.0F);
      this.addColoredPoint(placement, 1, 1, 0.0F);
      tes.draw();
      tes.startDrawingQuads();
      this.addColoredPoint(placement, 0, 1, 0.0625F);
      this.addColoredPoint(placement, 0, 0, 0.0625F);
      this.addColoredPoint(placement, 0, 0, 0.0F);
      this.addColoredPoint(placement, 0, 1, 0.0F);
      tes.draw();
   }

   private void addColoredPoint(PaintingPlacement pp, int x, int y, float d) {
      float[] pos = pp.painting2blockWithShift((float)x, (float)y, 0.0F);
      Tessellator.instance.setColorOpaque_I(16777215);
      Tessellator.instance.addVertex((double)pos[0], (double)pos[1], (double)pos[2]);
   }
}
