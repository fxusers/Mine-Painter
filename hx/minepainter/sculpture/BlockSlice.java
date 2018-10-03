package hx.minepainter.sculpture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class BlockSlice implements IBlockAccess {
   IBlockAccess iba;
   int x;
   int y;
   int z;
   Sculpture sculpture;
   int brightness;
   private static BlockSlice instance = new BlockSlice();

   public static BlockSlice at(IBlockAccess iba, int x, int y, int z) {
      instance.iba = iba;
      instance.x = x;
      instance.y = y;
      instance.z = z;
      TileEntity te = iba.getTileEntity(x, y, z);
      if (te != null && te instanceof SculptureEntity) {
         instance.sculpture = ((SculptureEntity)te).sculpture;
      } else {
         instance.sculpture = null;
      }

      return instance;
   }

   public static BlockSlice of(Sculpture sculpture, int brightness) {
      instance.iba = null;
      instance.sculpture = sculpture;
      instance.brightness = brightness;
      return instance;
   }

   public static void clear() {
      instance.iba = null;
   }

   public Block getBlock(int x, int y, int z) {
      if (this.sculpture != null) {
         Sculpture var10000 = this.sculpture;
         if (Sculpture.contains(x, y, z)) {
            return this.sculpture.getBlockAt(x, y, z, this);
         }
      }

      return this.iba == null ? Blocks.air : this.iba.getBlock(this.x + cap(x), this.y + cap(y), this.z + cap(z));
   }

   public TileEntity getTileEntity(int x, int y, int z) {
      return this.iba == null ? null : this.iba.getTileEntity(this.x + cap(x), this.y + cap(y), this.z + cap(z));
   }

   @SideOnly(Side.CLIENT)
   public int getLightBrightnessForSkyBlocks(int x, int y, int z, int var4) {
      return this.iba == null ? this.brightness : this.iba.getLightBrightnessForSkyBlocks(this.x + cap(x), this.y + cap(y), this.z + cap(z), var4);
   }

   public int getBlockMetadata(int x, int y, int z) {
      if (this.sculpture != null) {
         Sculpture var10000 = this.sculpture;
         if (Sculpture.contains(x, y, z)) {
            return this.sculpture.getMetaAt(x, y, z, this);
         }
      }

      return this.iba == null ? 0 : this.iba.getBlockMetadata(this.x + cap(x), this.y + cap(y), this.z + cap(z));
   }

   public boolean isAirBlock(int x, int y, int z) {
      if (this.sculpture != null) {
         Sculpture var10000 = this.sculpture;
         if (Sculpture.contains(x, y, z)) {
            return this.sculpture.getBlockAt(x, y, z, this) == Blocks.air;
         }
      }

      return this.iba == null ? true : this.iba.isAirBlock(this.x + cap(x), this.y + cap(y), this.z + cap(z));
   }

   @SideOnly(Side.CLIENT)
   public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public int getHeight() {
      return this.iba == null ? 256 : this.iba.getHeight();
   }

   @SideOnly(Side.CLIENT)
   public boolean extendedLevelsInChunkCache() {
      return this.iba == null ? false : this.iba.extendedLevelsInChunkCache();
   }

   public Vec3Pool func_82732_R() {
      return this.iba == null ? null : this.iba.func_82732_R();
   }

   public int isBlockProvidingPowerTo(int x, int y, int z, int var4) {
      return this.iba == null ? 0 : this.iba.isBlockProvidingPowerTo(this.x + cap(x), this.y + cap(y), this.z + cap(z), var4);
   }

   public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
      return this.iba == null ? false : this.iba.isSideSolid(this.x + cap(x), this.y + cap(y), this.z + cap(z), side, _default);
   }

   private static int cap(int original) {
      return original > 7 ? original - 7 : (original >= 0 ? 0 : original);
   }
}
