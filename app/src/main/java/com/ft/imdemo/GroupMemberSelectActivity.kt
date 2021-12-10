package com.ft.imdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ft.ftimkit.ui.ChatFragment
import com.ft.ftimkit.util.IMConstants
import com.ft.imdemo.databinding.ActivityGroupMemberSelectBinding

/**
 * @author LDL
 * @date: 2021/11/8
 * @description:
 */
class GroupMemberSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupMemberSelectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMemberSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAdd.setOnClickListener {
            val intent = Intent()
            intent.apply {
                val membersNames = "abc bcd a"
                val membersIds = "123 456 789"
                putExtra(IMConstants.USER_NAME_SELECT, membersNames)
                putExtra(IMConstants.USER_ID_SELECT, membersIds)
                putExtras(this)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}