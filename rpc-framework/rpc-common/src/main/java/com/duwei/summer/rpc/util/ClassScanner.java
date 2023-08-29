package com.duwei.summer.rpc.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassScanner {
    public static List<Class<?>> scan(String basePackage, Class<? extends Annotation> clazz) {
        return getAllClassNames(basePackage).stream()
                .map((className) -> {
                    try {
                        return Class.forName(className);
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(tClass -> tClass.isAnnotationPresent(clazz))
                .collect(Collectors.toList());
    }

    /**
     * 获取包及其子包下的所有类的全类名
     *
     * @param basePackage 基础包
     * @return 全类名列表
     */
    public static List<String> getAllClassNames(String basePackage) {
        String basePath = basePackage.replaceAll("\\.", "/");
        URL url = ClassLoader.getSystemClassLoader().getResource(basePath);
        if (url == null) {
            throw new RuntimeException("包扫描时，发现路径不存在.");
        }
        String absolutePath = url.getPath();

        List<String> classNames = new ArrayList<>();
        recursionFile(absolutePath, classNames, basePath);
        return classNames;
    }

    private static void recursionFile(String absolutePath, List<String> classNames, String basePath) {
        // 获取文件
        File file = new File(absolutePath);
        // 判断文件是否是文件夹
        if (file.isDirectory()) {
            // 找到文件夹的所有的文件
            File[] children = file.listFiles(pathname -> pathname.isDirectory() || pathname.getPath().endsWith(".class"));
            if (children == null || children.length == 0) {
                return;
            }
            for (File child : children) {
                if (child.isDirectory()) {
                    // 递归调用
                    recursionFile(child.getAbsolutePath(), classNames, basePath);
                } else {
                    // 文件 --> 类的权限定名称
                    String className = getClassNameByAbsolutePath(child.getAbsolutePath(), basePath);
                    classNames.add(className);
                }
            }

        } else {
            // 文件 --> 类的全限定名称
            String className = getClassNameByAbsolutePath(absolutePath, basePath);
            classNames.add(className);
        }
    }

    private static String getClassNameByAbsolutePath(String absolutePath, String basePath) {
        String fileName = absolutePath
                .substring(absolutePath.indexOf(basePath.replaceAll("/", "\\\\")))
                .replaceAll("\\\\", ".");
        fileName = fileName.substring(0, fileName.indexOf(".class"));
        return fileName;
    }

}