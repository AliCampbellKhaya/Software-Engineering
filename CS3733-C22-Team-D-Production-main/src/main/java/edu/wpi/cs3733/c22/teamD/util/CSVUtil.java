package edu.wpi.cs3733.c22.teamD.util;

import edu.wpi.cs3733.c22.teamD.database.entity.ServiceRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSVUtil {
  /**
   * Create a .csv file using Java Reflection
   *
   * @param arrayList The array list of objects to read.
   * @param <T> A generic type.
   */
  public static <T> void generateCSV(File filename, ArrayList<T> arrayList)
      throws FileNotFoundException {
    /* If the array is empty, then return nothing */
    if (arrayList.isEmpty()) return;

    /* Instantiate the writer */
    final PrintWriter writer = new PrintWriter(filename);

    /* Get the class type of the objects in the array */
    final Class<?> type = arrayList.get(0).getClass();

    /* Get the name of all the attributes */
    final ArrayList<Field> classAttributes = new ArrayList<>(List.of(type.getDeclaredFields()));

    boolean doesExtend = ServiceRequest.class.isAssignableFrom(type);
    if (doesExtend) {
      final Class<?> superType = arrayList.get(0).getClass().getSuperclass();
      classAttributes.addAll(0, (List.of(superType.getDeclaredFields())));
    }

    /* Write the parsed attributes to the file */
    writer.println(classAttributes.stream().map(Field::getName).collect(Collectors.joining(",")));

    /* For each object, read each attribute and append it to the file with a comma separating */
    arrayList.forEach(
        obj -> {
          writer.println(
              classAttributes.stream()
                  .map(
                      attribute -> {
                        attribute.setAccessible(true);
                        String output = "";
                        try {
                          output = attribute.get(obj).toString();
                        } catch (IllegalAccessException | ClassCastException e) {
                          System.err.println("[CSVUtil] Object attribute access error.");
                        }
                        return output;
                      })
                  .collect(Collectors.joining(",")));
          writer.flush();
        });
    writer.close();
  }
}
