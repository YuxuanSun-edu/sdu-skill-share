<template>
  <div class="panel">
    <h2>编辑需求</h2>
    <div class="form" v-if="loaded">
      <label class="field">
        <span>类别：</span>
        <select v-model="form.category">
          <option disabled value="">请选择类别</option>
          <option v-for="item in categories" :key="item">{{ item }}</option>
          <option>其他</option>
        </select>
      </label>

      <label v-if="form.category === '其他'" class="field">
        <span>具体类别：</span>
        <input v-model="customCategory" placeholder="请输入具体类别" />
      </label>

      <label class="field">
        <span>需求：</span>
        <input v-model="form.title" placeholder="例如：需要制作毕业答辩PPT" />
      </label>

      <label class="field">
        <span>详细描述：</span>
        <textarea v-model="form.description" placeholder="请说明具体要求、完成时间或希望的服务方式"></textarea>
      </label>

      <label class="field">
        <span>预算：</span>
        <input v-model="form.budget" placeholder="例如：50元、免费互助、面议" />
      </label>

      <button @click="submit">保存修改</button>
      <p class="muted">{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { requestDetail, updateRequest } from '../api'

const route = useRoute()
const router = useRouter()
const categories = ['编程开发', '设计创作', '语言学习', '摄影剪辑', '课程辅导']
const loaded = ref(false)
const message = ref('')
const customCategory = ref('')

const form = reactive({
  category: '',
  title: '',
  description: '',
  budget: ''
})

const submit = async () => {
  const category = form.category === '其他' ? customCategory.value.trim() : form.category
  if (!category || !form.title || !form.description || !form.budget) {
    message.value = '请完整填写需求信息'
    return
  }

  await updateRequest(route.params.id, {
    ...form,
    category
  })
  message.value = '修改成功'
  router.push('/profile?tab=requests')
}

onMounted(async () => {
  const request = await requestDetail(route.params.id)
  Object.assign(form, {
    category: categories.includes(request.category) ? request.category : '其他',
    title: request.title || '',
    description: request.description || '',
    budget: request.budget || ''
  })
  customCategory.value = categories.includes(request.category) ? '' : request.category || ''
  loaded.value = true
})
</script>
