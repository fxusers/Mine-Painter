package hx.minepainter;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hx.minepainter.item.CanvasItem;
import hx.minepainter.item.CanvasRenderer;
import hx.minepainter.item.ChiselItem;
import hx.minepainter.item.DroppedSculptureItem;
import hx.minepainter.item.DroppedSculptureRenderer;
import hx.minepainter.item.Palette;
import hx.minepainter.item.PieceItem;
import hx.minepainter.item.PieceRenderer;
import hx.minepainter.item.WrenchItem;
import hx.minepainter.painting.PaintTool;
import hx.minepainter.painting.PaintingBlock;
import hx.minepainter.painting.PaintingEntity;
import hx.minepainter.painting.PaintingOperationMessage;
import hx.minepainter.painting.PaintingRenderer;
import hx.minepainter.sculpture.SculptureBlock;
import hx.minepainter.sculpture.SculptureEntity;
import hx.minepainter.sculpture.SculptureEntityRenderer;
import hx.minepainter.sculpture.SculptureOperationMessage;
import hx.minepainter.sculpture.SculptureRender;
import hx.utils.BlockLoader;
import hx.utils.ItemLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

@Mod(
   modid = "minepainter",
   version = "0.2.6"
)
public class ModMinePainter {
   public static CreativeTabs tabMinePainter = new CreativeTabs("minepainter") {
      public Item getTabIconItem() {
         return ModMinePainter.mixerbrush.item;
      }
   };
   public static BlockLoader sculpture = new BlockLoader(new SculptureBlock(), SculptureEntity.class);
   public static BlockLoader painting = new BlockLoader(new PaintingBlock(), PaintingEntity.class);
   public static ItemLoader chisel = new ItemLoader(new ChiselItem());
   public static ItemLoader barcutter = new ItemLoader(new ChiselItem.Barcutter());
   public static ItemLoader saw = new ItemLoader(new ChiselItem.Saw());
   public static ItemLoader piece = new ItemLoader(new PieceItem());
   public static ItemLoader bar = new ItemLoader(new PieceItem.Bar());
   public static ItemLoader cover = new ItemLoader(new PieceItem.Cover());
   public static ItemLoader droppedSculpture = new ItemLoader(new DroppedSculptureItem());
   public static ItemLoader wrench = new ItemLoader(new WrenchItem());
   public static ItemLoader minibrush = new ItemLoader(new PaintTool.Mini());
   public static ItemLoader mixerbrush = new ItemLoader(new PaintTool.Mixer());
   public static ItemLoader bucket = new ItemLoader(new PaintTool.Bucket());
   public static ItemLoader eraser = new ItemLoader(new PaintTool.Eraser());
   public static ItemLoader palette = new ItemLoader(new Palette());
   public static ItemLoader canvas = new ItemLoader(new CanvasItem());
   public static SimpleNetworkWrapper network;

   @cpw.mods.fml.common.Mod.EventHandler
   public void preInit(FMLInitializationEvent e) {
      sculpture.load();
      painting.load();
      chisel.load();
      barcutter.load();
      saw.load();
      piece.load();
      bar.load();
      cover.load();
      droppedSculpture.load();
      wrench.load();
      minibrush.load();
      mixerbrush.load();
      bucket.load();
      eraser.load();
      palette.load();
      canvas.load();
      (new Crafting()).registerRecipes();
      MinecraftForge.EVENT_BUS.register(new EventHandler());
      network = NetworkRegistry.INSTANCE.newSimpleChannel("minepainter");
      network.registerMessage(SculptureOperationMessage.SculptureOperationHandler.class, SculptureOperationMessage.class, 0, Side.SERVER);
      network.registerMessage(PaintingOperationMessage.PaintingOperationHandler.class, PaintingOperationMessage.class, 1, Side.SERVER);
   }

   @SideOnly(Side.CLIENT)
   @cpw.mods.fml.common.Mod.EventHandler
   public void preInitClient(FMLInitializationEvent e) {
      sculpture.registerRendering(new SculptureRender(), new SculptureEntityRenderer());
      painting.registerRendering((ISimpleBlockRenderingHandler)null, new PaintingRenderer());
      piece.registerRendering(new PieceRenderer());
      bar.registerRendering(new PieceRenderer.Bar());
      cover.registerRendering(new PieceRenderer.Cover());
      droppedSculpture.registerRendering(new DroppedSculptureRenderer());
      canvas.registerRendering(new CanvasRenderer());
   }
}
