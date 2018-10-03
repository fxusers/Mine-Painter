package hx.minepainter.painting;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import net.minecraft.item.ItemStack;

public class PaintingCache {
   public static final int res = 256;
   private static LinkedList sheets = new LinkedList();
   private static ExpirablePool item_pool = new ExpirablePool(12) {
      public void release(PaintingIcon v) {
         v.release();
      }

      public PaintingIcon get() {
         return PaintingCache.get();
      }
   };

   public static PaintingIcon get() {
      Iterator i$ = sheets.iterator();

      PaintingSheet sheet;
      do {
         if (!i$.hasNext()) {
            PaintingSheet sheet = new PaintingSheet(256);
            sheets.add(sheet);
            return sheet.get();
         }

         sheet = (PaintingSheet)i$.next();
      } while(sheet.isEmpty());

      return sheet.get();
   }

   public static PaintingIcon get(ItemStack is) {
      boolean upload = !item_pool.contains(is);
      PaintingIcon pi = (PaintingIcon)item_pool.get(is);
      if (!item_pool.running) {
         item_pool.start();
      }

      if (upload) {
         try {
            byte[] data = is.getTagCompound().getByteArray("image_data");
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            BufferedImage img = ImageIO.read(bais);
            pi.fill(img);
         } catch (IOException var6) {
            ;
         }
      }

      return pi;
   }
}
