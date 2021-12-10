package com.ft.imdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.LogUtils
import com.ft.ftimkit.model.ChatType
import com.ft.imdemo.databinding.ActivityConversationBinding
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMGroupManager
import com.hyphenate.chat.EMGroupOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConversationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentUtils.add(
            supportFragmentManager,
            ConversationFragment.newInstance(),
            binding.flContainer.id
        )
        binding.fabChat.setOnClickListener {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val options = EMGroupOptions()
//                options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin
//                val allMembers = arrayOf("18501059401", "13037132267", "13012341234", "13030303031")
//                val createGroup = EMClient.getInstance().groupManager()
//                    .createGroup("九年级", "九年级·一期晚上 · 创新 · 杨昆", allMembers, null, options)
//                LogUtils.json(createGroup)
//            }
            ChatActivity.start(this, "165834159882241", ChatType.GroupChat.ordinal)
        }
    }
}