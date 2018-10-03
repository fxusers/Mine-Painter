package hx.minepainter.item;

import hx.minepainter.ModMinePainter;
import hx.minepainter.painting.PaintingCache;
import hx.minepainter.painting.PaintingIcon;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;

public class CanvasRenderer implements IItemRenderer {
   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return type == ItemRenderType.INVENTORY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.ENTITY;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      if (type != ItemRenderType.ENTITY) {
         return false;
      } else {
         return helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.ENTITY_BOBBING;
      }
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      IIcon icon = ((CanvasItem)ModMinePainter.canvas.item).getIconFromDamage(0);
      if (item.hasTagCompound()) {
         PaintingIcon pi = PaintingCache.get(item);
         GL11.glBindTexture(3553, pi.glTexId());
         icon = pi;
      }

      if (type == ItemRenderType.INVENTORY) {
         this.renderInventory((IIcon)icon);
      } else if (type != ItemRenderType.EQUIPPED && type != ItemRenderType.EQUIPPED_FIRST_PERSON) {
         GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
         this.renderEquipped((IIcon)icon);
      } else {
         this.renderEquipped((IIcon)icon);
      }

   }

   private void renderInventory(IIcon icon) {
      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();
      tes.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)icon.getMinU(), (double)icon.getMinV());
      tes.addVertexWithUV(1.0D, 15.0D, 0.0D, (double)icon.getMinU(), (double)icon.getMaxV());
      tes.addVertexWithUV(15.0D, 15.0D, 0.0D, (double)icon.getMaxU(), (double)icon.getMaxV());
      tes.addVertexWithUV(15.0D, 1.0D, 0.0D, (double)icon.getMaxU(), (double)icon.getMinV());
      tes.draw();
   }

   private void renderEquipped(IIcon icon) {
      Tessellator var5 = Tessellator.instance;
      float var7 = icon.getMinU();
      float var8 = icon.getMaxU();
      float var9 = icon.getMinV();
      float var10 = icon.getMaxV();
      ItemRenderer.renderItemIn2D(var5, var8, var9, var7, var10, 256, 256, 0.0625F);
   }
}
