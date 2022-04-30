package com.suave.spring;

import com.suave.spring.annotations.Autowired;
import com.suave.spring.annotations.Component;
import com.suave.spring.annotations.ComponentScan;
import com.suave.spring.annotations.Scope;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suave
 */
public class FakeApplicationContext {

    private Class configClass;

    private final Map<String, BeanDefinition> beanDefinitionMap  = new ConcurrentHashMap<>();

    private final Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>();

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
                                Component component = clazz.getAnnotation(Component.class);
                                String beanName = component.value();
                                if ("".equals(beanName)) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    // 如果是scope注解，则设置scope
                                    beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // 创建bean实例
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if ("singleton".equals(beanDefinition.getScope())) {
                Object bean = createBean(beanName, beanDefinition);
                singletonBeanMap.put(beanName, bean);
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class type = beanDefinition.getType();
        Object instance = null;
        try {
            instance = type.getConstructor().newInstance();
            // 依赖注入
            for (Field declaredField : type.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    // 如果存在依赖注入注解，就设置一下这个值
                    declaredField.setAccessible(true);
                    declaredField.set(instance, getBean(declaredField.getName()));
                }
            }
            // 判断是否实现了beanNameAware接口
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return instance;
    }


    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }
        String scope = beanDefinition.getScope();
        if ("singleton".equals(scope)) {
            // 单例
            Object bean = singletonBeanMap.get(beanName);
            if (bean == null) {
                Object createBean = createBean(beanName, beanDefinition);
                singletonBeanMap.put(beanName, createBean);
                return createBean;
            }
            return bean;
        } else {
            // 多例
            return createBean(beanName, beanDefinition);
        }
    }
}
