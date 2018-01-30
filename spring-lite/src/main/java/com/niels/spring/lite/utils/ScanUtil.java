package com.niels.spring.lite.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Niels Wang
 * @date 2018/1/30
 */
public class ScanUtil {
    private static final ClassLoader classLoader = ScanUtil.class.getClassLoader();

    public static final String FILE_TYPE = "file";
    public static final String JAR_TYPE = "jar";

    public static final String CLASS_TYPE = ".class";

    public static List<Class> scan(final String packageName) {
        List<Class> list = new ArrayList<>();
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (null == dirs) {
            return list;
        }
        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();
            if (FILE_TYPE.equals(protocol)) {
                findClassLocal(packageName, url.getPath(), list);
            } else if (JAR_TYPE.equals(protocol)) {
                findClassJar(packageName, url.getPath(), list);
            }
        }

        return list;
    }

    /**
     * @param packageName
     */
    public static void findClassLocal(final String packageName, String filePath, List<Class> list) {
        File file = new File(filePath);
        file.listFiles(new LocalFileFilter(packageName, list));
    }

    /**
     * @param packageName
     */
    private static void findClassJar(final String packageName, String pathName, List<Class> list) {
        JarFile jarFile;
        try {
            URL url = classLoader.getResource(pathName);
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            jarFile = jarURLConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException("could not be parsed as a URI reference");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (!jarEntryName.contains(pathName) || jarEntryName.equals(pathName + "/")) {
                continue;
            }
            if (jarEntry.isDirectory()) {
                String clazzName = jarEntry.getName().replace("/", ".");
                int endIndex = clazzName.lastIndexOf(".");
                String prefix = null;
                if (endIndex > 0) {
                    prefix = clazzName.substring(0, endIndex);
                }
                findClassJar(prefix,jarEntry.getName(), list);
            } else if (jarEntry.getName().endsWith(CLASS_TYPE)) {
                Class<?> clazz;
                try {
                    clazz = classLoader.loadClass(jarEntry.getName().replace("/", ".").replace(CLASS_TYPE, ""));
                } catch (ClassNotFoundException e) {
                    continue;
                }
                list.add(clazz);
            }

        }

    }

    static class LocalFileFilter implements FileFilter {

        private List<Class> list;
        private String packageName;

        public LocalFileFilter(String packageName, List<Class> list) {
            this.list = list;
            this.packageName = packageName;
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                findClassLocal(packageName+"."+file.getName(), file.getPath(), list);
                return true;
            }
            if (file.getName().endsWith(CLASS_TYPE)) {
                Class<?> clazz;
                try {
                    clazz = classLoader.loadClass(packageName + "." + file.getName().replace(CLASS_TYPE, ""));
                } catch (ClassNotFoundException e) {
                    return false;
                }
                list.add(clazz);
                return true;
            }
            return false;
        }
    }
}
