<template>
  <div>
    <header class="topbar">
      <RouterLink class="brand" to="/">山威技遇</RouterLink>
      <nav>
        <RouterLink to="/skills">技能大厅</RouterLink>
        <RouterLink to="/requests">需求大厅</RouterLink>
        <RouterLink v-if="isLogin" to="/messages">消息中心</RouterLink>
        <RouterLink v-if="isLogin" to="/profile">我的主页</RouterLink>
        <RouterLink v-if="isAdmin" to="/admin">管理后台</RouterLink>
      </nav>
      <div class="row">
        <span v-if="isLogin" class="muted">{{ displayName }}</span>
        <button v-if="isLogin" @click="switchAccount">切换账号</button>
        <button v-if="isLogin" class="ghost" @click="logout">退出账号</button>
        <RouterLink v-else class="login" to="/login">登录</RouterLink>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const user = ref(readUser())

function readUser() {
  return JSON.parse(localStorage.getItem('user') || 'null')
}

watch(
  () => route.fullPath,
  () => {
    user.value = readUser()
  }
)

const isLogin = computed(() => Boolean(user.value?.id))
const isAdmin = computed(() => user.value?.username === 'admin')
const displayName = computed(() => user.value?.nickname || user.value?.username || '')

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
}

const switchAccount = () => {
  logout()
}
</script>
