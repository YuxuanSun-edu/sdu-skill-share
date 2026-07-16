import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import SkillHall from '../views/SkillHall.vue'
import SkillDetail from '../views/SkillDetail.vue'
import PublishSkill from '../views/PublishSkill.vue'
import EditSkill from '../views/EditSkill.vue'
import PublishRequest from '../views/PublishRequest.vue'
import EditRequest from '../views/EditRequest.vue'
import RequestHall from '../views/RequestHall.vue'
import Profile from '../views/Profile.vue'
import Messages from '../views/Messages.vue'
import Admin from '../views/Admin.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/skills', component: SkillHall },
    { path: '/skills/:id', component: SkillDetail },
    { path: '/publish-skill', component: PublishSkill },
    { path: '/skills/:id/edit', component: EditSkill },
    { path: '/publish-request', component: PublishRequest },
    { path: '/requests/:id/edit', component: EditRequest },
    { path: '/requests', component: RequestHall },
    { path: '/profile', component: Profile },
    { path: '/messages', component: Messages },
    { path: '/admin', component: Admin }
  ]
})

router.beforeEach((to, from, next) => {
  const publicPaths = ['/', '/login', '/register', '/skills', '/requests']
  const isSkillDetail = /^\/skills\/\d+$/.test(to.path)
  const isLogin = Boolean(localStorage.getItem('token'))
  if (!isLogin && !publicPaths.includes(to.path) && !isSkillDetail) {
    next('/login')
    return
  }
  next()
})

export default router
