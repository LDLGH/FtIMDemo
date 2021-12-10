package com.ft.ftimkit.util;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.nbsp.materialfilepicker.MaterialFilePicker;

/**
 * TODO
 *
 * @author stephen.shen
  * @date 2018-08-06 21:59
 */
public class PictureFileUtil {

    /**
     * 从相册选择图片
      * @param mContext     上下文信息
       */
    public static void openGalleryPic(Activity mContext, int requstcode) {
        // 进入相册 不需要的api可以不写
        PictureSelector.create(mContext)
                .openGallery(PictureMimeType.ofImage())
                .selectionMode(PictureConfig.MULTIPLE)
                .isEnableCrop(false)
                .isCompress(true)
                .minimumCompressSize(100)
                .imageEngine(GlideEngine.createGlideEngine())
                .forResult(requstcode);
    }




    public static void openGalleryAudio(Activity mContext , int requstcode ) {
        // 进入相册 不需要的api可以不写
        PictureSelector.create(mContext)
                .openGallery(PictureMimeType.ofVideo())//
                .selectionMode(PictureConfig.SINGLE)
                .isEnableCrop(false)
                .isCompress(true)
                .isPreviewVideo(true)// 是否可预览视频
                .isEnablePreviewAudio(true) // 是否可播放音频
                .imageEngine(GlideEngine.createGlideEngine())
                .forResult(requstcode);
    }


    public static void openFile(Activity mContext, int requestCode) {
        new MaterialFilePicker()
                .withActivity(mContext)
                .withCloseMenu(true)
                .withRequestCode(requestCode)
                //       .withFilter(Pattern.compile(".*\\.txt$")) // Filtering files and directories by file name using regexp
                .withFilterDirectories(false) // Set directories filterable (false by default)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }
}
