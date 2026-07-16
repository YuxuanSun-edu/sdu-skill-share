<template>
  <div class="panel auth-panel">
    <h2>注册</h2>
    <div class="form">
      <label class="field">
        <span>账号</span>
        <input v-model="form.username" placeholder="请输入账号" />
      </label>
      <label class="field">
        <span>密码</span>
        <input v-model="form.password" placeholder="请输入密码" type="password" />
      </label>
      <label class="field">
        <span>昵称</span>
        <input v-model="form.nickname" placeholder="请输入昵称" />
      </label>
      <label class="field">
        <span>手机号</span>
        <input v-model="form.phone" placeholder="请输入手机号" />
      </label>
      <label class="field">
        <span>邮箱</span>
        <input v-model="form.email" placeholder="请输入邮箱" />
      </label>
      <button @click="doRegister">注册</button>
      <p class="muted">{{ message }}</p>
      <div class="auth-footer">
        <span class="muted">已有账号？</span>
        <RouterLink to="/login">去登录</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api'

const router = useRouter()
const message = ref('')
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: ''
})

const doRegister = async () => {
  if (!form.username || !form.password || !form.nickname || !form.phone || !form.email) {
    message.value = '请完整填写注册信息'
    return
  }
  try {
    await register({ ...form })
    message.value = '注册成功，请登录'
    router.push('/login')
  } catch (error) {
    message.value = error.message || '注册失败'
  }
}
</script>
