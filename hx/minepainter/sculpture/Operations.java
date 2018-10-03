package hx.minepainter.sculpture;

import hx.minepainter.ModMinePainter;
import hx.utils.Utils;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Operations {
   static double length;
   static int[] xyzf = new int[]{-1, -1, -1, -1};
   public static final int PLACE = 1;
   public static final int ALLX = 2;
   public static final int ALLY = 4;
   public static final int ALLZ = 8;
   public static final int DAMAGE = 16;
   public static final int CONSUME = 32;

   public static int editSubBlock(World w, int[] minmax, int x, int y, int z, Block block, byte meta) {
      int s = 0;
      LinkedList droplist = new LinkedList();

      for(int _x = minmax[0]; _x < minmax[3]; ++_x) {
         for(int _y = minmax[1]; _y < minmax[4]; ++_y) {
            for(int _z = minmax[2]; _z < minmax[5]; ++_z) {
               int tx = x;
               int ty = y;

               int tz;
               for(tz = z; _x > 7; ++tx) {
                  _x -= 8;
               }

               while(_y > 7) {
                  _y -= 8;
                  ++ty;
               }

               while(_z > 7) {
                  _z -= 8;
                  ++tz;
               }

               while(_x < 0) {
                  _x += 8;
                  --tx;
               }

               while(_y < 0) {
                  _y += 8;
                  --ty;
               }

               while(_z < 0) {
                  _z += 8;
                  --tz;
               }

               Block tgt_block = w.getBlock(tx, ty, tz);
               int tgt_meta = w.getBlockMetadata(tx, ty, tz);
               if (tgt_block == Blocks.air && block != Blocks.air) {
                  w.setBlock(tx, ty, tz, ModMinePainter.sculpture.block);  // Babar ???
               } else if (sculptable(tgt_block, tgt_meta)) {
                  convertToFullSculpture(w, tx, ty, tz);
               }

               if (w.getBlock(tx, ty, tz) == ModMinePainter.sculpture.block) {
                  SculptureEntity se = (SculptureEntity)w.getTileEntity(tx, ty, tz);
                  Block former = se.sculpture.getBlockAt(_x, _y, _z, (BlockSlice)null);
                  int metaFormer = se.sculpture.getMetaAt(_x, _y, _z, (BlockSlice)null);
                  addDrop(droplist, former, metaFormer);
                  se.sculpture.setBlockAt(_x, _y, _z, block, meta);  // Babar ???
                  if (se.sculpture.isEmpty()) {
                     w.setBlock(x, y, z, Blocks.air);
                  }

                  if (w.isRemote) {
                     se.getRender().changed = true;
                  } else {
                     w.markBlockForUpdate(tx, ty, tz);
                  }

                  ++s;
               }
            }
         }
      }

      Iterator i$ = droplist.iterator();

      while(i$.hasNext()) {
         int[] drop = (int[])i$.next();
         if (drop[0] != 0) {
            dropScrap(w, x, y, z, Block.getBlockById(drop[0]), (byte)drop[1], drop[2]);
         }
      }

      return s;
   }

   private static void addDrop(List drops, Block block, int meta) {
      int id = Block.getIdFromBlock(block);
      Iterator i$ = drops.iterator();

      int[] drop;
      do {
         if (!i$.hasNext()) {
            drops.add(new int[]{id, meta, 1});
            return;
         }

         drop = (int[])i$.next();
      } while(drop[0] != id || drop[1] != meta);

      ++drop[2];
   }

   public static void dropScrap(World w, int x, int y, int z, Block block, byte meta, int amount) {
      if (block != Blocks.air) {
         int covers = amount / 64;
         amount %= 64;
         int bars = amount / 8;
         amount %= 8;
         ItemStack is;
         if (covers > 0) {
            is = new ItemStack(ModMinePainter.cover.item);
            is.stackSize = covers;
            is.setItemDamage((Block.getIdFromBlock(block) << 4) + meta);
            ((SculptureBlock)ModMinePainter.sculpture.block).dropScrap(w, x, y, z, is);
         }

         if (bars > 0) {
            is = new ItemStack(ModMinePainter.bar.item);
            is.stackSize = bars;
            is.setItemDamage((Block.getIdFromBlock(block) << 4) + meta);
            ((SculptureBlock)ModMinePainter.sculpture.block).dropScrap(w, x, y, z, is);
         }

         if (amount > 0) {
            is = new ItemStack(ModMinePainter.piece.item);
            is.stackSize = amount;
            is.setItemDamage((Block.getIdFromBlock(block) << 4) + meta);
            ((SculptureBlock)ModMinePainter.sculpture.block).dropScrap(w, x, y, z, is);
         }

      }
   }

   public static boolean sculptable(Block b, int blockMeta) {
      if (b == null) {
         return false;
      } else if (b == Blocks.grass) {
         return false;
      } else if (b == Blocks.bedrock) {
         return false;
      } else if (b == Blocks.cactus) {
         return false;
      } else if (b == Blocks.glass) {
         return true;
      } else if (b == Blocks.stained_glass) {
         return true;
      } else if (b == Blocks.leaves) {
         return false;
      } else if (b.hasTileEntity(blockMeta)) {
         return false;
      } else if (!b.renderAsNormalBlock()) {
         return false;
      } else if (b.getBlockBoundsMaxX() != 1.0D) {
         return false;
      } else if (b.getBlockBoundsMaxY() != 1.0D) {
         return false;
      } else if (b.getBlockBoundsMaxZ() != 1.0D) {
         return false;
      } else if (b.getBlockBoundsMinX() != 0.0D) {
         return false;
      } else if (b.getBlockBoundsMinY() != 0.0D) {
         return false;
      } else {
         return b.getBlockBoundsMinZ() == 0.0D;
      }
   }

   public static void convertToFullSculpture(World w, int x, int y, int z) {
      Block was = w.getBlock(x, y, z);
      int meta = w.getBlockMetadata(x, y, z);
      w.setBlock(x, y, z, ModMinePainter.sculpture.block);
      SculptureEntity se = (SculptureEntity)w.getTileEntity(x, y, z);

      for(int i = 0; i < 512; ++i) {
         se.sculpture.setBlockAt(i >> 6 & 7, i >> 3 & 7, i >> 0 & 7, was, (byte)meta);
      }

   }

   public static int[] raytrace(int x, int y, int z, EntityPlayer ep) {
      Block sculpture = ep.worldObj.getBlock(x, y, z);
      Sculpture the_sculpture = null;
      if (sculpture == ModMinePainter.sculpture.block) {
         SculptureEntity se = (SculptureEntity)Utils.getTE(ep.worldObj, x, y, z);
         the_sculpture = se.sculpture();
      }

      Vec3 from = ep.getPosition(1.0F);
      from = from.addVector((double)(-x), (double)(-y), (double)(-z));
      Vec3 look = ep.getLookVec();
      return raytrace(the_sculpture, from, from.addVector(look.xCoord * 5.0D, look.yCoord * 5.0D, look.zCoord * 5.0D));
   }

   public static int[] raytrace(Sculpture sculpture, Vec3 start, Vec3 end) {
      xyzf[0] = xyzf[1] = xyzf[2] = xyzf[3] = -1;
      length = Double.MAX_VALUE;

      int z;
      Vec3 hit;
      int x;
      int y;
      for(z = 0; z <= 8; ++z) {
         hit = start.getIntermediateWithXValue(end, (double)((float)z / 8.0F));
         if (hit != null && hit.yCoord >= 0.0D && hit.zCoord >= 0.0D) {
            x = (int)(hit.yCoord * 8.0D);
            y = (int)(hit.zCoord * 8.0D);
            if (end.xCoord > start.xCoord) {
               updateRaytraceResult(sculpture, z, x, y, ForgeDirection.WEST.ordinal(), hit.subtract(start).lengthVector());
            } else {
               updateRaytraceResult(sculpture, z - 1, x, y, ForgeDirection.EAST.ordinal(), hit.subtract(start).lengthVector());
            }
         }
      }

      for(z = 0; z <= 8; ++z) {
         hit = start.getIntermediateWithYValue(end, (double)((float)z / 8.0F));
         if (hit != null && hit.xCoord >= 0.0D && hit.zCoord >= 0.0D) {
            x = (int)(hit.xCoord * 8.0D);
            y = (int)(hit.zCoord * 8.0D);
            if (end.yCoord > start.yCoord) {
               updateRaytraceResult(sculpture, x, z, y, ForgeDirection.DOWN.ordinal(), hit.subtract(start).lengthVector());
            } else {
               updateRaytraceResult(sculpture, x, z - 1, y, ForgeDirection.UP.ordinal(), hit.subtract(start).lengthVector());
            }
         }
      }

      for(z = 0; z <= 8; ++z) {
         hit = start.getIntermediateWithZValue(end, (double)((float)z / 8.0F));
         if (hit != null && hit.xCoord >= 0.0D && hit.yCoord >= 0.0D) {
            x = (int)(hit.xCoord * 8.0D);
            y = (int)(hit.yCoord * 8.0D);
            if (end.zCoord > start.zCoord) {
               updateRaytraceResult(sculpture, x, y, z, ForgeDirection.NORTH.ordinal(), hit.subtract(start).lengthVector());
            } else {
               updateRaytraceResult(sculpture, x, y, z - 1, ForgeDirection.SOUTH.ordinal(), hit.subtract(start).lengthVector());
            }
         }
      }

      return xyzf;
   }

   private static void updateRaytraceResult(Sculpture sculpture, int x, int y, int z, int f, double len) {
      if (Sculpture.contains(x, y, z)) {
         if (sculpture == null || sculpture.getBlockAt(x, y, z, (BlockSlice)null) != Blocks.air) {
            if (len < length) {
               length = len;
               xyzf[0] = x;
               xyzf[1] = y;
               xyzf[2] = z;
               xyzf[3] = f;
            }
         }
      }
   }

   public static void setBlockBoundsFromRaytrace(int[] pos, Block block, int type) {
      pos = (int[])pos.clone();
      if (hasFlag(type, 1)) {
         ForgeDirection dir = ForgeDirection.getOrientation(pos[3]);
         pos[0] += dir.offsetX;
         pos[1] += dir.offsetY;
         pos[2] += dir.offsetZ;
      }

      int x = 0;
      int y = 0;

      int z;
      for(z = 0; pos[0] < 0; --x) {
         pos[0] += 8;
      }

      while(pos[0] > 7) {
         pos[0] -= 8;
         ++x;
      }

      while(pos[1] < 0) {
         pos[1] += 8;
         --y;
      }

      while(pos[1] > 7) {
         pos[1] -= 8;
         ++y;
      }

      while(pos[2] < 0) {
         pos[2] += 8;
         --z;
      }

      while(pos[2] > 7) {
         pos[2] -= 8;
         ++z;
      }

      boolean allx = (type & 2) > 0;
      boolean ally = (type & 4) > 0;
      boolean allz = (type & 8) > 0;
      block.setBlockBounds(allx ? (float)(x + 0) : (float)x + (float)pos[0] / 8.0F, ally ? (float)(y + 0) : (float)y + (float)pos[1] / 8.0F, allz ? (float)(z + 0) : (float)z + (float)pos[2] / 8.0F, allx ? (float)(x + 1) : (float)x + (float)(pos[0] + 1) / 8.0F, ally ? (float)(y + 1) : (float)y + (float)(pos[1] + 1) / 8.0F, allz ? (float)(z + 1) : (float)z + (float)(pos[2] + 1) / 8.0F);
   }

   public static boolean validOperation(World worldObj, int x, int y, int z, int[] pos, int chiselFlags) {
      pos = (int[])pos.clone();
      if (hasFlag(chiselFlags, 1)) {
         ForgeDirection dir = ForgeDirection.getOrientation(pos[3]);
         pos[0] += dir.offsetX;
         pos[1] += dir.offsetY;
         pos[2] += dir.offsetZ;
      }

      while(pos[0] < 0) {
         pos[0] += 8;
         --x;
      }

      while(pos[0] > 7) {
         pos[0] -= 8;
         ++x;
      }

      while(pos[1] < 0) {
         pos[1] += 8;
         --y;
      }

      while(pos[1] > 7) {
         pos[1] -= 8;
         ++y;
      }

      while(pos[2] < 0) {
         pos[2] += 8;
         --z;
      }

      while(pos[2] > 7) {
         pos[2] -= 8;
         ++z;
      }

      Block b = worldObj.getBlock(x, y, z);
      if (hasFlag(chiselFlags, 1)) {
         if (b == Blocks.air) {
            return true;
         } else {
            return b == ModMinePainter.sculpture.block;
         }
      } else {
         int meta = worldObj.getBlockMetadata(x, y, z);
         if (b == Blocks.air) {
            return false;
         } else if (b == ModMinePainter.sculpture.block) {
            return true;
         } else {
            return sculptable(b, meta);
         }
      }
   }

   private static boolean hasFlag(int flags, int mask) {
      return (flags & mask) > 0;
   }

   public static boolean applyOperation(World w, int x, int y, int z, int[] pos, int flags, Block editBlock, int editMeta) {
      pos = (int[])pos.clone();
      if (hasFlag(flags, 1)) {
         ForgeDirection dir = ForgeDirection.getOrientation(pos[3]);
         pos[0] += dir.offsetX;
         pos[1] += dir.offsetY;
         pos[2] += dir.offsetZ;
      }

      while(pos[0] < 0) {
         pos[0] += 8;
         --x;
      }

      while(pos[0] > 7) {
         pos[0] -= 8;
         ++x;
      }

      while(pos[1] < 0) {
         pos[1] += 8;
         --y;
      }

      while(pos[1] > 7) {
         pos[1] -= 8;
         ++y;
      }

      while(pos[2] < 0) {
         pos[2] += 8;
         --z;
      }

      while(pos[2] > 7) {
         pos[2] -= 8;
         ++z;
      }

      int[] minmax = new int[6];
      boolean allx = hasFlag(flags, 2);
      boolean ally = hasFlag(flags, 4);
      boolean allz = hasFlag(flags, 8);
      minmax[0] = allx ? 0 : pos[0];
      minmax[1] = ally ? 0 : pos[1];
      minmax[2] = allz ? 0 : pos[2];
      minmax[3] = allx ? 8 : pos[0] + 1;
      minmax[4] = ally ? 8 : pos[1] + 1;
      minmax[5] = allz ? 8 : pos[2] + 1;
      int blocks = editSubBlock(w, minmax, x, y, z, editBlock, (byte)editMeta);
      return blocks > 0;
   }

   public static int getLookingAxis(EntityPlayer ep) {
      Vec3 vec = ep.getLookVec();
      double x = Math.abs(vec.xCoord);
      double y = Math.abs(vec.yCoord);
      double z = Math.abs(vec.zCoord);
      if (x >= y && x >= z) {
         return 0;
      } else if (y >= x && y >= z) {
         return 1;
      } else {
         return z >= x && z >= y ? 2 : 0;
      }
   }
}
