package com.baidu.autocut.tieba;

import com.baidu.autocut.tieba.util.UtilHelper;
import com.tinify.Source;
import com.tinify.Tinify;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengxin on 15/11/3.
 */
public class ResizeImageMultiThreadMain {

    final static String DIV = System.getProperties().getProperty("file.separator");
    private static LinkedBlockingQueue<Runnable> mBlockList = new LinkedBlockingQueue<>(20);
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 60, 1000, TimeUnit.DAYS, mBlockList);
    private static LinkedBlockingQueue<File> mFileList = new LinkedBlockingQueue<>(10);
    private static volatile boolean mHasMore = true;

    private static class TinyRunnable implements Runnable {


        @Override
        public void run() {

            File mFile = null;
            try {
                while (true) {
                    mFile=mFileList.poll();
                    if(mFile == null && !mHasMore){
                        break;
                    }else {
                        mFile = mFileList.take();
                    }
                    System.out.println("take=" + mFile.getName() + mFileList.size());
                    if (!mFile.getName().endsWith("png") && !mFile.getName().endsWith("jpg")) {
                        continue;
                    }
                    String folderPath = mFile.getParent() + "_opt" + DIV;

                    System.out.println(folderPath + mFile.getName());
//                    Thread.sleep(400);
                    synchronized (DIV) {
                        File folder = new File(folderPath);
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                    }
                    Source source = Tinify.fromFile(mFile.getAbsolutePath());
                    source.toFile(folderPath + mFile.getName());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.print("thread over=" + Thread.currentThread().getId());

        }
    }

    public static void main(String[] args) {
        Tinify.setKey("guglRCuGSFqZzsV4saNvl7MAYhbG5JS9");//zhengxin01
        Tinify.setKey("wPaBZeidsFCKdfkwjo0_cjHOL_MOeRQI");//zhengxin02
        File adknodpi = new File("/Users/zhengxin/Documents/workspace_baobao/fabu_6.19/temp");

        for (int i = 0; i < 20; i++) {
            executor.execute(new TinyRunnable());
        }
        UtilHelper.commonDft(adknodpi, new UtilHelper.ICommonFileCallBack() {
            @Override
            public void dealFile(File file) {
                System.out.println("add=" + file.getAbsoluteFile() + mFileList.size());
                try {
                    mFileList.put(file);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHasMore = false;
            }
        });

    }
}
