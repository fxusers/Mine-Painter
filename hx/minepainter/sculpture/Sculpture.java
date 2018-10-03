package hx.minepainter.sculpture;

import hx.utils.Debug;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;

public class Sculpture {
   byte[][] layers;
   int[] block_ids;
   byte[] block_metas;
   int[] usage_count;
   Rotation r = new Rotation();

   public Sculpture() {
      this.normalize();
   }

   public Rotation getRotation() {
      return this.r;
   }

   public void write(NBTTagCompound nbt) {
      nbt.setIntArray("block_ids", this.block_ids);
      nbt.setByteArray("block_metas", this.block_metas);

      for(int i = 0; i < this.layers.length; ++i) {
         nbt.setByteArray("layer" + i, this.layers[i]);
      }

      nbt.setByteArray("rotation", this.r.r);
   }

   public void read(NBTTagCompound nbt) {
      this.block_ids = nbt.getIntArray("block_ids");
      this.block_metas = nbt.getByteArray("block_metas");
      this.r.r = nbt.getByteArray("rotation");
      this.layers = new byte[this.log(this.block_ids.length)][];

      for(int i = 0; i < this.layers.length; ++i) {
         this.layers[i] = nbt.getByteArray("layer" + i);
      }

      this.normalize();
   }

   public Block getBlockAt(int x, int y, int z, BlockSlice slice) {
      if (!contains(x, y, z)) {
         return slice.getBlock(x, y, z);
      } else {
         int index = this.getIndex(x, y, z);
         return Block.getBlockById(this.block_ids[index]);
      }
   }

   public int getMetaAt(int x, int y, int z, BlockSlice slice) {
      if (!contains(x, y, z)) {
         return slice.getBlockMetadata(x, y, z);
      } else {
         int index = this.getIndex(x, y, z);
         return this.block_metas[index];
      }
   }

   public boolean setBlockAt(int x, int y, int z, Block block, byte meta) {   // Babar ???
      if (!contains(x, y, z)) {
         return false;
      } else {
         int index = this.findIndexForBlock(Block.getIdFromBlock(block), meta);
         if (index < 0) {
            this.grow();
            index = this.block_ids.length / 2;
            this.block_ids[index] = Block.getIdFromBlock(block);
            this.block_metas[index] = meta;
         }

         this.setIndex(x, y, z, index);
         return true;
      }
   }

   public boolean isEmpty() {
      int s = 0;

      for(int i = 0; i < this.block_ids.length; ++i) {
         if (this.block_ids[i] == 0) {
            s += this.usage_count[i];
         }
      }

      return s == 512;
   }

   public boolean isFull() {
      int s = 0;

      for(int i = 0; i < this.block_ids.length; ++i) {
         if (this.block_ids[i] == 0) {
            s += this.usage_count[i];
         }
      }

      return s == 0;
   }

   private int findIndexForBlock(int blockID, byte meta) {
      for(int i = 0; i < this.block_ids.length; ++i) {
         if (this.block_ids[i] == blockID && this.block_metas[i] == meta) {
            return i;
         }

         if (this.usage_count[i] == 0) {
            this.block_ids[i] = blockID;
            this.block_metas[i] = meta;
            return i;
         }
      }

      return -1;
   }

   int getIndex(int x, int y, int z) {
      this.r.apply(x, y, z);
      int index = 0;

      for(int l = 0; l < this.layers.length; ++l) {
         if ((this.layers[l][this.r.x * 8 + this.r.y] & 1 << this.r.z) > 0) {
            index |= 1 << l;
         }
      }

      return index;
   }

   void setIndex(int x, int y, int z, int index) {
      int prev = this.getIndex(x, y, z);
      --this.usage_count[prev];
      ++this.usage_count[index];
      this.r.apply(x, y, z);

      for(int l = 0; l < this.layers.length; ++l) {
         if ((index & 1 << l) > 0) {
            this.layers[l][this.r.x * 8 + this.r.y] = (byte)(this.layers[l][this.r.x * 8 + this.r.y] | 1 << this.r.z);
         } else {
            this.layers[l][this.r.x * 8 + this.r.y] = (byte)(this.layers[l][this.r.x * 8 + this.r.y] & ~(1 << this.r.z));
         }
      }

   }

   public static boolean contains(int x, int y, int z) {
      return x >= 0 && y >= 0 && z >= 0 && x < 8 && y < 8 && z < 8;
   }

   private boolean check() {
      if (this.block_ids == null) {
         return false;
      } else if (this.block_metas == null) {
         return false;
      } else if (this.layers == null) {
         return false;
      } else if (this.r.r == null) {
         return false;
      } else {
         for(int i = 0; i < this.layers.length; ++i) {
            if (this.layers[i] == null) {
               Debug.log("layer " + i + " is null!");
               return false;
            }

            if (this.layers[i].length != 64) {
               Debug.log("layer " + i + " is " + this.layers[i].length + " long!");
               return false;
            }
         }

         if (this.block_ids.length != 1 << this.layers.length) {
            return false;
         } else if (this.block_ids.length != this.block_metas.length) {
            return false;
         } else {
            if (this.usage_count.length != this.block_ids.length) {
               this.usage_count = new int[this.block_ids.length];
            }

            return true;
         }
      }
   }

   private void grow() {
      byte[][] nlayers = new byte[this.layers.length + 1][];

      for(int i = 0; i < this.layers.length; ++i) {
         nlayers[i] = this.layers[i];
      }

      nlayers[this.layers.length] = new byte[64];
      this.layers = nlayers;
      int[] ids = new int[this.block_ids.length * 2];

      for(int i = 0; i < this.block_ids.length; ++i) {
         ids[i] = this.block_ids[i];
      }

      this.block_ids = ids;
      byte[] metas = new byte[this.block_metas.length * 2];

      for(int i = 0; i < this.block_metas.length; ++i) {
         metas[i] = this.block_metas[i];
      }

      this.block_metas = metas;
      int[] usage = new int[this.usage_count.length * 2];

      for(int i = 0; i < this.usage_count.length; ++i) {
         usage[i] = this.usage_count[i];
      }

      this.usage_count = usage;
   }

   private void normalize() {
      if (!this.check()) {
         this.layers = new byte[1][64];
         this.block_ids = new int[2];
         this.block_metas = new byte[2];
         this.usage_count = new int[2];
      }

      int i;
      for(i = 0; i < this.usage_count.length; ++i) {
         this.usage_count[i] = 0;
      }

      for(i = 0; i < 512; ++i) {
         int index = this.getIndex(i >> 6, i >> 3 & 7, i & 7);
         ++this.usage_count[index];
      }

   }

   private int log(int num) {
      int i;
      for(i = 0; num > 1; ++i) {
         num >>= 1;
      }

      return i;
   }

   public int getLight() {
      int light = 0;
      int current = false;

      for(int i = 0; i < this.usage_count.length; ++i) {
         int current = Block.getBlockById(this.block_ids[i]).getLightValue();
         if (current > light) {
            light = current;
         }
      }

      return light;
   }
}
