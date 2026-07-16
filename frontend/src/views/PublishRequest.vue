<template>
  <div class="panel">
    <h2>发布需求</h2>
    <div class="form">
      <label class="field">
        <span>类别：</span>
        <select v-model="form.category">
          <option disabled value="">请选择类别</option>
          <option>编程开发</option>
          <option>设计创作</option>
          <option>语言学习</option>
          <option>摄影剪辑</option>
          <option>课程辅导</option>
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

      <button @click="submit">发布</button>
      <p class="muted">{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { publishRequest } from '../api'

const router = useRouter()
const message = ref('')
const customCategory = ref('')
const currentUser = () => JSON.parse(localStorage.getItem('user') || '{"id":1}')

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

  await publishRequest({
    userId: currentUser().id,
    category,
    title: form.title,
    description: form.description,
    budget: form.budget
  })
  message.value = '发布成功'
  router.push('/requests')
}
</script>
