package org.dict.zip.cli;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Hiroshi Miura on 16/04/09.
 * @author Hiroshi Miura
 */
class StaticUtils {
   /**
    * Compare binary files. Both files must be files (not directories) and exist.
    *
    * @param first  - first file
    * @param second - second file
    * @return boolean - true if files are binery equal
    * @throws IOException - error in function
    */
   static boolean isFileBinaryEquals(File first, File second) throws IOException {
      return isFileBinaryEquals(first, second, 0, first.length());
   }

    /**
    * Compare binary files (for test). Both files must be files (not directories) and exist.
    *
    * @param first  - first file
    * @param second - second file
    * @param off - compare from offset
    * @param len - comparison length
    * @return boolean - true if files are binery equal
    * @throws IOException - error in function
    */
   static boolean isFileBinaryEquals( File first, File second, final long off, final long len) throws IOException {
      boolean result= false;
      final int BUFFER_SIZE = 65536;
      final int COMP_SIZE = 512;

      if (len <= 1) {
         throw new IllegalArgumentException();
      }

      if ((first.exists()) && (second.exists())
         && (first.isFile()) && (second.isFile())) {
         if (first.getCanonicalPath().equals(second.getCanonicalPath())) {
            result = true;
         } else {
            FileInputStream firstInput;
            FileInputStream secondInput;
            BufferedInputStream bufFirstInput = null;
            BufferedInputStream bufSecondInput = null;

            try {
               firstInput = new FileInputStream(first);
               secondInput = new FileInputStream(second);
               bufFirstInput = new BufferedInputStream(firstInput, BUFFER_SIZE);
               bufSecondInput = new BufferedInputStream(secondInput, BUFFER_SIZE);

               byte[] firstBytes = new byte[COMP_SIZE];
               byte[] secondBytes = new byte[COMP_SIZE];

               bufFirstInput.skip(off);
               bufSecondInput.skip(off);

               long readLengthTotal = 0;
               result = true;
               while (readLengthTotal < len) {
                  int readLength = COMP_SIZE;
                  if (len - readLengthTotal < (long) COMP_SIZE) {
                     readLength = (int) (len - readLengthTotal);
                  }
                  int lenFirst = bufFirstInput.read(firstBytes, 0, readLength);
                  int lenSecond = bufSecondInput.read(secondBytes, 0, readLength);
                  if (lenFirst != lenSecond) {
                     result = false;
                     break;
                  }
                  if ((lenFirst < 0) && (lenSecond < 0)) {
                     result = true;
                     break;
                  }
                  if (lenFirst < firstBytes.length) {
                     byte[] a = Arrays.copyOfRange(firstBytes, 0, lenFirst);
                     byte[] b = Arrays.copyOfRange(secondBytes, 0, lenSecond);
                     if (!Arrays.equals(a, b)) {
                        result = false;
                        break;
                     }
                  } else if (!Arrays.equals(firstBytes, secondBytes)) {
                     result = false;
                     break;
                  }
               }
            } catch (RuntimeException e) {
               throw e;
            } finally {
               try {
                  if (bufFirstInput != null) {
                     bufFirstInput.close();
                  }
               } finally {
                  if (bufSecondInput != null) {
                     bufSecondInput.close();
                  }
               }
            }
         }
      }

      return result;
   }

   /**
    * StaticUtils is static utility class.
    * It should not be instantiated.
    */
   private StaticUtils() {}
}
