package net.minecraft.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.command.TranslatableExceptionProvider;
import net.minecraft.util.datafix.NamespacedSchema;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION = Level.DISABLED;
   public static boolean developmentMode;
   /** Array of the special characters that are allowed in any text drawing of Minecraft. */
   public static final char[] ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

   /**
    * Checks if the given character is allowed to be put into chat.
    */
   public static boolean isAllowedCharacter(char character) {
      return character != 167 && character >= ' ' && character != 127;
   }

   /**
    * Filter a string, keeping only characters for which {@link #isAllowedCharacter(char)} returns true.
    *  
    * Note that this method strips line breaks, as {@link #isAllowedCharacter(char)} returns false for those.
    * @return A filtered version of the input string
    */
   public static String filterAllowedCharacters(String input) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : input.toCharArray()) {
         if (isAllowedCharacter(c0)) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   static {
      if (System.getProperty("io.netty.leakDetection.level") == null) // Forge: allow level to be manually specified
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new TranslatableExceptionProvider();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}