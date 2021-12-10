
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RelativeLayout
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.ft.ftimkit.R
import com.ft.ftimkit.util.BitmapUtil
import com.ft.ftimkit.util.GlideApp
import com.ft.ftimkit.util.image.IChatGlideLoader

/**
 * @author LDL
 * @date: 2021/5/24
 * @description:图片加载器实现类
 */
object ChatGlideLoader : IChatGlideLoader {


    /**
     * 加载一般图片
     * @param url 图片链接
     * */
    override fun load(context: Context, url: String, imageView: ImageView) {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.mipmap.default_img_failed) // 正在加载中的图片
            .error(R.mipmap.default_img_failed) // 加载失败的图片
        GlideApp.with(context)
            .load(url)
            .apply(options)
            .centerCrop()
            .into(imageView)
    }


    override fun loadCircle(context: Context, url: String, imageView: ImageView) {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.mipmap.default_img_failed) // 正在加载中的图片
            .error(R.mipmap.default_img_failed) // 加载失败的图片
        GlideApp.with(context)
            .load(if (url.isEmpty()) "" else url)
            .apply(options)
            .circleCrop()
            .into(imageView)
    }


    override fun loadRounded(
        context: Context,
        url: String,
        roundingRadius: Int,
        imageView: ImageView
    ) {
        GlideApp.with(context)
            .load(url)
            .apply(
                RequestOptions.bitmapTransform(RoundedCorners(ConvertUtils.dp2px(roundingRadius.toFloat())))
                    .override(imageView.width, imageView.height)
            )
            .into(imageView)
    }

    override fun loadChatImage(context: Context, url: String, imageView: ImageView) {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.mipmap.default_img_failed) // 正在加载中的图片
            .error(R.mipmap.default_img_failed) // 加载失败的图片
        GlideApp.with(context)
            .load(url) // 图片地址
            .apply(options)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val imageSize = BitmapUtil.getImageSize((resource as BitmapDrawable).bitmap)
                    val imageLP = imageView.layoutParams as RelativeLayout.LayoutParams
                    imageLP.width = imageSize.width
                    imageLP.height = imageSize.height
                    imageView.layoutParams = imageLP
                    GlideApp.with(context)
                        .load(resource)
                        .apply(options) // 参数
                        .into(imageView)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

}