package com.xyl.spark.schedule.common.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by XXX on 2017/9/29.
 * <p>
 * shell命令行服务
 */

@Service
public class CommandService {

    static Log log = LogFactory.getLog(CommandService.class);

    private boolean[] syncObject = new boolean[0];


    /**
     * 带超时退出机制的命令行执行
     *
     * @param timeout
     * @param commands
     */
    public void execute(long timeout, String... commands) {

        CommandThread commandThread = new CommandThread("CommandService_" + StringUtils.join(commands, "_"), commands);
        commandThread.start();
        synchronized (syncObject){
            try {
                syncObject.wait(timeout);
            } catch (Exception e) {
                log.warn("InterruptedException", e);
            }
        }
        if (!commandThread.isFinished())
        {
            // 超时,需要关闭该命令窗口
            log.warn("Thread: " + commandThread.getName() + " is runned timeout!");
        }
        commandThread.getProcess().destroy();
    }

    private static Process nonBlockingExecute(String... commonds) {

        log.info("commands: " + StringUtils.join(commonds, "\t"));
        try {

            Process process = Runtime.getRuntime().exec(commonds);
            return process;

        } catch (Exception e) {
            log.error("commond executed failed! commond: " + StringUtils.join(commonds, "\t"), e);
            return null;
        }
    }


    /**
     * 执行一条命令,并根据enableLogging状态输出日志
     *
     * @param commands
     */
    public void execute(String... commands) {
        Process process = null;
        InputStream is = null;
        BufferedReader bufferedReader = null;
        try {

            long t1 = System.currentTimeMillis();
            process = nonBlockingExecute(commands);
            is = process.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (true) {
                line = bufferedReader.readLine();
                if(line == null){
                    break;
                }
                log.info(line);
            }
            long t2 = System.currentTimeMillis();
            log.info("command execute successful! use time: " + (t2 - t1) + "ms, command: " + StringUtils.join(commands, "\t"));
        } catch (Exception e) {
            log.error("command executed failed! command: " + StringUtils.join(commands, "\t"));
        }finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(is);
            if(process != null){
                process.destroy();
            }
        }

    }


    public static class CommandThread extends Thread {

        private String[] commonds;
        private Process process;
        private boolean finished = false;

        private CommandThread(String name, String[] commonds) {
            super(name);
            this.commonds = commonds;
        }

        @Override
        public void run() {
            this.finished = false;
            process = nonBlockingExecute(commonds);
            this.finished = true;
        }


        public boolean isFinished() {
            return finished;
        }

        public Process getProcess() {
            return process;
        }
    }

    public static void main(String[] args) throws Exception {
        String[] command = new String[]{"/bin/sh", "-c", "jps -m"};

//        nonBlockingExecute("/bin/sh", "-c", "jps -m");

//        CommandService commandService = new CommandService();
//        commandService.execute(command);
//        CommandThread commandThread = new CommandThread("CommandService_" + StringUtils.join(command, "_"), command);
//        commandThread.start();

        Process process = nonBlockingExecute(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        while (true){
            line = reader.readLine();
            if(line == null){
                break;
            }
            System.out.println(line);
        }
    }

}
