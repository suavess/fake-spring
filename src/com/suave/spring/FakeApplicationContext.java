package com.suave.spring;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;

/**
 * @author suave
 */
public class FakeApplicationContext {

    private Class configClass;

    public FakeApplicationContext(Class configClass) {
        this.configClass = configClass;
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            // 获取扫描注解
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 扫描路径
            String path = componentScanAnnotation.value();
            path = path.replace(".", "/");
            // 类加载器
            ClassLoader classLoader = this.getClass().getClassLoader();
            URL resource = classLoader.getResource(path);
            // 封装file
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                // 如果是目录，获取目录下的所有files
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f.getName().endsWith(".class")) {
                        // 是class文件，通过类加载器加载
                        String classNameWithoutSuffix = f.getName().substring(0, f.getName().indexOf(".class"));
                        Class<?> clazz = null;
                        try {
                            clazz = classLoader.loadClass(path.replace("/", ".") + "." + classNameWithoutSuffix);
                            if (clazz.isAnnotationPresent(Component.class)) {
                                 // 如果是component注解，则添加到容器中
                                                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                 
            }
        }
    }

    public Object getBean(String beanName) {
        return null;
    }
}
