<template>
  <section class="between">
    <h2>需求大厅</h2>
    <RouterLink v-if="isLogin" class="login" to="/publish-request">发布需求</RouterLink>
  </section>

  <p class="muted" v-if="message">{{ message }}</p>

  <div class="grid">
    <article class="card" v-for="item in list" :key="item.requestId">
      <div class="between">
        <span class="tag">{{ item.category || '校园互助' }}</span>
        <span class="muted">{{ item.status === 'OPEN' ? '待接收' : item.status }}</span>
      </div>
      <h3>需求：{{ item.title }}</h3>
      <p class="muted">详细描述：{{ item.description }}</p>
      <p>预算：{{ item.budget || '面议' }}</p>
      <p>发布者：{{ publisherName(item.userId) }}</p>
      <div v-if="isLogin" class="row">
        <RouterLink v-if="isOwner(item)" class="login" :to="`/requests/${item.requestId}/edit`">修改</RouterLink>
        <button v-if="isOwner(item)" class="danger" @click="removeRequest(item.requestId)">删除</button>
        <button v-else class="ghost" @click="accept(item.requestId)">接受需求</button>
      </div>
    </article>
  </div>

  <p v-if="!list.length" class="muted">暂无开放需求</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { acceptRequest, deleteRequest, requestHall } from '../api'
import { nicknameById } from '../utils/userNames'

const list = ref([])
const message = ref('')
const nameMap = ref({})
const isLogin = Boolean(localStorage.getItem('token'))
const currentUser = () => JSON.parse(localStorage.getItem('user') || '{"id":1}')
const isOwner = item => Number(item.userId) === Number(currentUser().id)
const publisherName = id => nameMap.value[id] || '加载中'

const load = async () => {
  list.value = await requestHall()
  const ids = [...new Set(list.value.map(item => item.userId).filter(Boolean))]
  const pairs = await Promise.all(ids.map(async id => [id, await nicknameById(id)]))
  nameMap.value = Object.fromEntries(pairs)
}

const accept = async id => {
  try {
    await acceptRequest(id, currentUser().id)
    message.value = '需求接收成功'
    await load()
  } catch (error) {
    message.value = error.message || '接收失败'
  }
}

const removeRequest = async id => {
  await deleteRequest(id)
  message.value = '需求已删除'
  await load()
}

onMounted(load)
</script>
