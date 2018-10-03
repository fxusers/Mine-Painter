package hx.minepainter.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import hx.minepainter.sculpture.SculptureBlock;
import hx.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class PieceRenderer implements IItemRenderer {
   private RenderItem renderItem = new RenderItem();
   private ItemStack is;
   private Minecraft mc = Minecraft.getMinecraft();

   public boolean handleRenderType(ItemStack item, ItemRenderType type) {
      return type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
   }

   public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
      return type == ItemRenderType.ENTITY || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
   }

   public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
      if (this.is == null) {
         this.is = new ItemStack(ModMinePainter.sculpture.block);
      }

      SculptureBlock sculpture = (SculptureBlock)ModMinePainter.sculpture.block;
      PieceItem piece = (PieceItem)Utils.getItem(item);
      sculpture.setCurrentBlock(piece.getEditBlock(item), piece.getEditMeta(item));
      this.setBounds(sculpture);
      if (type == ItemRenderType.INVENTORY) {
         RenderHelper.enableGUIStandardItemLighting();
         this.renderItem.renderItemIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, this.is, 0, 0);
      } else if (type == ItemRenderType.ENTITY) {
         EntityItem eis = (EntityItem)data[1];
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         RenderBlocks rb = (RenderBlocks)((RenderBlocks)data[0]);
         rb.renderBlockAsItem(sculpture, 0, 1.0F);
      } else {
         Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem((EntityLivingBase)data[1], this.is, 0, type);
      }

      sculpture.setCurrentBlock((Block)null, 0);
      sculpture.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   protected void setBounds(SculptureBlock sculpture) {
      sculpture.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
   }

   public static class Cover extends PieceRenderer {
      protected void setBounds(SculptureBlock sculpture) {
         sculpture.setBlockBounds(0.3F, 0.0F, 0.0F, 0.7F, 1.0F, 1.0F);
      }
   }

   public static class Bar extends PieceRenderer {
      protected void setBounds(SculptureBlock sculpture) {
         sculpture.setBlockBounds(0.3F, 0.0F, 0.3F, 0.7F, 1.0F, 0.7F);
      }
   }
}
