package bootstrap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XXX on 2017/8/31.
 */
public class Bootstrap {


    public static String APPLICATION_NAME = null;
    private static final String EXECUTOR_CLASSNAME = "com.xyl.spark.schedule.common.MainExecutor";


    /**
     * 主程序入口
     * @param args
     */
    public static void main(String[] args){
        File rootFile = new File(".");
        File libDir = new File("lib"); //依赖jar
        File commonDir = new File("common"); //工具包
        List<URL> urlList = new ArrayList<URL>();
        loadJarList(libDir, urlList);
        loadJarList(commonDir, urlList);
        if(args != null || args.length > 0){
            File distDir = new File(args[0]);
            loadJarList(distDir, urlList);
            APPLICATION_NAME = args[0];
        }
        URLClassLoader classLoader = new URLClassLoader(urlList.toArray(new URL[0]), Bootstrap.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Executor executor = (Executor) classLoader.loadClass(EXECUTOR_CLASSNAME).newInstance();
            executor.executor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadJarList(File dir, List<URL> list){
        File[] files = dir.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                loadJarList(file, list);
            }else if(file.isFile()){
                String fileName = file.getName().toLowerCase();
                if(fileName.endsWith(".jar")){
                    try {
                        list.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
