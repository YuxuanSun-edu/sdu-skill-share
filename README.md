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

如果使用 IDEA，直接选择运行配置 `All Backend Services`，点击绿色运行按钮即可一次启动五个后端服务。

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

## 必须展示的技术点

- Gateway：统一入口为 `/api/user/**`、`/api/skill/**`、`/api/order/**`、`/api/message/**`。
- Nacos：五个服务都会注册到 `localhost:8848`。
- Redis：`skill-service` 对技能详情 `skill:detail:{id}` 和热门技能 `skill:hot` 做缓存，`user-service` 保存 `login:{userId}` 登录态。
- RabbitMQ：`order-service` 提交预约和评价后发送消息，`message-service` 监听队列并写入 `message` 表。
- OpenFeign：`order-service` 通过 `SkillClient` 调用 `skill-service` 查询技能信息。
- MyBatis Plus：各服务通过 `BaseMapper` 完成 CRUD。

## 答辩演示流程

1. 启动 Docker 基础服务，打开 Nacos 控制台确认服务注册。
2. 启动所有 Spring Cloud 服务和 Vue3 前端。
3. 使用用户 A 登录，发布技能“Python数据分析”。
4. 使用用户 B 搜索 `Python`，进入技能详情页。
5. 用户 B 提交预约，说明 RabbitMQ 异步通知流程。
6. 打开 RabbitMQ 控制台或消息中心，查看用户 A 收到“新的技能预约”。
7. 调用评价接口或在接口工具中提交评价，查看“新的评价”消息。
8. 连续访问同一技能详情，说明第一次查 MySQL 并写入 Redis，第二次直接命中 Redis。
9. 展示 Gateway 路由配置、OpenFeign 调用代码、MyBatis Plus Mapper、Git 提交记录。

如果全程使用 IDEA，请查看：

```text
outputs/IDEA启动与教案对照说明.md
```

## 说明

本项目面向课程答辩演示，密码使用明文仅为降低本地演示复杂度。正式项目应改为 BCrypt 加密、完善权限模型，并补充更完整的后台管理接口。

如果需要展示 Git 提交记录，可在具备 `.git` 写入权限的本机环境执行：

```powershell
git init
git add .
git commit -m "init shanwei skill share platform"
```
