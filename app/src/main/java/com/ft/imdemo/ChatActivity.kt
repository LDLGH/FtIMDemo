package com.ft.imdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.ToastUtils
import com.ft.ftimkit.event.AtMemberSelectEvent
import com.ft.ftimkit.model.ChatType
import com.ft.ftimkit.ui.ChatFragment
import com.ft.imdemo.databinding.ActivityChatBinding
import com.hjq.xtoast.XToast
import com.hjq.xtoast.draggable.SpringDraggable
import com.jeremyliao.liveeventbus.LiveEventBus


class ChatActivity : AppCompatActivity() {

    companion object {

        const val TO_CHAT_ID = "to_chat_id"
        const val CHAT_TYPE = "chat_type"

        @JvmStatic
        fun start(context: Context, toChatId: String, chatType: Int = ChatType.Chat.ordinal) {
            val starter = Intent(context, ChatActivity::class.java)
                .putExtra(TO_CHAT_ID, toChatId)
                .putExtra(CHAT_TYPE, chatType)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var mChatFragment: ChatFragment
    private val launcherGroupMemberSelectActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            mChatFragment.updateInputText(data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toChatId = intent.getStringExtra(TO_CHAT_ID)
        val chatType = intent.getIntExtra(CHAT_TYPE, ChatType.Chat.ordinal)
        mChatFragment = ChatFragment.getInstance(toChatId!!, chatType)
        FragmentUtils.add(
            supportFragmentManager,
            mChatFragment,
            binding.flContainer.id
        )
        LiveEventBus.get(AtMemberSelectEvent::class.java)
            .observe(this, {
                launcherGroupMemberSelectActivity.launch(
                    Intent(
                        this,
                        GroupMemberSelectActivity::class.java
                    )
                )
            })
        binding.dfb.setOnClickListener {
            ToastUtils.showLong("被点击")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mChatFragment.onActivityResult(requestCode, resultCode, data)
    }
}