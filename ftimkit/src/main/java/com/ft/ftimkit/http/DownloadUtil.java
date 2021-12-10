package com.ft.ftimkit.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ft.ftimkit.interfaces.IFangIMProgressFileCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {

    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;

    private Handler mHandler;

    public static DownloadUtil get() {

        if (downloadUtil == null) {
            synchronized (DownloadUtil.class) {
                if (downloadUtil == null) {
                    downloadUtil = new DownloadUtil();
                }
            }
        }
        return downloadUtil;
    }

    public DownloadUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(15 * 1000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new ProgressInterceptor())
                .addInterceptor(new HeaderInterceptor());

        okHttpClient = builder.build();
        mHandler = new Handler(Looper.getMainLooper());
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     * @param listener     下载监听
     */

    public void download(final String url, final String destFileDir, final String destFileName, IFangIMProgressFileCallback listener) {

        if (url == null) {
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                // 下载失败监听回调
                if (listener != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            String str = "url:" + url + ",destFileDir:" + destFileDir + ",destFileName:" + destFileName;
                            listener.onFailure(-1, str + ",message:" + e.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("DownloadUtils", "thread:" + Thread.currentThread().toString());
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        if (listener != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onProgress(progress);
                                }
                            });
                        }

                    }
                    fos.flush();
                    //下载完成
                    if (listener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(file);
                            }
                        });
                    }
                } catch (Exception e) {

                    if (listener != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String str = "url:" + url + ",destFileDir:" + destFileDir + ",destFileName:" + destFileName;
                                listener.onFailure(-1, str + ",message:" + e.getMessage());
                            }
                        });
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });
    }
}
