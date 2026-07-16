<template>
  <section class="between">
    <h2>消息中心</h2>
    <button @click="load">刷新</button>
  </section>

  <div class="grid">
    <article class="card" v-for="item in list" :key="item.id">
      <div class="between">
        <span class="tag">{{ item.status === 'READ' ? '已读' : '未读' }}</span>
        <span class="muted">{{ formatTime(item.createTime) }}</span>
      </div>
      <h3>{{ item.title }}</h3>
      <p>{{ item.content }}</p>
      <div class="row">
        <button class="ghost" @click="markRead(item.id)">标记已读</button>
        <button class="danger" @click="remove(item.id)">删除</button>
      </div>
    </article>
  </div>

  <p v-if="!list.length" class="muted">暂无消息</p>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { deleteMessage, messages, readMessage } from '../api'

const list = ref([])
const userId = () => JSON.parse(localStorage.getItem('user') || '{"id":1}').id

const load = async () => {
  list.value = await messages(userId())
}

const markRead = async id => {
  await readMessage(id)
  await load()
}

const remove = async id => {
  await deleteMessage(id)
  await load()
}

const formatTime = value => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 19)
}

onMounted(load)
</script>
