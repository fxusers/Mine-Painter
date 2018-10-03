package hx.utils;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

public class ItemLoader {
   public Item item;

   public ItemLoader(Item item) {
      this.item = item;
   }

   public void load(String name) {
      GameRegistry.registerItem(this.item, name);
   }

   public void load() {
      this.load(this.item.getClass().getSimpleName().replace("$", "_"));
   }

   @SideOnly(Side.CLIENT)
   public void registerRendering(IItemRenderer renderer) {
      MinecraftForgeClient.registerItemRenderer(this.item, renderer);
   }
}
