package common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class used to create instances of all classes in a package when the application is loaded.
 *
 * This was implemented because registration of message handlers and messages is done inside a
 * static constructor. Therefore, there is no guarantee it executes until the first instance of
 * that class is created. However, that can happen, for example, when a message of that type is received,
 * and, at that point, the message should have already been initialized.
 *
 * The solution was to create an instance of every class in that subpackage (with some exceptions) explicitly
 * when the program starts.
 */
public class ClassLoader {

    /**
     * Loads all classes in the given package, with the provided exceptions
     * @param packageName the name of the package
     * @param exclude the names of the classes not to load
     */
    public static void loadClasses(String packageName, Collection<String> exclude) {
        try {
            Set<Class> classes = listClasses(packageName, exclude);

            for (Class c : classes)
                c.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to load classes", e);
        }
    }

    /**
     * Lists all classes in the given package, with the provided exceptions
     * @param packageName the name of the package
     * @param exclude the names of the classes not to load
     *
     * @return a set of all the requested classes
     */
    private static Set<Class> listClasses(String packageName, Collection<String> exclude) {

        InputStream stream = java.lang.ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class") && !isExcluded(line, exclude))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toSet());
    }

    /**
     * Returns whether the given class is excluded from loading
     *
     * @implNote a class is said to be excluded from loading if and only if
     * its name contains any of the strings in the excluded
     * collection.
     *
     * @param str the class name (not fully qualified)
     * @param exclude a collection with the names of all excluded classes
     * @return whether the given class is excluded from loading
     */
    private static boolean isExcluded(String str, Collection<String> exclude) {
        for(String s : exclude) {
            if(str.contains(s))
                return true;
        }

        return false;
    }

    /**
     * Gets the class object from the class and package names
     * @param className the name of the class
     * @param packageName the name of the package
     * @return the class object
     */
    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found " + className);
        }
    }
}
