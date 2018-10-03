package hx.minepainter.sculpture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import hx.utils.Utils;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class SculptureBlock extends BlockContainer {
   private int x;
   private int y;
   private int z;
   private int meta = 0;
   private Block current;
   private int renderID;

   public void setCurrentBlock(Block that, int meta) {
      if (that == null) {
         int meta = false;
         this.renderID = -1;
         this.current = Blocks.stone;
      } else {
         this.current = that;
         this.meta = meta;
         this.renderID = that.getRenderType();
      }
   }

   public void setSubCoordinate(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void dropScrap(World w, int x, int y, int z, ItemStack is) {
      this.dropBlockAsItem(w, x, y, z, is);
   }

   public SculptureBlock() {
      super(Material.rock);
      this.current = Blocks.stone;
      this.renderID = -1;
      this.setHardness(1.0F);
   }

   public MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 st, Vec3 ed) {
      SculptureEntity tile = (SculptureEntity)Utils.getTE(w, x, y, z);
      Sculpture sculpture = tile.sculpture();
      int[] pos = Operations.raytrace(sculpture, st.addVector((double)(-x), (double)(-y), (double)(-z)), ed.addVector((double)(-x), (double)(-y), (double)(-z)));
      if (pos[0] == -1) {
         return null;
      } else {
         ForgeDirection dir = ForgeDirection.getOrientation(pos[3]);
         Vec3 hit = null;
         if (dir.offsetX != 0) {
            hit = st.getIntermediateWithXValue(ed, (double)((float)x + (float)pos[0] / 8.0F + (float)(dir.offsetX + 1) / 16.0F));
         } else if (dir.offsetY != 0) {
            hit = st.getIntermediateWithYValue(ed, (double)((float)y + (float)pos[1] / 8.0F + (float)(dir.offsetY + 1) / 16.0F));
         } else if (dir.offsetZ != 0) {
            hit = st.getIntermediateWithZValue(ed, (double)((float)z + (float)pos[2] / 8.0F + (float)(dir.offsetZ + 1) / 16.0F));
         }

         if (hit == null) {
            return sculpture.isEmpty() ? super.collisionRayTrace(w, x, y, z, st, ed) : null;
         } else {
            return new MovingObjectPosition(x, y, z, pos[3], hit);
         }
      }
   }

   public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {
      SculptureEntity tile = (SculptureEntity)Utils.getTE(par1World, par2, par3, par4);
      Sculpture sculpture = tile.sculpture();

      for(int x = 0; x < 8; ++x) {
         for(int y = 0; y < 8; ++y) {
            for(int z = 0; z < 8; ++z) {
               if (sculpture.getBlockAt(x, y, z, (BlockSlice)null) != Blocks.air) {
                  this.setBlockBounds((float)x / 8.0F, (float)y / 8.0F, (float)z / 8.0F, (float)(x + 1) / 8.0F, (float)(y + 1) / 8.0F, (float)(z + 1) / 8.0F);
                  super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
               }
            }
         }
      }

      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
      if (iba.getBlock(x, y, z) == this.current) {
         return false;
      } else {
         return iba.isAirBlock(x, y, z) || !iba.getBlock(x, y, z).isOpaqueCube();
      }
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new SculptureEntity();
   }

   @SideOnly(Side.CLIENT)
   public void registerBlockIcons(IIconRegister p_149651_1_) {
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(int side, int meta) {
      return this.current.getIcon(side, this.meta);
   }

   @SideOnly(Side.CLIENT)
   public int getRenderType() {
      return this.renderID == -1 ? ModMinePainter.sculpture.renderID : this.renderID;
   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean renderAsNormalBlock() {
      return false;
   }

   public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
      SculptureEntity se = (SculptureEntity)Utils.getTE(world, x, y, z);
      NBTTagCompound nbt = new NBTTagCompound();
      ItemStack is = new ItemStack(ModMinePainter.droppedSculpture.item);
      se.sculpture.write(nbt);
      is.setTagCompound(nbt);
      return is;
   }

   public int getLightValue(IBlockAccess world, int x, int y, int z) {
      TileEntity te = world.getTileEntity(x, y, z);
      if (te != null && te instanceof SculptureEntity) {
         SculptureEntity se = (SculptureEntity)te;
         return se.sculpture.getLight();
      } else {
         return super.getLightValue(world, x, y, z);
      }
   }

   protected ItemStack createStackedBlock(int p_149644_1_) {
      return null;
   }

   public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
      return null;
   }

   public void breakBlock(World w, int x, int y, int z, Block b, int meta) {
      SculptureEntity se = (SculptureEntity)Utils.getTE(w, x, y, z);
      if (se != null && !se.sculpture().isEmpty()) {
         NBTTagCompound nbt = new NBTTagCompound();
         ItemStack is = new ItemStack(ModMinePainter.droppedSculpture.item);
         se.sculpture.write(nbt);
         is.setTagCompound(nbt);
         this.dropBlockAsItem(w, x, y, z, is);
         super.breakBlock(w, x, y, z, b, meta);
      } else {
         super.breakBlock(w, x, y, z, b, meta);
      }
   }
}
