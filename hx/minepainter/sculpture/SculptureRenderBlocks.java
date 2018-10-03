package hx.minepainter.sculpture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class SculptureRenderBlocks extends RenderBlocks {
   private double[] overrideBounds = new double[6];
   public boolean[] drawFace = new boolean[6];

   public SculptureRenderBlocks() {
      for(int i = 0; i < 6; ++i) {
         this.drawFace[i] = true;
      }

   }

   public void cull(Sculpture sculpture, int x, int y, int z) {
      Block ours = sculpture.getBlockAt(x, y, z, (BlockSlice)null);

      for(int i = 0; i < 6; ++i) {
         ForgeDirection dir = ForgeDirection.getOrientation(i);
         int _x = x + dir.offsetX;
         int _y = y + dir.offsetY;
         int _z = z + dir.offsetZ;
         if (!Sculpture.contains(_x, _y, _z)) {
            this.drawFace[i] = true;
         } else {
            Block theirs = sculpture.getBlockAt(_x, _y, _z, (BlockSlice)null);
            if (theirs == ours) {
               this.drawFace[i] = false;
            } else if (theirs != Blocks.air && theirs.isOpaqueCube()) {
               this.drawFace[i] = false;
            } else {
               this.drawFace[i] = true;
            }
         }
      }

   }

   public void setRenderBoundsFromBlock(Block p_147775_1_) {
      super.setRenderBoundsFromBlock(p_147775_1_);
      this.render2override();
      this.renderFull();
   }

   private void override2render() {
      super.renderMinX = this.overrideBounds[0];
      super.renderMinY = this.overrideBounds[1];
      super.renderMinZ = this.overrideBounds[2];
      super.renderMaxX = this.overrideBounds[3];
      super.renderMaxY = this.overrideBounds[4];
      super.renderMaxZ = this.overrideBounds[5];
   }

   private void render2override() {
      this.overrideBounds[0] = super.renderMinX;
      this.overrideBounds[1] = super.renderMinY;
      this.overrideBounds[2] = super.renderMinZ;
      this.overrideBounds[3] = super.renderMaxX;
      this.overrideBounds[4] = super.renderMaxY;
      this.overrideBounds[5] = super.renderMaxZ;
   }

   private void renderFull() {
      super.renderMinX = 0.0D;
      super.renderMinY = 0.0D;
      super.renderMinZ = 0.0D;
      super.renderMaxX = 1.0D;
      super.renderMaxY = 1.0D;
      super.renderMaxZ = 1.0D;
   }

   public void renderFaceYNeg(Block p_147768_1_, double p_147768_2_, double p_147768_4_, double p_147768_6_, IIcon p_147768_8_) {
      if (this.drawFace[0]) {
         this.override2render();
         super.renderFaceYNeg(p_147768_1_, p_147768_2_, p_147768_4_, p_147768_6_, p_147768_8_);
         this.renderFull();
      }
   }

   public void renderFaceYPos(Block p_147806_1_, double p_147806_2_, double p_147806_4_, double p_147806_6_, IIcon p_147806_8_) {
      if (this.drawFace[1]) {
         this.override2render();
         super.renderFaceYPos(p_147806_1_, p_147806_2_, p_147806_4_, p_147806_6_, p_147806_8_);
         this.renderFull();
      }
   }

   public void renderFaceZNeg(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_, IIcon p_147761_8_) {
      if (this.drawFace[2]) {
         this.override2render();
         super.renderFaceZNeg(p_147761_1_, p_147761_2_, p_147761_4_, p_147761_6_, p_147761_8_);
         this.renderFull();
      }
   }

   public void renderFaceZPos(Block p_147734_1_, double p_147734_2_, double p_147734_4_, double p_147734_6_, IIcon p_147734_8_) {
      if (this.drawFace[3]) {
         this.override2render();
         super.renderFaceZPos(p_147734_1_, p_147734_2_, p_147734_4_, p_147734_6_, p_147734_8_);
         this.renderFull();
      }
   }

   public void renderFaceXNeg(Block p_147798_1_, double p_147798_2_, double p_147798_4_, double p_147798_6_, IIcon p_147798_8_) {
      if (this.drawFace[4]) {
         this.override2render();
         super.renderFaceXNeg(p_147798_1_, p_147798_2_, p_147798_4_, p_147798_6_, p_147798_8_);
         this.renderFull();
      }
   }

   public void renderFaceXPos(Block p_147764_1_, double p_147764_2_, double p_147764_4_, double p_147764_6_, IIcon p_147764_8_) {
      if (this.drawFace[5]) {
         this.override2render();
         super.renderFaceXPos(p_147764_1_, p_147764_2_, p_147764_4_, p_147764_6_, p_147764_8_);
         this.renderFull();
      }
   }
}
