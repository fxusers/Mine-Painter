package hx.minepainter.sculpture;

public class Rotation {
   byte[] r = new byte[9];
   int x;
   int y;
   int z;

   public void apply(int x, int y, int z) {
      this.x = x * this.r[0] + y * this.r[3] + z * this.r[6];
      this.y = x * this.r[1] + y * this.r[4] + z * this.r[7];
      this.z = x * this.r[2] + y * this.r[5] + z * this.r[8];
      if (this.r[0] + this.r[3] + this.r[6] < 0) {
         this.x += 7;
      }

      if (this.r[1] + this.r[4] + this.r[7] < 0) {
         this.y += 7;
      }

      if (this.r[2] + this.r[5] + this.r[8] < 0) {
         this.z += 7;
      }

   }

   public void multiply(Rotation r) {
      byte[] result = new byte[9];

      for(int i = 0; i < 9; ++i) {
         int x = i / 3;
         int y = i % 3;

         for(int j = 0; j < 3; ++j) {
            result[i] = (byte)(result[i] + r.r[x * 3 + j] * this.r[j * 3 + y]);
         }
      }

      this.r = result;
   }

   public void rotate(int face) {
      if (face == 0) {
         this.multiply(new Rotation(2, 0));
      } else if (face == 1) {
         this.multiply(new Rotation(0, 2));
      } else if (face == 2) {
         this.multiply(new Rotation(0, 1));
      } else if (face == 3) {
         this.multiply(new Rotation(1, 0));
      } else if (face == 4) {
         this.multiply(new Rotation(1, 2));
      } else if (face == 5) {
         this.multiply(new Rotation(2, 1));
      }

   }

   private Rotation(int axis1, int axis2) {
      int to1 = axis1 * 3 + axis2;
      int to2 = axis2 * 3 + axis1;
      this.r[to1] = 1;
      this.r[to2] = -1;

      for(int i = 0; i < 3; ++i) {
         if (i != axis1 && i != axis2) {
            this.r[i * 3 + i] = 1;
         }
      }

   }

   public Rotation() {
      this.r[0] = 1;
      this.r[4] = 1;
      this.r[8] = 1;
   }

   private int normalize(int x) {
      if (x < 0) {
         x += x / 8 * 8 + 8;
      }

      x %= 8;
      return x;
   }
}
