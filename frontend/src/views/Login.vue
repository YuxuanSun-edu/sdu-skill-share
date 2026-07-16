<template>
  <div class="panel auth-panel">
    <h2>登录</h2>
    <div class="form">
      <label class="field">
        <span>账号</span>
        <input v-model="form.username" placeholder="请输入账号" />
      </label>
      <label class="field">
        <span>密码</span>
        <input v-model="form.password" placeholder="请输入密码" type="password" />
      </label>
      <button @click="doLogin">登录</button>
      <p class="muted">{{ message }}</p>
      <div class="auth-footer">
        <span class="muted">还没有账号？</span>
        <RouterLink to="/register">去注册</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api'

const router = useRouter()
const message = ref('')
const form = reactive({ username: '', password: '' })

const doLogin = async () => {
  if (!form.username || !form.password) {
    message.value = '请输入账号和密码'
    return
  }
  try {
    const data = await login({ username: form.username, password: form.password })
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(data.user))
    message.value = '登录成功'
    router.push('/profile')
  } catch (error) {
    message.value = error.message || '登录失败'
  }
}
</script>
