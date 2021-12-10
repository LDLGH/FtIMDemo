package com.ft.imdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ft.ftimkit.model.ChatType
import com.ft.imdemo.adapter.ConversationListAdapter
import com.ft.imdemo.databinding.FragmentConversationBinding
import com.hyphenate.chat.EMConversation

class ConversationFragment : Fragment() {

    companion object {
        fun newInstance() = ConversationFragment()
    }

    private lateinit var viewModel: ConversationViewModel
    private lateinit var binding: FragmentConversationBinding
    private val mAdapter: ConversationListAdapter by lazy { ConversationListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConversationViewModel::class.java)
        initRv()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadLocalAllConversations()
    }

    private fun initRv() {
        binding.rvConversation.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val conversation = mAdapter.data[position].info as EMConversation
            val chatType =
                if (conversation.type == EMConversation.EMConversationType.Chat) ChatType.Chat else ChatType.GroupChat
            ChatActivity.start(requireActivity(), conversation.conversationId(), chatType.ordinal)
        }
        mAdapter.setOnItemLongClickListener { adapter, view, position ->
            showConversationDialog(position)
            return@setOnItemLongClickListener true
        }
    }

    private fun initObserver() {
        viewModel.mConversationsLiveData.observe(viewLifecycleOwner, {
            mAdapter.setList(it)
        })
        viewModel.mConversationUpdateLiveData.observe(viewLifecycleOwner, {
            viewModel.loadLocalAllConversations()
        })
    }

    private fun showConversationDialog(pos: Int) {
        val message = mAdapter.data[pos]
        val items = mutableListOf<String>()
        if (message.isTop) {
            items.add("取消置顶")
        } else {
            items.add("置顶")
        }
        items.add("删除")
        AlertDialog.Builder(requireActivity())
            .setTitle("操作")
            .setItems(
                items.toTypedArray()
            ) { dialog, which ->
                if (items[which] == "置顶") {
                    viewModel.makeConversationTop(message)
                    viewModel.loadLocalAllConversations()
                }
                if (items[which] == "删除") {
                    val conversation = message.info as EMConversation
                    viewModel.deleteConversation(conversation.conversationId())
                }
                if (items[which] == "取消置顶") {
                    viewModel.cancelConversationTop(message)
                    viewModel.loadLocalAllConversations()
                }
                dialog.dismiss()
            }
            .create().show()
    }
}