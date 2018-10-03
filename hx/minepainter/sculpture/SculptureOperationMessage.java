package hx.minepainter.sculpture;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class SculptureOperationMessage implements IMessage {
   int[] pos = new int[4];
   int x;
   int y;
   int z;
   Block block;
   int meta;
   int flags;

   public SculptureOperationMessage() {
   }

   public SculptureOperationMessage(int[] pos, int x, int y, int z, Block block, int meta, int flags) {
      this.pos = pos;
      this.x = x;
      this.y = y;
      this.z = z;
      this.block = block;
      this.meta = meta;
      this.flags = flags;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos[0] = buf.readByte();
      this.pos[1] = buf.readByte();
      this.pos[2] = buf.readByte();
      this.pos[3] = buf.readByte();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.block = Block.getBlockById(buf.readInt());
      this.meta = buf.readByte();
      this.flags = buf.readByte();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.pos[0]);
      buf.writeByte(this.pos[1]);
      buf.writeByte(this.pos[2]);
      buf.writeByte(this.pos[3]);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeInt(Block.getIdFromBlock(this.block));
      buf.writeByte(this.meta);
      buf.writeByte(this.flags);
   }

   public static class SculptureOperationHandler implements IMessageHandler 
   {
      public IMessage onMessage(SculptureOperationMessage message, MessageContext ctx) 
      {
         World w = ctx.getServerHandler().playerEntity.worldObj;
         if (Operations.validOperation(w, message.x, message.y, message.z, message.pos, message.flags)) 
         {
            Operations.applyOperation(w, message.x, message.y, message.z, message.pos, message.flags, message.block, message.meta);
         }

         EntityPlayer ep = ctx.getServerHandler().playerEntity;
         ItemStack is = ep.getCurrentEquippedItem();
         if ((message.flags & 16) > 0) 
         {
            is.damageItem(1, ep);
         } 
         else 
         if ((32 & message.flags) > 0 && !ep.capabilities.isCreativeMode) 
         {
            --is.stackSize;
            if (is.stackSize <= 0) 
            {
               ForgeEventFactory.onPlayerDestroyItem(ep, is);
               ep.inventory.mainInventory[ep.inventory.currentItem] = null;
            }
         }

         return null;
      }
   }
}