package hx.minepainter.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.ModMinePainter;
import hx.minepainter.sculpture.Sculpture;
import hx.minepainter.sculpture.SculptureEntity;
import hx.utils.Utils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class DroppedSculptureItem extends Item {
   public DroppedSculptureItem() {
      this.setUnlocalizedName("dropped_sculpture");
   }

   public void readTo(ItemStack is, Sculpture sculpture) {
      if (is.hasTagCompound()) {
         sculpture.read(is.getTagCompound());
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister r) {
   }

   public boolean getShareTag() {
      return true;
   }

   public boolean onItemUse(ItemStack is, EntityPlayer ep, World w, int x, int y, int z, int face, float xs, float ys, float zs) {
      if (!w.getBlock(x, y, z).getMaterial().isSolid()) {
         return false;
      } else {
         ForgeDirection dir = ForgeDirection.getOrientation(face);
         int _x = x + dir.offsetX;
         int _y = y + dir.offsetY;
         int _z = z + dir.offsetZ;
         if (!w.isAirBlock(_x, _y, _z)) {
            return false;
         } else if (!ep.canPlayerEdit(x, y, z, face, is)) {
            return false;
         } else {
            w.setBlock(_x, _y, _z, ModMinePainter.sculpture.block);
            SculptureEntity se = (SculptureEntity)Utils.getTE(w, _x, _y, _z);
            se.sculpture().read(is.getTagCompound());
            if (!ep.capabilities.isCreativeMode) {
               --is.stackSize;
            }

            return true;
         }
      }
   }
}
