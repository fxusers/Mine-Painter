package hx.minepainter.painting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class PaintingEntity extends TileEntity {
   BufferedImage image = new BufferedImage(16, 16, 2);
   @SideOnly(Side.CLIENT)
   private PaintingIcon icon;

   public PaintingEntity() {
      for(int i = 0; i < 16; ++i) {
         this.image.setRGB(i, 0, this.getColorForDye(i) | -16777216);
      }

      Graphics g = this.image.getGraphics();
      g.setColor(Color.white);
      g.fillRect(0, 1, 16, 15);
   }

   public int getColorForDye(int dye_index) {
      return ItemDye.field_150922_c[dye_index];
   }

   public BufferedImage getImg() {
      return this.image;
   }

   @SideOnly(Side.CLIENT)
   public PaintingIcon getIcon() {
      if (this.icon == null) {
         this.icon = PaintingCache.get();
      }

      return this.icon;
   }

   public void invalidate() {
      super.invalidate();
      if (super.worldObj.isRemote) {
         this.getIcon().release();
         this.icon = null;
      }

   }

   public void onChunkUnload() {
      super.onChunkUnload();
      if (super.worldObj.isRemote) {
         this.getIcon().release();
         this.icon = null;
      }

   }

   public void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      this.writeImageToNBT(nbt);
   }

   public void writeImageToNBT(NBTTagCompound nbt) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         ImageIO.write(this.image, "png", baos);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      nbt.setByteArray("image_data", baos.toByteArray());
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.readFromNBTToImage(nbt);
      super.readFromNBT(nbt);
   }

   public void readFromNBTToImage(NBTTagCompound nbt) {
      if (nbt != null) {
         byte[] data = nbt.getByteArray("image_data");
         ByteArrayInputStream bais = new ByteArrayInputStream(data);

         try {
            BufferedImage img = ImageIO.read(bais);
            this.image = img;
            if (super.worldObj != null && super.worldObj.isRemote) {
               this.getIcon().fill(img);
            }
         } catch (IOException var5) {
            this.getIcon().fill(this.image);
         }

      }
   }

   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.writeToNBT(nbttagcompound);
      return new S35PacketUpdateTileEntity(super.xCoord, super.yCoord, super.zCoord, 17, nbttagcompound);
   }

   public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.func_148857_g());
   }
}
