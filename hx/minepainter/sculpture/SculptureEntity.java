package hx.minepainter.sculpture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class SculptureEntity extends TileEntity {
   Sculpture sculpture = new Sculpture();
   @SideOnly(Side.CLIENT)
   private SculptureRenderCompiler render;

   public Sculpture sculpture() {
      return this.sculpture;
   }

   @SideOnly(Side.CLIENT)
   public SculptureRenderCompiler getRender() {
      if (this.render == null) {
         this.render = new SculptureRenderCompiler();
      }

      return this.render;
   }

   @SideOnly(Side.CLIENT)
   public void updateRender() {
      if (super.worldObj.isRemote) {
         BlockSlice slice = BlockSlice.at(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
         this.getRender().update(slice);
         BlockSlice.clear();
      }

   }

   @SideOnly(Side.CLIENT)
   public void invalidate() {
      super.invalidate();
      if (super.worldObj.isRemote) {
         this.getRender().clear();
      }

   }

   @SideOnly(Side.CLIENT)
   public void onChunkUnload() {
      super.onChunkUnload();
      if (super.worldObj.isRemote) {
         this.getRender().clear();
      }

   }

   public void writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      this.sculpture.write(nbt);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      if (super.worldObj != null && super.worldObj.isRemote) {
         this.getRender().changed = true;
      }

      super.readFromNBT(nbt);
      this.sculpture.read(nbt);
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
