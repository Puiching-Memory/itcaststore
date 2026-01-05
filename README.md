# ItCast Store - 网上书城系统

**课程名称**: 网络编程技术  
**项目名称**: ItCast Store 网上书城系统  
**开发时间**: 2025年  
**技术架构**: 前后端分离架构

---

## 一、项目概述

本项目是一个基于现代化技术栈开发的在线图书商城系统，采用前后端分离架构，实现了用户注册登录、商品浏览、购物车管理、订单处理等完整的电商功能。

### 技术栈

**后端**：
- Java 25 + Spring Boot 4.0.1
- Spring Security + JWT 认证
- Spring Data JPA + PostgreSQL 17
- Maven

**前端**：
- Vue 3 + TypeScript
- Element Plus + Vite
- Pinia 状态管理
- Axios

**部署**：
- Docker + Docker Compose
- 支持代码热重载
- 集成 Langflow 智能体服务

---

## 二、项目功能

### 用户端功能
- ✅ 用户注册登录（JWT 认证）
- ✅ 商品浏览、搜索、分页
- ✅ 商品详情展示
- ✅ 购物车管理（后端持久化）
- ✅ 订单创建、查询和支付
- ✅ 个人中心（信息修改）
- ✅ 公告展示
- ✅ 热卖商品推荐

### 管理端功能
- ✅ 商品管理（CRUD）
- ✅ 订单管理（查询、删除）
- ✅ 公告管理（CRUD）
- ✅ 用户管理（角色切换、状态管理）
- ✅ 权限控制（RBAC）

---

## 三、快速开始

### Docker 部署（推荐）

```bash
# 1. 进入 Docker 配置目录
cd docker

# 2. 构建并启动所有服务
docker-compose up --build -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f
```

**访问地址**：
- 前端：http://localhost:5173（Vite 开发服务器，支持热重载）
- 后端 API：http://localhost:8080/api
- 数据库：localhost:5433
- Langflow：http://localhost:7860

**默认账号**：
- 管理员：`admin` / `123456`
- 普通用户：`madan` / `123456`

### 热重载说明

- **后端**：修改 `backend/src/` 下的 Java 文件，Spring Boot DevTools 自动重启（5-10秒）
- **前端**：修改 `frontend/src/` 下的 Vue/TypeScript 文件，Vite 自动刷新（几乎即时）

### 本地开发

**环境要求**：JDK 21, Node.js 20+, PostgreSQL 17, Maven 3.9+

```bash
# 启动数据库
docker run -d --name postgres \
  -e POSTGRES_DB=itcaststore \
  -e POSTGRES_USER=itcaststore \
  -e POSTGRES_PASSWORD=itcaststore \
  -p 5432:5432 postgres:17-alpine

# 启动后端
cd backend
mvn spring-boot:run

# 启动前端
cd frontend
npm install
npm run dev
```

---

## 四、项目结构

```
itcaststore/
├── backend/          # Spring Boot 后端
│   ├── src/         # 源代码
│   └── Dockerfile   # Docker 配置
├── frontend/         # Vue 3 前端
│   ├── src/         # 源代码
│   └── Dockerfile   # Docker 配置
└── docker/          # Docker Compose 配置
    └── docker-compose.yml
```

---

## 五、API 接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 用户接口（需认证）
- `GET /api/users/me` - 获取当前用户信息
- `PUT /api/users/me` - 更新个人信息

### 商品接口
- `GET /api/products` - 获取商品列表（支持分页、分类、搜索）
- `GET /api/products/{id}` - 获取商品详情
- `GET /api/products/hot` - 获取热卖商品
- `POST /api/products` - 添加商品（管理员）
- `PUT /api/products/{id}` - 更新商品（管理员）
- `DELETE /api/products/{id}` - 删除商品（管理员）

### 购物车接口（需认证）
- `GET /api/cart` - 获取购物车列表
- `POST /api/cart` - 添加商品到购物车
- `PUT /api/cart/{productId}` - 更新购物车商品数量
- `DELETE /api/cart/{productId}` - 从购物车删除商品
- `DELETE /api/cart` - 清空购物车

### 订单接口（需认证）
- `GET /api/orders` - 获取用户订单列表
- `POST /api/orders` - 创建订单
- `POST /api/orders/{id}/pay` - 支付订单
- `GET /api/orders/admin/all` - 获取所有订单（管理员）
- `DELETE /api/orders/{id}` - 删除订单（用户/管理员）

### 公告接口
- `GET /api/notices` - 获取公告列表
- `GET /api/notices/{id}` - 获取公告详情
- `POST /api/notices` - 发布公告（管理员）
- `PUT /api/notices/{id}` - 更新公告（管理员）
- `DELETE /api/notices/{id}` - 删除公告（管理员）

### 管理员接口（需管理员权限）
- `GET /api/users/admin` - 获取用户列表
- `PUT /api/users/admin/{id}/role` - 修改用户角色
- `PUT /api/users/admin/{id}/state` - 修改用户状态

**统一响应格式**：`{ code, message, data }`

---

## 六、数据库设计

### 主要数据表
- **users** - 用户表（用户名、密码、邮箱、角色等）
- **products** - 商品表（名称、价格、分类、库存、图片等）
- **orders** - 订单表（金额、收货信息、支付状态等）
- **orderitem** - 订单项表（订单ID、商品ID、购买数量）
- **notice** - 公告表（标题、内容、发布时间）

---

## 七、Git LFS 配置

本项目使用 Git LFS 管理图片素材和其他大文件。

```bash
# 初始化 Git LFS
git lfs install

# 如果图片已提交，需要迁移
git lfs migrate import --include="*.png,*.jpg,*.jpeg,*.gif,*.svg" --everything
```

**已配置的文件类型**：图片（PNG、JPG、GIF、SVG等）、视频、音频、字体、PDF

**注意事项**：
- 首次使用需要运行 `git lfs install`
- 如果图片已提交到 Git，需要使用 `git lfs migrate` 命令迁移
- 团队成员都需要安装 Git LFS

---

## 八、开发规范

- **代码风格**：遵循 Java 和 TypeScript 编码规范
- **Git 提交**：使用有意义的提交信息
- **API 设计**：遵循 RESTful 规范
- **注释**：关键业务逻辑添加注释

---

## 九、项目亮点

1. **现代化技术栈**：Java 25、Spring Boot 4.0.1、Vue 3、TypeScript
2. **安全机制**：JWT 认证、BCrypt 加密、Spring Security、RBAC
3. **开发体验**：前后端分离、Docker 容器化、代码热重载
4. **智能体集成**：集成 Langflow 智能体服务，支持 AI 辅助功能
5. **架构优势**：环境一致性、快速部署、代码规范

---

## 十、参考资料

- Spring Boot: https://spring.io/projects/spring-boot
- Vue 3: https://vuejs.org/
- PostgreSQL: https://www.postgresql.org/docs/
- Docker: https://docs.docker.com/