package hx.utils;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class BlockLoader {
   public final Block block;
   public final Class tileEntityClass;
   @SideOnly(Side.CLIENT)
   public int renderID;

   public BlockLoader(Block block, Class clazz) {
      this.block = block;
      this.tileEntityClass = clazz;
   }

   public void load() {
      GameRegistry.registerBlock(this.block, this.block.getClass().getSimpleName());
      if (this.tileEntityClass != null) {
         GameRegistry.registerTileEntity(this.tileEntityClass, this.tileEntityClass.getSimpleName());
      }

   }

   @SideOnly(Side.CLIENT)
   public void registerRendering(ISimpleBlockRenderingHandler blockRenderer, TileEntitySpecialRenderer tileRenderer) {
      if (blockRenderer != null) {
         this.renderID = RenderingRegistry.getNextAvailableRenderId();
         RenderingRegistry.registerBlockHandler(this.renderID, blockRenderer);
      }

      if (tileRenderer != null) {
         ClientRegistry.bindTileEntitySpecialRenderer(this.tileEntityClass, tileRenderer);
      }

   }
}
