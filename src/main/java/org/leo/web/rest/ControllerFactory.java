package org.leo.web.rest;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.leo.web.annotation.DeleteMapping;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PatchMapping;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.PutMapping;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RestController;
import org.leo.web.rest.mapping.ControllerBean;
import org.leo.web.rest.mapping.ControllerMappingRegistry;
import org.leo.web.rest.mapping.DeleteMappingRegisterStrategy;
import org.leo.web.rest.mapping.GetMappingRegisterStrategy;
import org.leo.web.rest.mapping.PatchMappingRegisterStrategy;
import org.leo.web.rest.mapping.PostMappingRegisterStrategy;
import org.leo.web.rest.mapping.PutMappingRegisterStrategy;
import org.leo.web.rest.mapping.RequestMappingRegisterContext;
import org.leo.web.rest.mapping.RequestMappingRegisterStrategy;

/**
 * 请求映射工厂类
 * 
 * @author Leo
 * @date 2018/3/16
 */
public final class ControllerFactory {

    private final static Logger logger = LoggerFactory.getLogger(ControllerFactory.class);

    /**
     * 注册Mpping
     * 
     * @param basePackage
     */
    public void registerController(String basePackage) {
        Set<Class<?>> controllerClasses = findClassesByPackage(basePackage);
        for (Class<?> controllerClass : controllerClasses) {
            registerClass(controllerClass);
        }
    }

    /**
     * 扫描包路径下所有的class文件
     *
     * @param packageName
     * @return
     */
    private Set<Class<?>> findClassesByPackage(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>(64);
        String pkgDirName = packageName.replace('.', '/');
        try {
            Enumeration<URL> urls = ControllerFactory.class.getClassLoader().getResources(pkgDirName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    // 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 获取包的物理路径
                    findClassesByFile(filePath, packageName, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(packageName, jar, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 扫描包下的所有class文件
     * 
     * @param path
     * @param packageName
     * @param classes
     */
    private void findClassesByFile(String path, String packageName, Set<Class<?>> classes) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles(filter -> filter.isDirectory() || filter.getName().endsWith("class"));
        for (File f : files) {
            if (f.isDirectory()) {
                findClassesByFile(packageName + "." + f.getName(), path + "/" + f.getName(), classes);
                continue;
            }

            // 获取类名，去掉 ".class" 后缀
            String className = f.getName();
            className = packageName + "." + className.substring(0, className.length() - 6);

            // 加载类
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Class not found, {}", className);
            }
            if (clazz != null && clazz.getAnnotation(RestController.class) != null) {
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
    }

    /**
     * 扫描包路径下的所有class文件
     *
     * @param packageName
     *            包名
     * @param jar
     *            jar文件
     * @param classes
     *            保存包路径下class的集合
     */
    private static void findClassesByJar(String packageName, JarFile jar, Set<Class<?>> classes) {
        String pkgDir = packageName.replace(".", "/");
        Enumeration<JarEntry> entry = jar.entries();
        while (entry.hasMoreElements()) {
            JarEntry jarEntry = entry.nextElement();

            String jarName = jarEntry.getName();
            if (jarName.charAt(0) == '/') {
                jarName = jarName.substring(1);
            }
            if (jarEntry.isDirectory() || !jarName.startsWith(pkgDir) || !jarName.endsWith(".class")) {
                // 非指定包路径， 非class文件
                continue;
            }

            // 获取类名，去掉 ".class" 后缀
            String[] classNameSplit = jarName.split("/");
            String className = packageName + "." + classNameSplit[classNameSplit.length - 1];
            if(className.endsWith(".class")) {
                className = className.substring(0, className.length() - 6);
            }

            // 加载类
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Class not found, {}", className);
            }
            if (clazz != null && clazz.getAnnotation(RestController.class) != null) {
                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
    }

    /**
     * 注册类
     * 
     * @param clazz
     */
    private void registerClass(Class<?> clazz) {
        String className = clazz.getName();
        logger.info("Registered REST Controller: {}", className);
        ControllerBean bean = new ControllerBean(clazz, clazz.getAnnotation(RestController.class).singleton());
        ControllerMappingRegistry.registerBean(className, bean);

        String url = null;
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            url = requestMapping.value();
        }
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            RequestMappingRegisterStrategy strategy = null;
            // 遍历所有method，生成ControllerMapping并注册。
            if(method.getAnnotation(GetMapping.class) != null) {
                strategy = new GetMappingRegisterStrategy();
            } else if(method.getAnnotation(PostMapping.class) != null) {
                strategy = new PostMappingRegisterStrategy();
            } else if(method.getAnnotation(PutMapping.class) != null) {
                strategy = new PutMappingRegisterStrategy();
            } else if(method.getAnnotation(DeleteMapping.class) != null) {
                strategy = new DeleteMappingRegisterStrategy();
            } else if(method.getAnnotation(PatchMapping.class) != null) {
                strategy = new PatchMappingRegisterStrategy();
            }
            
            if(strategy != null) {
                RequestMappingRegisterContext mappingRegCtx = new RequestMappingRegisterContext(strategy);
                mappingRegCtx.registerMapping(clazz, url, method);
            }
        }
    }

}
