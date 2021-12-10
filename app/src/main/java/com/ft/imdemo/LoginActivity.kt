package com.ft.imdemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.ft.imdemo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            val account = binding.etAccount.text.toString()
            val password = binding.etPassword.text.toString()
            if (password.isEmpty() || account.isEmpty()) {
                ToastUtils.showLong("请输入账号或密码")
                return@setOnClickListener
            }
            mViewModel.login(account, password)
        }
        binding.btnRegister.setOnClickListener {
            ActivityUtils.startActivity(RegisterActivity::class.java)
        }
        mViewModel.mLoginLiveData.observe(this, {
            if (it) {
                ActivityUtils.startActivity(ConversationActivity::class.java)
                finish()
            }
        })

    }
}