package com.ft.imdemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ToastUtils
import com.ft.imdemo.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val mViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            val account = binding.etAccount.text.toString()
            val password = binding.etPassword.text.toString()
            if (password.isEmpty() || account.isEmpty()) {
                ToastUtils.showLong("请输入账号或密码")
                return@setOnClickListener
            }
            mViewModel.register(account, password)
        }
        mViewModel.mRegisterLiveData.observe(this, {
            if (it) {
                finish()
            } else {
                ToastUtils.showLong("注册失败")
            }
        })
    }

}