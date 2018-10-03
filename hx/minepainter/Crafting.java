package hx.minepainter;

import cpw.mods.fml.common.registry.GameRegistry;
import hx.minepainter.item.PieceItem;
import hx.minepainter.painting.PaintTool;
import hx.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class Crafting {
   private IRecipe scrap = new IRecipe() {
      public boolean matches(InventoryCrafting ic, World w) {
         Block block = null;
         int meta = 0;
         int count = 0;
         int size = ic.getSizeInventory();

         for(int i = 0; i < size; ++i) {
            ItemStack is = ic.getStackInSlot(i);
            if (is != null && is.getItem() instanceof PieceItem) {
               PieceItem pi = (PieceItem)Utils.getItem(is);
               if (block == null) {
                  block = pi.getEditBlock(is);
                  meta = pi.getEditMeta(is);
               }

               if (block != pi.getEditBlock(is)) {
                  return false;
               }

               if (meta != pi.getEditMeta(is)) {
                  return false;
               }

               count += pi.getWorthPiece();
            }
         }

         if (count == 0) {
            return false;
         } else if (count % 512 == 0 && count / 512 <= 64) {
            return true;
         } else if (count % 64 == 0 && count / 64 <= 64) {
            return true;
         } else if (count % 8 == 0 && count / 8 <= 64) {
            return true;
         } else if (count <= 64) {
            return true;
         } else {
            return false;
         }
      }

      public ItemStack getCraftingResult(InventoryCrafting ic) {
         Block block = null;
         int meta = 0;
         int count = 0;
         int size = ic.getSizeInventory();

         for(int i = 0; i < size; ++i) {
            ItemStack is = ic.getStackInSlot(i);
            if (is != null && is.getItem() instanceof PieceItem) {
               PieceItem pi = (PieceItem)Utils.getItem(is);
               if (block == null) {
                  block = pi.getEditBlock(is);
                  meta = pi.getEditMeta(is);
               }

               count += pi.getWorthPiece();
            }
         }

         if (count % 512 == 0 && count / 512 <= 64) {
            return new ItemStack(block, count / 512, meta);
         } else if (count % 64 == 0 && count / 64 <= 64) {
            return new ItemStack(ModMinePainter.cover.item, count / 64, (Block.getIdFromBlock(block) << 4) + meta);
         } else if (count % 8 == 0 && count / 8 <= 64) {
            return new ItemStack(ModMinePainter.bar.item, count / 8, (Block.getIdFromBlock(block) << 4) + meta);
         } else {
            return new ItemStack(ModMinePainter.piece.item, count, (Block.getIdFromBlock(block) << 4) + meta);
         }
      }

      public int getRecipeSize() {
         return 0;
      }

      public ItemStack getRecipeOutput() {
         return null;
      }
   };
   private IRecipe fillBucket = new IRecipe() {
      public int getRecipeSize() {
         return 0;
      }

      public ItemStack getRecipeOutput() {
         return null;
      }

      public boolean matches(InventoryCrafting ic, World w) {
         ItemStack bucket = null;
         ItemStack dye = null;
         int size = ic.getSizeInventory();

         for(int i = 0; i < size; ++i) {
            ItemStack is = ic.getStackInSlot(i);
            if (is != null) {
               if (!(is.getItem() instanceof PaintTool.Bucket) && is.getItem() != Items.water_bucket) {
                  if (!(is.getItem() instanceof ItemDye) && is.getItem() != Items.slime_ball) {
                     return false;
                  }

                  if (dye != null) {
                     return false;
                  }

                  dye = is;
               } else {
                  if (bucket != null) {
                     return false;
                  }

                  bucket = is;
               }
            }
         }

         return bucket != null && dye != null;
      }

      public ItemStack getCraftingResult(InventoryCrafting ic) {
         ItemStack bucket = null;
         ItemStack dye = null;
         int size = ic.getSizeInventory();

         for(int i = 0; i < size; ++i) {
            ItemStack is = ic.getStackInSlot(i);
            if (is != null) {
               if (!(is.getItem() instanceof PaintTool.Bucket) && is.getItem() != Items.water_bucket) {
                  if (is.getItem() instanceof ItemDye || is.getItem() == Items.slime_ball) {
                     if (dye != null) {
                        return null;
                     }

                     dye = is;
                  }
               } else {
                  if (bucket != null) {
                     return null;
                  }

                  bucket = is;
               }
            }
         }

         ItemStack newbucket = new ItemStack(ModMinePainter.bucket.item);
         newbucket.setItemDamage(dye.getItem() == Items.slime_ball ? 16 : dye.getItemDamage());
         return newbucket;
      }
   };

   public void registerRecipes() {
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.minibrush.item), new Object[]{"X  ", " Y ", "  Z", 'X', new ItemStack(Blocks.wool), 'Y', new ItemStack(Items.stick), 'Z', new ItemStack(Items.dye, 1, 1)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.mixerbrush.item), new Object[]{"XX ", "XY ", "  Z", 'X', new ItemStack(Blocks.wool), 'Y', new ItemStack(Items.stick), 'Z', new ItemStack(Items.dye, 1, 1)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.canvas.item), new Object[]{"XXX", "XXX", 'X', new ItemStack(Blocks.wool)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.chisel.item), new Object[]{"X ", " Y", 'X', new ItemStack(Blocks.cobblestone), 'Y', new ItemStack(Items.stick)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.barcutter.item), new Object[]{"X ", " Y", 'X', new ItemStack(Items.iron_ingot), 'Y', new ItemStack(ModMinePainter.chisel.item)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.saw.item), new Object[]{"X ", " Y", 'X', new ItemStack(Items.diamond), 'Y', new ItemStack(ModMinePainter.barcutter.item)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.palette.item), new Object[]{"X", "Y", 'X', new ItemStack(Blocks.planks), 'Y', new ItemStack(ModMinePainter.chisel.item)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.eraser.item), new Object[]{"XX ", "YY ", "ZZ ", 'X', new ItemStack(Items.slime_ball), 'Y', new ItemStack(Items.paper), 'Z', new ItemStack(Items.dye, 1, 4)});
      GameRegistry.addRecipe(new ItemStack(ModMinePainter.wrench.item), new Object[]{"XX ", "YX ", " X ", 'X', new ItemStack(Items.iron_ingot), 'Y', new ItemStack(Items.dye, 1, 1)});
      GameRegistry.addRecipe(this.scrap);
      GameRegistry.addRecipe(this.fillBucket);
   }
}
