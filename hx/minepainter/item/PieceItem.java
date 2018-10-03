package hx.minepainter.item;

import hx.minepainter.sculpture.Operations;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PieceItem extends ChiselItem {
   public PieceItem() {
      this.setCreativeTab((CreativeTabs)null);
      this.setUnlocalizedName("sculpture_piece");
      this.setTextureName("");
      this.setHasSubtypes(true);
      this.setMaxStackSize(64);
      this.setMaxDamage(0);
   }

   public void registerIcons(IIconRegister r) {
   }

   public Block getEditBlock(ItemStack is) {
      return Block.getBlockById(is.getItemDamage() >> 4 & 4095);
   }

   public int getEditMeta(ItemStack is) {
      return is.getItemDamage() & 15;
   }

   public int getChiselFlags(EntityPlayer ep) {
      return 33;
   }

   public int getWorthPiece() {
      return 1;
   }

   public static class Cover extends PieceItem {
      public int getChiselFlags(EntityPlayer ep) {
         int axis = Operations.getLookingAxis(ep);
         switch(axis) {
         case 0:
            return 45;
         case 1:
            return 43;
         case 2:
            return 39;
         default:
            return 1;
         }
      }

      public int getWorthPiece() {
         return 64;
      }
   }

   public static class Bar extends PieceItem {
      public int getChiselFlags(EntityPlayer ep) {
         int axis = Operations.getLookingAxis(ep);
         switch(axis) {
         case 0:
            return 35;
         case 1:
            return 37;
         case 2:
            return 41;
         default:
            return 1;
         }
      }

      public int getWorthPiece() {
         return 8;
      }
   }
}
