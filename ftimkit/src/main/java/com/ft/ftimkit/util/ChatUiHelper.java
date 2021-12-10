package com.ft.ftimkit.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ft.ftimkit.R;
import com.ft.ftimkit.emoji.EmojiAdapter;
import com.ft.ftimkit.emoji.EmojiBean;
import com.ft.ftimkit.emoji.EmojiDao;
import com.ft.ftimkit.emoji.EmojiVpAdapter;
import com.ft.ftimkit.event.AtMemberSelectEvent;
import com.ft.ftimkit.model.ChatType;
import com.ft.ftimkit.widget.IndicatorView;
import com.ft.ftimkit.widget.RecordButton;
import com.ft.ftimkit.widget.TIMMentionEditText;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChatUiHelper {
    private static final String SHARE_PREFERENCE_NAME = "com.chat.ui";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";
    private Activity mActivity;
    private LinearLayout mContentLayout;//整体界面布局
    private RelativeLayout mBottomLayout;//底部布局
    private LinearLayout mEmojiLayout;//表情布局
    private LinearLayout mAddLayout;//添加布局
    private Button mSendBtn;//发送按钮
    private View mAddButton;//加号按钮
    private Button mAudioButton;//录音按钮
    private ImageView mAudioIv;//录音图片


    private TIMMentionEditText mEditText;
    private InputMethodManager mInputManager;
    private SharedPreferences mSp;
    private ImageView mIvEmoji;

    private ChatType mChatType = ChatType.GroupChat;
    private Map<String, String> atUserInfoMap = new HashMap<>();
    private String displayInputString;

    public ChatUiHelper() {

    }

    public static ChatUiHelper with(Activity activity) {
        ChatUiHelper mChatUiHelper = new ChatUiHelper();
        //   AndroidBug5497Workaround.assistActivity(activity);
        mChatUiHelper.mActivity = activity;
        mChatUiHelper.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatUiHelper.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return mChatUiHelper;
    }

    public static final int EVERY_PAGE_SIZE = 21;
    private List<EmojiBean> mListEmoji;

    public ChatUiHelper bindEmojiData(View rootView) {

        mListEmoji = EmojiDao.getInstance().getEmojiBean();
        //  LogUtil.d("获取到的表情集合"+Arrays.asList(mListEmoji));
        LinearLayout homeEmoji = rootView.findViewById(R.id.home_emoji);
        ViewPager vpEmoji = rootView.findViewById(R.id.vp_emoji);
        final IndicatorView indEmoji = rootView.findViewById(R.id.ind_emoji);
        LinearLayout.LayoutParams layoutParams12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        //将RecyclerView放至ViewPager中：
        int pageSize = EVERY_PAGE_SIZE;
        EmojiBean mEmojiBean = new EmojiBean();
        mEmojiBean.setId("0");
        mEmojiBean.setUnicodeInt(000);
        int deleteCount = (int) Math.ceil(mListEmoji.size() * 1.0 / EVERY_PAGE_SIZE);//要显示的删除键的数量
        LogUtils.d("" + deleteCount);
        //添加删除键
        for (int i = 1; i < deleteCount + 1; i++) {
            if (i == deleteCount) {
                mListEmoji.add(mListEmoji.size(), mEmojiBean);
            } else {
                mListEmoji.add(i * EVERY_PAGE_SIZE - 1, mEmojiBean);
            }
            LogUtils.d("添加次数" + i);

        }


        int pageCount = (int) Math.ceil((mListEmoji.size()) * 1.0 / pageSize);//一共的页数
        LogUtils.d("总共的页数:" + pageCount);
        List<View> viewList = new ArrayList<View>();
        for (int index = 0; index < pageCount; index++) {
            //每个页面创建一个recycleview
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.item_emoji_vprecy, vpEmoji, false);
            recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 7));
            EmojiAdapter entranceAdapter;
            if (index == pageCount - 1) {
                //最后一页的数据
                List<EmojiBean> lastPageList = mListEmoji.subList(index * EVERY_PAGE_SIZE, mListEmoji.size());
                entranceAdapter = new EmojiAdapter(lastPageList, index, EVERY_PAGE_SIZE);
            } else {
                entranceAdapter = new EmojiAdapter(mListEmoji.subList(index * EVERY_PAGE_SIZE, (index + 1) * EVERY_PAGE_SIZE), index, EVERY_PAGE_SIZE);
            }

            entranceAdapter.setOnItemClickListener((adapter, view, position) -> {
                EmojiBean emojiBean = (EmojiBean) adapter.getData().get(position);
                if (emojiBean.getId().equals("0")) {
                    //如果是删除键
                    mEditText.dispatchKeyEvent(new KeyEvent(
                            KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    int i = mEditText.getSelectionStart();
                    Editable editable = mEditText.getText();
                    editable.insert(i, emojiBean.getId());
                    QDQQFaceManager.handlerEmojiText(mEditText, editable.toString(), true);
//                    mEditText.append(((EmojiBean) adapter.getData().get(position)).getUnicodeInt());
                }
            });
            recyclerView.setAdapter(entranceAdapter);
            viewList.add(recyclerView);
        }
        EmojiVpAdapter adapter = new EmojiVpAdapter(viewList);
        vpEmoji.setAdapter(adapter);
        indEmoji.setIndicatorCount(vpEmoji.getAdapter().getCount());
        indEmoji.setCurrentIndicator(vpEmoji.getCurrentItem());
        vpEmoji.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indEmoji.setCurrentIndicator(position);
            }
        });
        return this;
    }


    //绑定整体界面布局
    public ChatUiHelper bindContentLayout(LinearLayout bottomLayout) {
        mContentLayout = bottomLayout;
        return this;
    }


    //绑定输入框
    @SuppressLint("ClickableViewAccessibility")
    public ChatUiHelper bindEditText(TIMMentionEditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && mBottomLayout.isShown()) {
                lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                hideBottomLayout(true);//隐藏表情布局，显示软件盘
                mIvEmoji.setImageResource(R.mipmap.ic_emoji);
                //软件盘显示后，释放内容高度
                mEditText.postDelayed(() -> unlockContentHeightDelayed(), 200L);
            }
            return false;
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mEditText.getText().toString().trim().length() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                    mAddButton.setVisibility(View.GONE);
                } else {
                    mSendBtn.setVisibility(View.GONE);
                    mAddButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEditText.setOnMentionInputListener(tag -> {
            if ((tag.equals(TIMMentionEditText.TIM_METION_TAG) || tag.equals(TIMMentionEditText.TIM_METION_TAG_FULL))){
//                    && ChatHelper.isGroupChat(mChatType)) {
                LiveEventBus.get(AtMemberSelectEvent.class)
                        .post(new AtMemberSelectEvent());
            }
        });
        return this;
    }

    //绑定底部布局
    public ChatUiHelper bindBottomLayout(RelativeLayout bottomLayout) {
        mBottomLayout = bottomLayout;
        return this;
    }


    //绑定表情布局
    public ChatUiHelper bindEmojiLayout(LinearLayout emojiLayout) {
        mEmojiLayout = emojiLayout;
        return this;
    }

    //绑定添加布局
    public ChatUiHelper bindAddLayout(LinearLayout addLayout) {
        mAddLayout = addLayout;
        return this;
    }

    //绑定发送按钮
    public ChatUiHelper bindttToSendButton(Button sendbtn) {
        mSendBtn = sendbtn;
        return this;
    }

    //绑定语音按钮点击事件
    public ChatUiHelper bindAudioBtn(RecordButton audioBtn) {
        mAudioButton = audioBtn;
        return this;
    }

    //绑定语音图片点击事件
    public ChatUiHelper bindAudioIv(ImageView audioIv) {
        mAudioIv = audioIv;
        audioIv.setOnClickListener(view -> PermissionX.init((FragmentActivity) mActivity)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储权限
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                ).request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        MediaManager.release();
                        //如果录音按钮显示
                        if (mAudioButton.isShown()) {
                            hideAudioButton();
                            mEditText.requestFocus();
                            showSoftInput();
                        } else {
                            mEditText.clearFocus();
                            showAudioButton();
                            hideEmotionLayout();
                            hideMoreLayout();
                        }
                    } else {
                        ToastUtils.showShort("请添加相关权限");
                    }
                }));

        // UIUtils.postTaskDelay(() -> mRvMsg.smoothMoveToPosition(mRvMsg.getAdapter().getItemCount() - 1), 50);
        return this;
    }

    //绑定语音按钮点击事件
    public ChatUiHelper bindChatType(int chatType) {
        mChatType = chatType == ChatType.Chat.ordinal() ? ChatType.Chat : ChatType.GroupChat;
        return this;
    }

    private void hideAudioButton() {
        mAudioButton.setVisibility(View.GONE);
        mEditText.setVisibility(View.VISIBLE);
        mAudioIv.setImageResource(R.mipmap.ic_audio);
    }


    private void showAudioButton() {
        mAudioButton.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.GONE);
        mAudioIv.setImageResource(R.mipmap.ic_keyboard);
        if (mBottomLayout.isShown()) {
            hideBottomLayout(false);
        } else {
            hideSoftInput();
        }
    }


    //绑定表情按钮点击事件
    public ChatUiHelper bindToEmojiButton(ImageView emojiBtn) {
        mIvEmoji = emojiBtn;
        emojiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.requestFocus();
                if (!mEmojiLayout.isShown()) {
                    if (mAddLayout.isShown()) {
                        showEmotionLayout();
                        hideMoreLayout();
                        hideAudioButton();
                        return;
                    }
                } else if (mEmojiLayout.isShown() && !mAddLayout.isShown()) {
                    mIvEmoji.setImageResource(R.mipmap.ic_emoji);
                    if (mBottomLayout.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        hideBottomLayout(true);//隐藏表情布局，显示软件盘
                        unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                    } else {
                        if (isSoftInputShown()) {//同上
                            lockContentHeight();
                            showBottomLayout();
                            unlockContentHeightDelayed();
                        } else {
                            showBottomLayout();//两者都没显示，直接显示表情布局
                        }
                    }


                    return;
                }
                showEmotionLayout();
                hideMoreLayout();
                hideAudioButton();
                if (mBottomLayout.isShown()) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideBottomLayout(true);//隐藏表情布局，显示软件盘
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                } else {
                    if (isSoftInputShown()) {//同上
                        lockContentHeight();
                        showBottomLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showBottomLayout();//两者都没显示，直接显示表情布局
                    }
                }
            }
        });
        return this;
    }


    //绑定底部加号按钮
    public ChatUiHelper bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.clearFocus();
                hideAudioButton();
                if (mBottomLayout.isShown()) {
                    if (mAddLayout.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        hideBottomLayout(true);//隐藏表情布局，显示软件盘
                        unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                    } else {
                        showMoreLayout();
                        hideEmotionLayout();
                    }
                } else {
                    if (isSoftInputShown()) {//同上
                        hideEmotionLayout();
                        showMoreLayout();
                        lockContentHeight();
                        showBottomLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showMoreLayout();
                        hideEmotionLayout();
                        showBottomLayout();//两者都没显示，直接显示表情布局
                    }
                }
            }
        });
        return this;
    }


    private void hideMoreLayout() {
        mAddLayout.setVisibility(View.GONE);
    }

    private void showMoreLayout() {
        mAddLayout.setVisibility(View.VISIBLE);
    }


    /**
     * 隐藏底部布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    public void hideBottomLayout(boolean showSoftInput) {
        if (mBottomLayout.isShown()) {
            mBottomLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void showBottomLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_TAG, dip2Px(270));
        }
        hideSoftInput();
        mBottomLayout.getLayoutParams().height = softInputHeight;
        mBottomLayout.setVisibility(View.VISIBLE);
    }


    private void showEmotionLayout() {
        mEmojiLayout.setVisibility(View.VISIBLE);
        mIvEmoji.setImageResource(R.mipmap.ic_keyboard);
    }

    private void hideEmotionLayout() {
        mEmojiLayout.setVisibility(View.GONE);
        mIvEmoji.setImageResource(R.mipmap.ic_emoji);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    public boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    public int dip2Px(int dip) {
        float density = mActivity.getApplicationContext().getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }


    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }


    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /*  *
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。*/
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        if (isNavigationBarExist(mActivity)) {
            softInputHeight = softInputHeight - getNavigationHeight(mActivity);
        }
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }


    public void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentLayout.getLayoutParams();
        params.height = mContentLayout.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    public void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentLayout.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }


    private static final String NAVIGATION = "navigationBarBackground";

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != View.NO_ID &&
                        NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public void updateInputText(String names, String ids) {
        if (names == null || ids == null || names.isEmpty() || ids.isEmpty()) {
            return;
        }

        updateAtUserInfoMap(names, ids);
        if (mEditText != null) {
            mEditText.setText(String.format("%s%s", mEditText.getText(), displayInputString));
            mEditText.setSelection(Objects.requireNonNull(mEditText.getText()).length());
        }
    }

    private void updateAtUserInfoMap(String names, String ids) {
        displayInputString = "";

        if (ids.equals(IMConstants.SELECT_ALL)) {
            atUserInfoMap.put(names, ids);

            //for display
            displayInputString += names;
            displayInputString += " ";
            displayInputString += TIMMentionEditText.TIM_METION_TAG;
        } else {
            String[] listName = names.split(" ");
            String[] listId = ids.split(" ");

            //此处防止昵称和ID不对等
            boolean isListName = (listName.length >= listId.length);
            int i = 0;
            if (isListName) {
                for (i = 0; i < listId.length; i++) {
                    atUserInfoMap.put(listName[i], listId[i]);

                    //for display
                    displayInputString += listName[i];
                    displayInputString += " ";
                    displayInputString += TIMMentionEditText.TIM_METION_TAG;
                }
            } else {
                for (i = 0; i < listName.length; i++) {
                    atUserInfoMap.put(listName[i], listId[i]);

                    //for display
                    displayInputString += listName[i];
                    displayInputString += " ";
                    displayInputString += TIMMentionEditText.TIM_METION_TAG;
                }
            }
        }

        if (!displayInputString.isEmpty()) {
            displayInputString = displayInputString.substring(0, displayInputString.length() - 1);
        }
    }
}
