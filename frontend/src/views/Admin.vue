<template>
  <div v-if="!isAdmin" class="panel">
    <h2>管理员入口</h2>
    <p class="muted">请使用管理员账号登录后进入后台。</p>
    <RouterLink class="login" to="/login">去登录</RouterLink>
  </div>

  <template v-else>
    <section class="between">
      <h2>管理员后台</h2>
      <button @click="load">刷新数据</button>
    </section>

    <div class="row admin-tabs">
      <button v-for="item in tabs" :key="item.key" :class="{ secondary: activeTab === item.key, ghost: activeTab !== item.key }" @click="activeTab = item.key">
        {{ item.label }}
      </button>
    </div>

    <section v-if="activeTab === 'users'" class="panel admin-section">
      <h3>用户管理</h3>
      <div class="form admin-form">
        <input v-model="userForm.username" placeholder="账号" />
        <input v-model="userForm.password" placeholder="密码" />
        <input v-model="userForm.nickname" placeholder="昵称" />
        <input v-model="userForm.phone" placeholder="手机号" />
        <input v-model="userForm.email" placeholder="邮箱" />
        <button @click="saveUser">{{ editingUserId ? '保存用户' : '新增用户' }}</button>
        <button class="ghost" @click="resetUserForm">清空</button>
      </div>
      <p class="muted">{{ message }}</p>
      <table class="table">
        <thead><tr><th>账号</th><th>昵称</th><th>邮箱</th><th>手机号</th><th>注册时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in users" :key="item.id">
            <td>{{ item.username }}</td>
            <td>{{ item.nickname }}</td>
            <td>{{ item.email }}</td>
            <td>{{ item.phone }}</td>
            <td>{{ formatTime(item.createTime) }}</td>
            <td class="row">
              <button class="ghost" @click="editUser(item)">编辑</button>
              <button class="danger" @click="removeUser(item.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="activeTab === 'skills'" class="panel admin-section">
      <div class="between">
        <h3>技能管理</h3>
        <RouterLink class="login" to="/publish-skill">新增技能</RouterLink>
      </div>
      <table class="table">
        <thead><tr><th>技能</th><th>分类</th><th>发布者</th><th>价格</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in skills" :key="item.skillId">
            <td>{{ item.skillName }}</td>
            <td>{{ item.category }}</td>
            <td>{{ nameOf(item.userId) }}</td>
            <td>{{ item.price || '面议' }}</td>
            <td>{{ item.status }}</td>
            <td class="row">
              <RouterLink class="login" :to="`/skills/${item.skillId}/edit`">编辑</RouterLink>
              <button class="danger" @click="removeSkill(item.skillId)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="activeTab === 'requests'" class="panel admin-section">
      <div class="between">
        <h3>需求管理</h3>
        <RouterLink class="login" to="/publish-request">新增需求</RouterLink>
      </div>
      <table class="table">
        <thead><tr><th>需求</th><th>分类</th><th>发布者</th><th>预算</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in requests" :key="item.requestId">
            <td>{{ item.title }}</td>
            <td>{{ item.category }}</td>
            <td>{{ nameOf(item.userId) }}</td>
            <td>{{ item.budget || '面议' }}</td>
            <td>{{ statusText(item.status) }}</td>
            <td class="row">
              <RouterLink class="login" :to="`/requests/${item.requestId}/edit`">编辑</RouterLink>
              <button class="danger" @click="removeRequest(item.requestId)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="activeTab === 'orders'" class="panel admin-section">
      <h3>预约管理</h3>
      <table class="table">
        <thead><tr><th>编号</th><th>技能</th><th>预约方</th><th>服务方</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in orders" :key="item.orderId">
            <td>{{ item.orderId }}</td>
            <td>{{ item.skillId }}</td>
            <td>{{ nameOf(item.buyerId) }}</td>
            <td>{{ nameOf(item.sellerId) }}</td>
            <td>
              <select v-model="item.status">
                <option value="PENDING">待接收</option>
                <option value="ACCEPTED">已接收</option>
                <option value="CONFIRMED">已确认预约</option>
                <option value="FINISHED">待评价</option>
                <option value="DONE">已评价</option>
                <option value="CANCELLED">已取消</option>
              </select>
            </td>
            <td><button class="ghost" @click="saveOrderStatus(item)">保存状态</button></td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="activeTab === 'comments'" class="panel admin-section">
      <h3>评价管理</h3>
      <table class="table">
        <thead><tr><th>订单</th><th>评价人</th><th>被评价人</th><th>评分</th><th>内容</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in comments" :key="item.commentId">
            <td>{{ item.orderId }}</td>
            <td>{{ nameOf(item.fromUser) }}</td>
            <td>{{ nameOf(item.toUser) }}</td>
            <td>{{ item.score }}</td>
            <td>{{ item.content }}</td>
            <td><button class="danger" @click="removeComment(item.commentId)">删除</button></td>
          </tr>
        </tbody>
      </table>
    </section>

    <section v-if="activeTab === 'messages'" class="panel admin-section">
      <h3>消息管理</h3>
      <table class="table">
        <thead><tr><th>接收人</th><th>标题</th><th>内容</th><th>状态</th><th>时间</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="item in messagesList" :key="item.id">
            <td>{{ nameOf(item.toUser) }}</td>
            <td>{{ item.title }}</td>
            <td>{{ item.content }}</td>
            <td>{{ item.status === 'READ' ? '已读' : '未读' }}</td>
            <td>{{ formatTime(item.createTime) }}</td>
            <td><button class="danger" @click="removeMessage(item.id)">删除</button></td>
          </tr>
        </tbody>
      </table>
    </section>
  </template>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  adminComments,
  adminCreateUser,
  adminDeleteUser,
  adminMessages,
  adminOrders,
  adminRequests,
  adminUpdateUser,
  adminUsers,
  deleteComment,
  deleteMessage,
  deleteRequest,
  deleteSkill,
  pageSkills,
  updateOrderStatus
} from '../api'
import { nicknameById } from '../utils/userNames'

const currentUser = () => JSON.parse(localStorage.getItem('user') || '{}')
const isAdmin = computed(() => currentUser().username === 'admin')
const tabs = [
  { key: 'users', label: '用户' },
  { key: 'skills', label: '技能' },
  { key: 'requests', label: '需求' },
  { key: 'orders', label: '预约' },
  { key: 'comments', label: '评价' },
  { key: 'messages', label: '消息' }
]
const activeTab = ref('users')
const users = ref([])
const skills = ref([])
const requests = ref([])
const orders = ref([])
const comments = ref([])
const messagesList = ref([])
const names = ref({})
const message = ref('')
const editingUserId = ref(null)
const userForm = reactive({ username: '', password: '', nickname: '', phone: '', email: '' })

const formatTime = value => value ? String(value).replace('T', ' ').slice(0, 19) : ''
const nameOf = id => names.value[id] || '加载中'
const statusText = status => ({
  OPEN: '待接收',
  ACCEPTED: '已接收',
  PENDING: '待接收',
  CONFIRMED: '已确认预约',
  FINISHED: '待评价',
  DONE: '已评价',
  CANCELLED: '已取消'
}[status] || status || '未知')

const collectUserIds = () => {
  const ids = []
  skills.value.forEach(item => ids.push(item.userId))
  requests.value.forEach(item => ids.push(item.userId))
  orders.value.forEach(item => ids.push(item.buyerId, item.sellerId))
  comments.value.forEach(item => ids.push(item.fromUser, item.toUser))
  messagesList.value.forEach(item => ids.push(item.toUser))
  return [...new Set(ids.filter(Boolean))]
}

const loadNames = async () => {
  const pairs = await Promise.all(collectUserIds().map(async id => [id, await nicknameById(id)]))
  names.value = Object.fromEntries(pairs)
}

const load = async () => {
  if (!isAdmin.value) return
  const page = await pageSkills({ current: 1, size: 100 })
  const [userList, requestList, orderList, commentList, messageList] = await Promise.all([
    adminUsers(),
    adminRequests(),
    adminOrders(),
    adminComments(),
    adminMessages()
  ])
  users.value = userList
  skills.value = page.records || []
  requests.value = requestList
  orders.value = orderList
  comments.value = commentList
  messagesList.value = messageList
  await loadNames()
}

const resetUserForm = () => {
  editingUserId.value = null
  Object.assign(userForm, { username: '', password: '', nickname: '', phone: '', email: '' })
}

const editUser = item => {
  editingUserId.value = item.id
  Object.assign(userForm, {
    username: item.username,
    password: '',
    nickname: item.nickname,
    phone: item.phone,
    email: item.email
  })
}

const saveUser = async () => {
  try {
    if (editingUserId.value) {
      await adminUpdateUser(editingUserId.value, { ...userForm })
      message.value = '用户已修改'
    } else {
      await adminCreateUser({ ...userForm })
      message.value = '用户已新增'
    }
    resetUserForm()
    await load()
  } catch (error) {
    message.value = error.message || '操作失败'
  }
}

const removeUser = async id => {
  try {
    await adminDeleteUser(id)
    await load()
  } catch (error) {
    message.value = error.message || '删除失败'
  }
}

const removeSkill = async id => {
  await deleteSkill(id)
  await load()
}

const removeRequest = async id => {
  await deleteRequest(id)
  await load()
}

const saveOrderStatus = async item => {
  await updateOrderStatus(item.orderId, item.status)
  await load()
}

const removeComment = async id => {
  await deleteComment(id)
  await load()
}

const removeMessage = async id => {
  await deleteMessage(id)
  await load()
}

onMounted(load)
</script>
