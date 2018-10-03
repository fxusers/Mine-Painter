package hx.minepainter.painting;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public enum PaintingPlacement {
   UPXNEG(ForgeDirection.UP, ForgeDirection.WEST),
   UPXPOS(ForgeDirection.UP, ForgeDirection.EAST),
   UPZNEG(ForgeDirection.UP, ForgeDirection.NORTH),
   UPZPOS(ForgeDirection.UP, ForgeDirection.SOUTH),
   XNEG(ForgeDirection.WEST, ForgeDirection.DOWN),
   XPOS(ForgeDirection.EAST, ForgeDirection.DOWN),
   ZNEG(ForgeDirection.NORTH, ForgeDirection.DOWN),
   ZPOS(ForgeDirection.SOUTH, ForgeDirection.DOWN),
   DOWNXNEG(ForgeDirection.DOWN, ForgeDirection.WEST),
   DOWNXPOS(ForgeDirection.DOWN, ForgeDirection.EAST),
   DOWNZNEG(ForgeDirection.DOWN, ForgeDirection.NORTH),
   DOWNZPOS(ForgeDirection.DOWN, ForgeDirection.SOUTH);

   ForgeDirection normal;
   ForgeDirection ypos;
   ForgeDirection xpos;

   public static PaintingPlacement of(int id) {
      return values()[id % values().length];
   }

   public static PaintingPlacement of(Vec3 vec, int face) {
      ForgeDirection dir = ForgeDirection.getOrientation(face);
      switch(dir) {
      case SOUTH:
         return ZPOS;
      case NORTH:
         return ZNEG;
      case WEST:
         return XNEG;
      case EAST:
         return XPOS;
      case DOWN:
         if (Math.abs(vec.xCoord) > Math.abs(vec.zCoord)) {
            return vec.xCoord > 0.0D ? DOWNXNEG : DOWNXPOS;
         }

         return vec.zCoord > 0.0D ? DOWNZNEG : DOWNZPOS;
      case UP:
         if (Math.abs(vec.xCoord) > Math.abs(vec.zCoord)) {
            return vec.xCoord > 0.0D ? UPXNEG : UPXPOS;
         }

         return vec.zCoord > 0.0D ? UPZNEG : UPZPOS;
      default:
         return null;
      }
   }

   private PaintingPlacement(ForgeDirection normal, ForgeDirection ypos) {
      this.normal = normal;
      this.ypos = ypos;
      this.xpos = normal.getRotation(ypos);
   }

   public float[] painting2blockWithShift(float x, float y, float shift) {
      float[] point = new float[]{(float)((1 - (this.xpos.offsetX + this.ypos.offsetX + this.normal.offsetX)) / 2), (float)((1 - (this.xpos.offsetY + this.ypos.offsetY + this.normal.offsetY)) / 2), (float)((1 - (this.xpos.offsetZ + this.ypos.offsetZ + this.normal.offsetZ)) / 2)};
      point[0] += (float)this.xpos.offsetX * x + (float)this.ypos.offsetX * y + (float)this.normal.offsetX * shift;
      point[1] += (float)this.xpos.offsetY * x + (float)this.ypos.offsetY * y + (float)this.normal.offsetY * shift;
      point[2] += (float)this.xpos.offsetZ * x + (float)this.ypos.offsetZ * y + (float)this.normal.offsetZ * shift;
      return point;
   }

   public float[] painting2block(float x, float y) {
      return this.painting2blockWithShift(x, y, 0.0625F);
   }

   public float[] block2painting(float x, float y, float z) {
      float[] point = new float[]{(float)((1 - this.xpos.offsetX - this.xpos.offsetY - this.xpos.offsetZ) / 2), (float)((1 - this.ypos.offsetX - this.ypos.offsetY - this.ypos.offsetZ) / 2)};
      point[0] += (float)this.xpos.offsetX * x + (float)this.xpos.offsetY * y + (float)this.xpos.offsetZ * z;
      point[1] += (float)this.ypos.offsetX * x + (float)this.ypos.offsetY * y + (float)this.ypos.offsetZ * z;
      return point;
   }

   public void setBlockBounds(Block b) {
      b.setBlockBounds((float)(0 + (1 - this.normal.offsetX) / 2), (float)(0 + (1 - this.normal.offsetY) / 2), (float)(0 + (1 - this.normal.offsetZ) / 2), (float)(1 - (1 + this.normal.offsetX) / 2), (float)(1 - (1 + this.normal.offsetY) / 2), (float)(1 - (1 + this.normal.offsetZ) / 2));
   }
}
