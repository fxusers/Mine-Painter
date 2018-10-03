package hx.minepainter.painting;

import java.util.HashMap;
import java.util.Iterator;

public abstract class ExpirablePool {
   final int expire;
   HashMap timeouts = new HashMap();
   HashMap items = new HashMap();
   boolean running = false;

   public ExpirablePool(int expire) {
      this.expire = expire;
   }

   public void start() {
      this.running = true;
      (new Thread(new Runnable() {
         public void run() {
            while(ExpirablePool.this.running) {
               Iterator iter = ExpirablePool.this.timeouts.keySet().iterator();

               while(iter.hasNext()) {
                  Object t = iter.next();
                  int count = (Integer)ExpirablePool.this.timeouts.get(t);
                  if (count <= 0) {
                     iter.remove();
                     ExpirablePool.this.release(ExpirablePool.this.items.remove(t));
                  } else {
                     ExpirablePool.this.timeouts.put(t, count - 1);
                  }
               }

               try {
                  Thread.sleep(80L);
               } catch (InterruptedException var4) {
                  var4.printStackTrace();
               }

               if (ExpirablePool.this.items.isEmpty()) {
                  ExpirablePool.this.running = false;
               }
            }

         }
      })).start();
   }

   public abstract void release(Object var1);

   public abstract Object get();

   public void stop() {
      this.running = false;
   }

   public boolean contains(Object t) {
      return this.items.containsKey(t);
   }

   public Object get(Object t) {
      if (!this.items.containsKey(t)) {
         this.items.put(t, this.get());
      }

      this.timeouts.put(t, this.expire);
      return this.items.get(t);
   }
}
