package hx.minepainter.painting;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandImportPainting extends CommandBase {
   private static LinkedBlockingQueue tasks = new LinkedBlockingQueue();
   private static Thread worker;

   public String getCommandName() {
      return "mpimport";
   }

   public String getCommandUsage(ICommandSender var1) {
      return "mpimport <image url> [--size <w> <h>]\nto import image as w * h pieces of 16x16 paintings";
   }

   public void processCommand(ICommandSender var1, String[] var2) {
      this.startWorking();
      int w = true;
      int h = true;
      String url = var2[0];

      for(int i = 0; i < var2.length; ++i) {
         if (var2[i].equals("--size") && var2.length - i > 2) {
            int w = Integer.parseInt(var2[i + 1]);
            int var10 = Integer.parseInt(var2[i + 2]);
         }
      }

      try {
         BufferedImage var11 = ImageIO.read(new URL(url));
      } catch (MalformedURLException var7) {
         var7.printStackTrace();
      } catch (IOException var8) {
         var8.printStackTrace();
      }

   }

   private void startWorking() {
      if (worker == null || !worker.isAlive()) {
         worker = new Thread() {
            public void run() {
               while(true) {
                  try {
                     ((Runnable)CommandImportPainting.tasks.take()).run();
                  } catch (InterruptedException var2) {
                     var2.printStackTrace();
                  }
               }
            }
         };
         worker.start();
      }

   }
}
