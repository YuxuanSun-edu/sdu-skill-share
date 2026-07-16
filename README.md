# 山威技遇：校园技能互助与服务共享平台

山威技遇是一个面向高校学生的前后端分离微服务项目，重点展示技能共享、服务预约、需求大厅、消息通知和评价反馈，不包含二手交易与支付流程。

## 技术栈

- 前端：Vue3、Vue Router、Axios、Node.js、Vite
- 后端：Spring Boot 3、Spring MVC、Spring Cloud、Spring Cloud Gateway、OpenFeign
- 数据层：MyBatis Plus、MySQL
- 中间件：Nacos、Redis、RabbitMQ
- JDK：17

## 项目结构

```text
shanwei-skill-share
├── common              公共返回体、JWT、异常处理
├── gateway             统一网关，端口 8080
├── user-service        用户注册、登录、JWT、Redis 登录态，端口 8081
├── skill-service       技能发布、搜索、分页、Redis 缓存，端口 8082
├── order-service       需求、预约、评价、OpenFeign、RabbitMQ，端口 8083
├── message-service     RabbitMQ 消费与消息中心，端口 8084
├── frontend            Vue3 前端，端口 5173
├── sql                 数据库建表与演示数据
└── scripts             本地启动脚本
```

## 本地启动

1. 启动基础服务：

```powershell
docker compose up -d
```

2. 编译后端：

```powershell
mvn clean package -DskipTests
```

3. 启动后端微服务：

```powershell
.\scripts\start-backend.ps1
```


也可以分别启动：

```powershell
mvn -pl gateway spring-boot:run
mvn -pl user-service spring-boot:run
mvn -pl skill-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl message-service spring-boot:run
```

4. 启动前端：

```powershell
.\scripts\start-frontend.ps1
```

前端地址：http://localhost:5173

网关地址：http://localhost:8080

Nacos 控制台：http://localhost:8848/nacos

RabbitMQ 控制台：http://localhost:15672，默认账号密码均为 `guest`。

## 演示账号

- 用户 A：`studentA` / `123456`
- 用户 B：`studentB` / `123456`

## 核心接口

- 用户登录：`POST /api/user/login`
- 用户主页：`GET /api/user/profile`
- 发布技能：`POST /api/skill`
- 技能详情：`GET /api/skill/{id}`
- 技能搜索：`GET /api/skill/search?keyword=Python`
- 发布需求：`POST /api/order/requests`
- 需求大厅：`GET /api/order/requests`
- 提交预约：`POST /api/order/orders`
- 提交评价：`POST /api/order/comments`
- 消息中心：`GET /api/message?userId=1`


- Gateway：统一入口为 `/api/user/**`、`/api/skill/**`、`/api/order/**`、`/api/message/**`。
- Nacos：五个服务都会注册到 `localhost:8848`。
- Redis：`skill-service` 对技能详情 `skill:detail:{id}` 和热门技能 `skill:hot` 做缓存，`user-service` 保存 `login:{userId}` 登录态。
- RabbitMQ：`order-service` 提交预约和评价后发送消息，`message-service` 监听队列并写入 `message` 表。
- OpenFeign：`order-service` 通过 `SkillClient` 调用 `skill-service` 查询技能信息。
- MyBatis Plus：各服务通过 `BaseMapper` 完成 CRUD。



```powershell
git init
git add .
git commit -m "init shanwei skill share platform"
```
