package hx.minepainter.painting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import hx.minepainter.item.Palette;
import hx.utils.Utils;
import java.awt.image.BufferedImage;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class PaintTool extends Item {
   public PaintTool() {
      this.setCreativeTab(ModMinePainter.tabMinePainter);
      this.setMaxStackSize(1);
   }

   public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs) {
      if (!w.isRemote) {
         return false;
      } else {
         boolean changed = this.paintAt(w, x, y, z, xs, ys, zs, this.getColor(ep, is));
         if (changed) {
            ModMinePainter.network.sendToServer(new PaintingOperationMessage(this, x, y, z, xs, ys, zs, this.getColor(ep, is)));
         }

         return changed;
      }
   }

   public int getColor(EntityPlayer ep, ItemStack is) {
      int size = ep.inventory.getSizeInventory();

      for(int i = 0; i < size; ++i) {
         ItemStack slot = ep.inventory.getStackInSlot(i);
         if (slot != null && slot.getItem() instanceof Palette) {
            return Palette.getColors(slot)[0];
         }
      }

      return 0;
   }

   public boolean apply(BufferedImage img, float[] point, int color) {
      return false;
   }

   public boolean inBounds(int x, int y) {
      return x >= 0 && x < 16 && y >= 0 && y < 16;
   }

   public boolean paintAt(World w, int x, int y, int z, float xs, float ys, float zs, int color) {
      if (w.getBlock(x, y, z) != ModMinePainter.painting.block) {
         return false;
      } else {
         PaintingEntity pe = (PaintingEntity)Utils.getTE(w, x, y, z);
         if (pe == null) {
            return false;
         } else {
            PaintingPlacement place = PaintingPlacement.of(w.getBlockMetadata(x, y, z));
            float[] point = place.block2painting(xs, ys, zs);
            boolean changed = false;

            for(int i = -1; i <= 1; ++i) {
               for(int j = -1; j <= 1; ++j) {
                  int _x = x + place.xpos.offsetX * i + place.ypos.offsetX * j;
                  int _y = y + place.xpos.offsetY * i + place.ypos.offsetY * j;
                  int _z = z + place.xpos.offsetZ * i + place.ypos.offsetZ * j;
                  if (w.getBlock(_x, _y, _z) == ModMinePainter.painting.block && w.getBlockMetadata(_x, _y, _z) == place.ordinal()) {
                     PaintingEntity painting = (PaintingEntity)Utils.getTE(w, _x, _y, _z);
                     point[0] -= (float)i;
                     point[1] -= (float)j;
                     boolean _changed = this.apply(painting.image, point, color);
                     point[0] += (float)i;
                     point[1] += (float)j;
                     if (_changed) {
                        if (w.isRemote) {
                           pe.getIcon().fill(pe.image);
                        } else {
                           w.markBlockForUpdate(_x, _y, _z);
                        }

                        changed = true;
                     }
                  }
               }
            }

            return changed;
         }
      }
   }

   public static class Eraser extends PaintTool.Mixer {
      public Eraser() {
         this.setUnlocalizedName("eraser").setTextureName("minepainter:eraser");
      }

      public boolean apply(BufferedImage img, float[] point, int color) {
         int x = (int)(point[0] * 16.0F + 16.0F) - 16;
         int y = (int)(point[1] * 16.0F + 16.0F) - 16;
         boolean changed = false;

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               if (this.inBounds(x + i, y + j)) {
                  changed = true;
                  color = img.getRGB(x + i, y + j);
                  int a75 = (int)((float)(color >> 24 & 255) * 0.75F) << 24;
                  a75 += color & 16777215;
                  int a50 = (int)((float)(color >> 24 & 255) * 0.5F) << 24;
                  a50 += color & 16777215;
                  int to_blend = Math.abs(i) + Math.abs(j);
                  if (to_blend == 0) {
                     to_blend = 0;
                  } else if (to_blend == 1) {
                     to_blend = a50;
                  } else {
                     to_blend = a75;
                  }

                  img.setRGB(x + i, y + j, to_blend);
               }
            }
         }

         return changed;
      }
   }

   public static class Mixer extends PaintTool {
      public Mixer() {
         this.setUnlocalizedName("mixer_brush").setTextureName("minepainter:brush");
      }

      public boolean apply(BufferedImage img, float[] point, int color) {
         int x = (int)(point[0] * 16.0F + 16.0F) - 16;
         int y = (int)(point[1] * 16.0F + 16.0F) - 16;
         int a75 = (int)((float)(color >> 24 & 255) * 0.75F) << 24;
         a75 += color & 16777215;
         int a50 = (int)((float)(color >> 24 & 255) * 0.5F) << 24;
         a50 += color & 16777215;
         boolean changed = false;

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               if (this.inBounds(x + i, y + j)) {
                  changed = true;
                  int to_blend = Math.abs(i) + Math.abs(j);
                  if (to_blend == 0) {
                     to_blend = color;
                  } else if (to_blend == 1) {
                     to_blend = a75;
                  } else {
                     to_blend = a50;
                  }

                  img.setRGB(x + i, y + j, this.mix(to_blend, img.getRGB(x + i, y + j)));
               }
            }
         }

         return changed;
      }

      private int mix(int color, int original) {
         float a_alpha = (float)(color >> 24 & 255) / 255.0F;
         float b_alpha = (float)(original >> 24 & 255) / 255.0F;
         float c_alpha = a_alpha + b_alpha * (1.0F - a_alpha);
         int result = 0;

         for(int b = 0; b < 24; b += 8) {
            int ca = color >> b & 255;
            ca = (int)((float)ca * a_alpha);
            int cb = original >> b & 255;
            cb = (int)((float)cb * b_alpha * (1.0F - a_alpha));
            result += (int)((float)(ca + cb) / c_alpha) << b;
         }

         result += (int)(255.0F * c_alpha) << 24;
         return result;
      }
   }

   public static class Bucket extends PaintTool {
      IIcon fill;

      public Bucket() {
         this.setUnlocalizedName("paint_bucket");
         this.setTextureName("minepainter:bucket");
         this.setHasSubtypes(true);
      }

      public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer ep) {
         MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(w, ep, true);
         if (mop == null) {
            return is;
         } else {
            Material m = w.getBlock(mop.blockX, mop.blockY, mop.blockZ).getMaterial();
            return m.isLiquid() ? new ItemStack(Items.bucket) : is;
         }
      }

      public boolean requiresMultipleRenderPasses() {
         return true;
      }

      public int getRenderPasses(int metadata) {
         return 2;
      }

      public IIcon getIcon(ItemStack is, int renderPass) {
         return renderPass == 0 ? super.itemIcon : this.fill;
      }

      @SideOnly(Side.CLIENT)
      public void registerIcons(IIconRegister par1IconRegister) {
         super.registerIcons(par1IconRegister);
         this.fill = par1IconRegister.registerIcon("minepainter:bucket_fill");
      }

      public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
         return par2 == 1 ? this.getColor(par1ItemStack.getItemDamage(), 0) : super.getColorFromItemStack(par1ItemStack, par2);
      }

      public int getColor(EntityPlayer ep, ItemStack is) {
         return this.getColor(is.getItemDamage(), -16777216);
      }

      private int getColor(int dmg, int mask) {
         return dmg < 16 ? ItemDye.field_150922_c[dmg] | mask : 16777215;
      }

      public boolean apply(BufferedImage img, float[] point, int color) {
         int x = (int)(point[0] * 16.0F + 16.0F) - 16;
         int y = (int)(point[1] * 16.0F + 16.0F) - 16;
         if (!this.inBounds(x, y)) {
            return false;
         } else {
            img.getRGB(x, y);

            for(int i = 0; i < 256; ++i) {
               x = i / 16;
               y = i % 16;
               img.setRGB(x, y, color);
            }

            return true;
         }
      }
   }

   public static class Mini extends PaintTool {
      public Mini() {
         this.setUnlocalizedName("mini_brush");
         this.setTextureName("minepainter:brush_small");
      }

      public boolean apply(BufferedImage img, float[] point, int color) {
         int x = (int)(point[0] * 16.0F + 16.0F) - 16;
         int y = (int)(point[1] * 16.0F + 16.0F) - 16;
         if (!this.inBounds(x, y)) {
            return false;
         } else {
            img.setRGB(x, y, color);
            return true;
         }
      }
   }
}
