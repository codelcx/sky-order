# 苍穹外卖 — 产品需求文档

## 1. 项目概述

苍穹外卖是一个餐饮外卖系统，包含 **管理后台** 和 **微信小程序** 两端，后端基于 Spring Boot 4.x + MyBatis-Plus 提供 RESTful API。

## 2. 技术栈

| 技术 | 版本 / 选型 |
|------|------------|
| Java | 21 |
| 框架 | Spring Boot 4.x |
| ORM | MyBatis-Plus 3.5.16 |
| 数据库 | MySQL |
| 项目构建 | Maven 多模块 |

## 3. 后端目录结构

```
backend/
├── pom.xml                                      # 父 POM（Spring Boot 4.x + MyBatis-Plus）
│
├── docs/
│   ├── sky.sql                                  # 数据库 DDL（11 张表 + 初始数据）
│   └── prd.md                                   # 本文档
│
├── sky-common/                                  # —— 公共模块 ——
│   ├── pom.xml
│   └── src/main/java/com/sky/
│       ├── constant/
│       │   ├── MessageConstant.java             # 消息提示常量
│       │   └── StatusConstant.java              # 启用/禁用状态
│       ├── context/
│       │   └── BaseContext.java                 # ThreadLocal 持有当前用户id
│       ├── enumeration/
│       │   └── OrderStatus.java                 # 订单状态枚举（7 种）
│       ├── exception/
│       │   ├── BaseException.java               # 运行时异常基类
│       │   ├── AccountLockedException.java
│       │   ├── LoginFailedException.java
│       │   ├── PasswordErrorException.java
│       │   └── UserNotExistsException.java
│       ├── properties/
│       │   ├── JwtProperties.java               # JWT 配置属性
│       │   ├── AliOssProperties.java            # 阿里云 OSS 配置属性
│       │   └── WeChatProperties.java            # 微信小程序配置属性
│       ├── result/
│       │   ├── Result.java                      # 统一返回 {code, msg, data}
│       │   └── PageResult.java                  # 分页返回
│       └── utils/
│           ├── JwtUtil.java                     # JWT 令牌生成/校验
│           └── AliOssUtil.java                  # 阿里云 OSS 文件上传
│
├── sky-pojo/                                    # —— 实体/DTO/VO 模块 ——
│   ├── pom.xml
│   └── src/main/java/com/sky/
│       ├── entity/                              # 表实体，与数据库一一对应
│       │   ├── Employee.java                    # 员工
│       │   ├── Category.java                    # 分类
│       │   ├── Dish.java                        # 菜品
│       │   ├── DishFlavor.java                  # 菜品口味
│       │   ├── Setmeal.java                     # 套餐
│       │   ├── SetmealDish.java                 # 套餐-菜品关联
│       │   ├── User.java                        # 微信用户
│       │   ├── AddressBook.java                 # 地址簿
│       │   ├── ShoppingCart.java                # 购物车
│       │   ├── Orders.java                      # 订单
│       │   └── OrderDetail.java                 # 订单明细
│       ├── dto/                                 # 请求传输对象
│       │   ├── EmployeeLoginDTO.java            # 员工登录
│       │   ├── EmployeeDTO.java                 # 员工增改
│       │   ├── CategoryDTO.java                 # 分类增改
│       │   ├── DishDTO.java                     # 菜品增改（含口味 list）
│       │   ├── SetmealDTO.java                  # 套餐增改（含关联菜品 list）
│       │   ├── ShoppingCartDTO.java             # 购物车操作
│       │   ├── OrdersSubmitDTO.java             # 用户下单
│       │   └── AddressBookDTO.java              # 地址操作
│       └── vo/                                  # 视图返回对象
│           ├── DishVO.java                      # 菜品 + 口味列表
│           ├── SetmealVO.java                   # 套餐 + 关联菜品列表
│           └── OrderVO.java                     # 订单 + 明细列表
│
└── sky-server/                                  # —— Web 服务模块 ——
    ├── pom.xml                                  # 依赖 sky-common, web, mybatis-plus, mysql
    └── src/
        ├── main/
        │   ├── java/com/sky/
        │   │   ├── SkyApplication.java          # 启动类
        │   │   │
        │   │   ├── config/                      # 配置
        │   │   │   ├── WebMvcConfig.java        # 拦截器注册、跨域
        │   │   │   ├── MyBatisPlusConfig.java   # 分页插件、自动填充
        │   │   │   ├── JacksonConfig.java       # 全局日期序列化
        │   │   │   └── OssConfig.java           # OSS 客户端 Bean
        │   │   │
        │   │   ├── interceptor/
        │   │   │   └── JwtTokenInterceptor.java # 登录拦截（admin + user）
        │   │   │
        │   │   ├── aspect/
        │   │   │   └── AutoFillAspect.java      # 自动填充 create/update 时间&人
        │   │   │
        │   │   ├── handler/
        │   │   │   └── GlobalExceptionHandler.java # 全局异常处理
        │   │   │
        │   │   ├── controller/
        │   │   │   ├── admin/                   # 管理后台 API
        │   │   │   │   ├── EmployeeController.java   # 员工登录/CRUD/状态
        │   │   │   │   ├── CategoryController.java   # 分类 CRUD/状态
        │   │   │   │   ├── DishController.java       # 菜品 CRUD/口味/起停售
        │   │   │   │   ├── SetmealController.java    # 套餐 CRUD/关联/起停售
        │   │   │   │   ├── OrderController.java      # 订单查看/接单/派送/完成/取消
        │   │   │   │   └── CommonController.java     # 文件上传
        │   │   │   └── user/                    # 小程序用户 API
        │   │   │       ├── UserController.java           # 微信登录
        │   │   │       ├── AddressBookController.java    # 地址 CRUD
        │   │   │       ├── ShoppingCartController.java   # 购物车增删查
        │   │   │       ├── OrderController.java          # 下单/支付/订单列表
        │   │   │       ├── CategoryController.java       # 分类浏览
        │   │   │       └── DishController.java           # 菜品/套餐浏览
        │   │   │
        │   │   ├── service/
        │   │   │   ├── EmployeeService.java
        │   │   │   ├── CategoryService.java
        │   │   │   ├── DishService.java
        │   │   │   ├── SetmealService.java
        │   │   │   ├── ShoppingCartService.java
        │   │   │   ├── AddressBookService.java
        │   │   │   ├── UserService.java
        │   │   │   ├── OrderService.java
        │   │   │   └── impl/
        │   │   │       ├── EmployeeServiceImpl.java
        │   │   │       ├── CategoryServiceImpl.java
        │   │   │       ├── DishServiceImpl.java
        │   │   │       ├── SetmealServiceImpl.java
        │   │   │       ├── ShoppingCartServiceImpl.java
        │   │   │       ├── AddressBookServiceImpl.java
        │   │   │       ├── UserServiceImpl.java
        │   │   │       └── OrderServiceImpl.java
        │   │   │
        │   │   └── mapper/                       # MyBatis-Plus Mapper
        │   │       ├── EmployeeMapper.java
        │   │       ├── CategoryMapper.java
        │   │       ├── DishMapper.java
        │   │       ├── DishFlavorMapper.java
        │   │       ├── SetmealMapper.java
        │   │       ├── SetmealDishMapper.java
        │   │       ├── ShoppingCartMapper.java
        │   │       ├── AddressBookMapper.java
        │   │       ├── UserMapper.java
        │   │       ├── OrdersMapper.java
        │   │       └── OrderDetailMapper.java
        │   │
        │   └── resources/
        │       └── application.yaml              # 端口 8080, MySQL 数据源
        │
        └── test/java/com/sky/
            └── SkyApplicationTests.java          # 测试
```

## 4. 功能模块

### 4.1 管理后台 (admin)

| 功能 | 涉及表 | Controller | Service | 说明 |
|------|--------|-----------|---------|------|
| 员工管理 | `employee` | admin.EmployeeController | EmployeeService | 登录、CRUD、启用/禁用 |
| 分类管理 | `category` | admin.CategoryController | CategoryService | CRUD、启用/禁用（type 区分菜品/套餐） |
| 菜品管理 | `dish`, `dish_flavor` | admin.DishController | DishService | CRUD、口味管理、起售/停售 |
| 套餐管理 | `setmeal`, `setmeal_dish` | admin.SetmealController | SetmealService | CRUD、关联菜品、起售/停售 |
| 订单管理 | `orders`, `order_detail` | admin.OrderController | OrderService | 查看、接单、派送、完成、取消、拒单 |
| 通用接口 | — | admin.CommonController | — | 文件上传 |

### 4.2 小程序用户端 (user)

| 功能 | 涉及表 | Controller | Service | 说明 |
|------|--------|-----------|---------|------|
| 用户管理 | `user` | user.UserController | UserService | 微信登录（openid） |
| 地址簿管理 | `address_book` | user.AddressBookController | AddressBookService | CRUD、设为默认 |
| 购物车管理 | `shopping_cart` | user.ShoppingCartController | ShoppingCartService | 增删改查 |
| 订单管理 | `orders`, `order_detail` | user.OrderController | OrderService | 下单、支付、取消、订单列表 |
| 分类浏览 | `category` | user.CategoryController | CategoryService | 分类列表 |
| 菜品浏览 | `dish`, `setmeal` | user.DishController | DishService | 菜品/套餐列表、根据分类查询 |

## 5. 数据库设计

数据库 `sky_order`，共 11 张表：

```
employee        ── 员工
user            ── 微信用户
category        ── 分类（type=1 菜品分类 / type=2 套餐分类）
dish            ── 菜品（关联 category）
dish_flavor     ── 菜品口味（关联 dish）
setmeal         ── 套餐（关联 category）
setmeal_dish    ── 套餐-菜品关联（关联 setmeal + dish）
shopping_cart   ── 购物车（关联 user + dish/setmeal）
address_book    ── 地址簿（关联 user）
orders          ── 订单（关联 user + address_book）
order_detail    ── 订单明细（关联 orders + dish/setmeal）
```

**订单状态流转**：

```
待付款(1) → 待接单(2) → 已接单(3) → 派送中(4) → 已完成(5)
                  ↓
           已取消(6) / 退款(7)
```