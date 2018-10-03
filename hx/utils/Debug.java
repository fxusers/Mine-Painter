package hx.utils;

public class Debug {
   public static void log(Object... thing) {
      StringBuilder sb = new StringBuilder();
      Object[] arr$ = thing;
      int len$ = thing.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Object i = arr$[i$];
         sb.append(i + ", ");
      }

      System.err.println(sb.toString());
   }
}
