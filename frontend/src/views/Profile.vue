<template>
  <div class="panel" v-if="data">
    <div class="between">
      <div>
        <h2>{{ data.user.nickname || data.user.username }} 的主页</h2>
        <p class="muted">{{ data.user.email }} · {{ data.user.phone }}</p>
      </div>
    </div>

    <div class="grid profile-nav">
      <section class="card profile-tab" :class="{ active: activeTab === 'skills' }" @click="setTab('skills')">
        <h3>我的技能</h3>
        <p>{{ skills.length }} 个已发布技能</p>
      </section>
      <section class="card profile-tab" :class="{ active: activeTab === 'requests' }" @click="setTab('requests')">
        <h3>我的需求</h3>
        <p>{{ requests.length }} 个已发布需求</p>
      </section>
      <section class="card profile-tab" :class="{ active: activeTab === 'orders' }" @click="setTab('orders')">
        <h3>我的预约</h3>
        <p>{{ orders.length }} 个相关预约</p>
      </section>
      <section class="card profile-tab" :class="{ active: activeTab === 'comments' }" @click="setTab('comments')">
        <h3>我的评价</h3>
        <p>{{ comments.length }} 条相关评价</p>
      </section>
    </div>
  </div>

  <div class="panel profile-content" v-if="data">
    <section v-if="activeTab === 'skills'">
      <div class="between">
        <h2>我发布的技能</h2>
        <RouterLink class="login" to="/publish-skill">发布技能</RouterLink>
      </div>
      <div class="grid">
        <SkillCard v-for="item in skills" :key="item.skillId" :skill="item" @deleted="removeSkill" />
      </div>
      <p v-if="!skills.length" class="muted">还没有发布技能</p>
    </section>

    <section v-if="activeTab === 'requests'">
      <div class="between">
        <h2>我发布的需求</h2>
        <RouterLink class="login" to="/publish-request">发布需求</RouterLink>
      </div>
      <div class="grid">
        <article class="card" v-for="item in requests" :key="item.requestId">
          <div class="between">
            <span class="tag">{{ item.category || '校园互助' }}</span>
            <span class="muted">{{ statusText(item.status) }}</span>
          </div>
          <h3>需求：{{ item.title }}</h3>
          <p class="muted">详细描述：{{ item.description }}</p>
          <p>预算：{{ item.budget || '面议' }}</p>
          <div class="row">
            <RouterLink class="login" :to="`/requests/${item.requestId}/edit`">修改</RouterLink>
            <button class="danger" @click="removeRequest(item.requestId)">删除</button>
          </div>
        </article>
      </div>
      <p v-if="!requests.length" class="muted">还没有发布需求</p>
    </section>

    <section v-if="activeTab === 'orders'">
      <h2>我的预约</h2>
      <div class="grid">
        <article class="card" v-for="item in orders" :key="item.orderId">
          <span class="tag">{{ statusText(item.status) }}</span>
          <h3>预约编号：{{ item.orderId }}</h3>
          <p>技能编号：{{ item.skillId }}</p>
          <p>我的身份：{{ isBuyer(item) ? '预约方' : '服务方' }}</p>
          <p>预约人：{{ nameOf(item.buyerId) }} · 服务人：{{ nameOf(item.sellerId) }}</p>
          <p class="muted">{{ formatTime(item.createTime) }}</p>
          <div class="row">
            <button v-if="canAccept(item)" @click="acceptAppointment(item.orderId)">接收预约</button>
            <button v-if="canConfirm(item)" @click="confirmAppointment(item.orderId)">确认预约</button>
            <button v-if="canFinish(item)" @click="finishAppointment(item.orderId)">确认完成服务</button>
          </div>
          <div v-if="canComment(item)" class="form compact-form">
            <label class="field">
              <span>评分</span>
              <input v-model.number="commentDraft(item.orderId).score" type="number" min="1" max="5" @input="limitScore(item.orderId)" />
            </label>
            <label class="field">
              <span>评价</span>
              <textarea v-model="commentDraft(item.orderId).content" placeholder="请填写服务评价"></textarea>
            </label>
            <button @click="submitComment(item)">提交评价</button>
          </div>
        </article>
      </div>
      <p v-if="!orders.length" class="muted">暂无预约记录</p>
    </section>

    <section v-if="activeTab === 'comments'">
      <h2>我的评价</h2>
      <div class="grid">
        <article class="card" v-for="item in comments" :key="item.commentId">
          <span class="tag">评分：{{ item.score }}</span>
          <h3>订单编号：{{ item.orderId }}</h3>
          <p>{{ item.content || '未填写评价内容' }}</p>
          <p class="muted">{{ formatTime(item.time) }}</p>
        </article>
      </div>
      <p v-if="!comments.length" class="muted">暂无评价记录</p>
    </section>
  </div>

  <div class="panel" v-if="!data">
    <h2>请先登录</h2>
    <RouterLink class="login" to="/login">去登录</RouterLink>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  acceptOrder,
  confirmOrder,
  createComment,
  deleteRequest,
  deleteSkill,
  finishOrder,
  profile,
  userComments,
  userOrders,
  userRequests,
  userSkills
} from '../api'
import SkillCard from '../components/SkillCard.vue'
import { nicknameById } from '../utils/userNames'

const route = useRoute()
const router = useRouter()
const data = ref(null)
const activeTab = ref(route.query.tab || 'skills')
const skills = ref([])
const requests = ref([])
const orders = ref([])
const comments = ref([])
const nameMap = ref({})
const commentForms = ref({})

const setTab = tab => {
  activeTab.value = tab
  router.replace({ path: '/profile', query: { tab } })
}

const statusText = status => {
  const map = {
    OPEN: '待接收',
    ACCEPTED: '已接收',
    PENDING: '待接收',
    CONFIRMED: '已确认预约',
    FINISHED: '待评价',
    DONE: '已评价',
    CANCELLED: '已取消'
  }
  return map[status] || status || '未知'
}

const formatTime = value => {
  if (!value) return ''
  return String(value).replace('T', ' ').slice(0, 19)
}

const nameOf = id => nameMap.value[id] || '加载中'
const currentUserId = () => data.value?.user?.id
const isBuyer = item => Number(item.buyerId) === Number(currentUserId())
const isSeller = item => Number(item.sellerId) === Number(currentUserId())
const canAccept = item => isSeller(item) && item.status === 'PENDING'
const canConfirm = item => isBuyer(item) && item.status === 'ACCEPTED'
const canFinish = item => isSeller(item) && item.status === 'CONFIRMED'
const canComment = item => isBuyer(item) && item.status === 'FINISHED'

const commentDraft = orderId => {
  if (!commentForms.value[orderId]) {
    commentForms.value[orderId] = { score: 5, content: '' }
  }
  return commentForms.value[orderId]
}

const limitScore = orderId => {
  const draft = commentDraft(orderId)
  if (draft.score > 5) draft.score = 5
  if (draft.score < 1) draft.score = 1
}

const loadNames = async orderList => {
  const ids = [...new Set(orderList.flatMap(item => [item.buyerId, item.sellerId]).filter(Boolean))]
  const pairs = await Promise.all(ids.map(async id => [id, await nicknameById(id)]))
  nameMap.value = Object.fromEntries(pairs)
}

const reloadOrders = async () => {
  orders.value = await userOrders(currentUserId())
  await loadNames(orders.value)
}

const removeSkill = async id => {
  await deleteSkill(id)
  skills.value = skills.value.filter(item => item.skillId !== id)
}

const removeRequest = async id => {
  await deleteRequest(id)
  requests.value = requests.value.filter(item => item.requestId !== id)
}

const acceptAppointment = async id => {
  await acceptOrder(id, currentUserId())
  await reloadOrders()
}

const confirmAppointment = async id => {
  await confirmOrder(id, currentUserId())
  await reloadOrders()
}

const finishAppointment = async id => {
  await finishOrder(id, currentUserId())
  await reloadOrders()
}

const submitComment = async item => {
  const draft = commentDraft(item.orderId)
  limitScore(item.orderId)
  await createComment({
    orderId: item.orderId,
    fromUser: currentUserId(),
    score: draft.score,
    content: draft.content
  })
  await reloadOrders()
  comments.value = await userComments(currentUserId())
}

onMounted(async () => {
  try {
    data.value = await profile()
    const userId = data.value.user.id
    const [skillList, requestList, orderList, commentList] = await Promise.all([
      userSkills(userId),
      userRequests(userId),
      userOrders(userId),
      userComments(userId)
    ])
    skills.value = skillList
    requests.value = requestList
    orders.value = orderList
    comments.value = commentList
    await loadNames(orderList)
  } catch (error) {
    data.value = null
  }
})
</script>
