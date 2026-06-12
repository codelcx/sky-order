# 订单管理端接口文档

## 基础信息

- **基础路径**: `/admin/order`
- **Controller**: `com.sky.controller.admin.OrderController`
- **分组标签**: `订单管理相关接口`

### 统一响应结构

```json
{
  "code": 0,      // Integer: 0=成功, 1=失败
  "msg": "success",
  "data": { ... }
}
```

### 分页响应结构

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 100,        // 总记录数
    "records": [ ... ],  // 数据列表
    "pageSize": 10,      // 每页条数
    "pageNum": 1         // 当前页码
  }
}
```

### 订单状态枚举

| 值 | 状态 | 说明 |
|---|------|------|
| 1 | PENDING_PAYMENT | 待付款 |
| 2 | TO_BE_CONFIRMED | 待接单 |
| 3 | CONFIRMED | 已接单 |
| 4 | DELIVERY_IN_PROGRESS | 派送中 |
| 5 | COMPLETED | 已完成 |
| 6 | CANCELLED | 已取消 |

### 支付方式

| 值 | 说明 |
|---|------|
| 1 | 微信支付 |
| 2 | 支付宝 |

### 支付状态

| 值 | 说明 |
|---|------|
| 0 | 未支付 |
| 1 | 已支付 |
| 2 | 退款 |

---

## 1. 订单搜索

> 条件分页查询订单列表。

- **URL**: `GET /admin/order/conditionSearch`
- **Method**: `GET`

### 请求参数 (Query)

| 参数 | 类型 | 必须 | 说明 |
|------|------|------|------|
| page | int | 是 | 页码，默认 1 |
| pageSize | int | 是 | 每页条数，默认 10 |
| number | String | 否 | 订单号 |
| phone | String | 否 | 手机号码 |
| status | Integer | 否 | 订单状态 (1-7) |
| beginTime | LocalDateTime | 否 | 开始时间 (yyyy-MM-dd HH:mm:ss) |
| endTime | LocalDateTime | 否 | 结束时间 (yyyy-MM-dd HH:mm:ss) |
| userId | Long | 否 | 用户ID |

**DTO**: `OrdersPageQueryDTO`

### 响应数据

`PageResult<OrderVO>`

#### OrderVO 字段

继承 `Orders` 全部字段，额外包含：

| 字段 | 类型 | 说明 |
|------|------|------|
| orderDishes | String | 订单菜品信息，格式: "菜品1*1;菜品2*3;" |
| orderDetailList | List\<OrderDetail\> | 订单详情列表 |

#### Orders 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 订单ID |
| number | String | 订单号 |
| status | Integer | 订单状态 |
| userId | Long | 用户ID |
| addressBookId | Long | 地址ID |
| orderTime | LocalDateTime | 下单时间 |
| checkoutTime | LocalDateTime | 结账时间 |
| payMethod | Integer | 支付方式 (1:微信 2:支付宝) |
| payStatus | Integer | 支付状态 (0:未支付 1:已支付 2:退款) |
| amount | BigDecimal | 实收金额 |
| remark | String | 备注 |
| userName | String | 用户名 |
| phone | String | 手机号 |
| address | String | 地址 |
| consignee | String | 收货人 |
| cancelReason | String | 取消原因 |
| rejectionReason | String | 拒绝原因 |
| cancelTime | LocalDateTime | 取消时间 |
| estimatedDeliveryTime | LocalDateTime | 预计送达时间 |
| deliveryStatus | Integer | 配送状态 (0:选择具体时间 1:立即送出) |
| deliveryTime | LocalDateTime | 送达时间 |
| packAmount | int | 打包费 |
| tablewareNumber | int | 餐具数量 |
| tablewareStatus | Integer | 餐具数量状态 (0:选择具体数量 1:按餐量提供) |

#### OrderDetail 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 明细ID |
| name | String | 名称 |
| orderId | Long | 订单ID |
| dishId | Long | 菜品ID |
| setmealId | Long | 套餐ID |
| dishFlavor | String | 口味 |
| number | Integer | 数量 |
| amount | BigDecimal | 金额 |
| image | String | 图片 |

### 响应示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "number": "202503121234",
        "status": 2,
        "userId": 1,
        "addressBookId": 1,
        "orderTime": "2025-03-12 10:30:00",
        "checkoutTime": "2025-03-12 10:30:05",
        "payMethod": 1,
        "payStatus": 1,
        "amount": 88.50,
        "remark": "少辣",
        "userName": "张三",
        "phone": "13800138000",
        "address": "北京市朝阳区xxx",
        "consignee": "张三",
        "estimatedDeliveryTime": "2025-03-12 11:30:00",
        "deliveryStatus": 1,
        "deliveryTime": null,
        "packAmount": 3,
        "tablewareNumber": 2,
        "tablewareStatus": 0,
        "orderDishes": "鱼香肉丝*1;米饭*2;",
        "orderDetailList": [
          {
            "id": 1,
            "name": "鱼香肉丝",
            "orderId": 1,
            "dishId": 1,
            "setmealId": null,
            "dishFlavor": "微辣",
            "number": 1,
            "amount": 32.00,
            "image": "xxx.jpg"
          }
        ]
      }
    ],
    "pageSize": 10,
    "pageNum": 1
  }
}
```

---

## 2. 查询订单详情

- **URL**: `GET /admin/order/details/{id}`
- **Method**: `GET`
- **参数**: `id` (Path) - 订单ID

### 响应数据

`Result<OrderVO>` (字段同上方 OrderVO)

### 响应示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "number": "202503121234",
    "status": 2,
    "orderDishes": "鱼香肉丝*1;米饭*2;",
    "orderDetailList": [
      {
        "id": 1,
        "name": "鱼香肉丝",
        "orderId": 1,
        "dishId": 1,
        "number": 1,
        "amount": 32.00,
        "image": "xxx.jpg"
      }
    ]
  }
}
```

---

## 3. 接单

> 商家接收订单，状态从 `待接单(2)` 变为 `已接单(3)`。

- **URL**: `PUT /admin/order/confirm`
- **Method**: `PUT`

### 请求参数 (Body)

```json
{
  "id": 1,       // Long, 必须 - 订单ID
  "status": 2    // Integer, 必须 - 订单状态
}
```

**DTO**: `OrdersConfirmDTO`

### 响应

```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 4. 订单状态数量统计

> 查询待接单、已接单、派送中的订单数量。

- **URL**: `GET /admin/order/statistics`
- **Method**: `GET`

### 响应数据

`Result<OrderStatisticsVO>`

| 字段 | 类型 | 说明 |
|------|------|------|
| toBeConfirmed | Integer | 待接单数量 (status=2) |
| confirmed | Integer | 已接单数量 (status=3) |
| deliveryInProgress | Integer | 派送中数量 (status=4) |

### 响应示例

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "toBeConfirmed": 5,
    "confirmed": 12,
    "deliveryInProgress": 3
  }
}
```

---

## 5. 拒单

> 商家拒绝订单，订单变为 `已取消(6)`。

- **URL**: `PUT /admin/order/rejection`
- **Method**: `PUT`

### 请求参数 (Body)

```json
{
  "id": 1,              // Long, 必须 - 订单ID
  "rejectionReason": "食材不足"  // String, 必须 - 拒绝原因
}
```

**DTO**: `OrdersRejectionDTO`

### 响应

```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 6. 取消订单

> 后台取消订单，订单变为 `已取消(6)`。

- **URL**: `PUT /admin/order/cancel`
- **Method**: `PUT`

### 请求参数 (Body)

```json
{
  "id": 1,              // Long, 必须 - 订单ID
  "cancelReason": "用户要求取消"  // String, 必须 - 取消原因
}
```

**DTO**: `OrdersCancelDTO`

### 响应

```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 7. 派送订单

> 商家派送订单，状态从 `已接单(3)` 变为 `派送中(4)`。

- **URL**: `PUT /admin/order/delivery/{id}`
- **Method**: `PUT`
- **参数**: `id` (Path) - 订单ID

### 响应

```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 8. 完成订单

> 订单送达完成，状态从 `派送中(4)` 变为 `已完成(5)`。

- **URL**: `PUT /admin/order/complete/{id}`
- **Method**: `PUT`
- **参数**: `id` (Path) - 订单ID

### 响应

```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 状态机流转图

```
待付款(1) ──支付──▶ 待接单(2) ──接单──▶ 已接单(3) ──派送──▶ 派送中(4) ──完成──▶ 已完成(5)
   │                   │                  │                    │
   ├──用户取消──▶       ├──拒单──▶         ├──取消──▶            ├──取消──▶
   └──取消────▶   已取消(6) ◀──────────────┴────────────────────┘
```

**支持的状态转换：**

| 当前状态 | 事件 | 目标状态 |
|----------|------|----------|
| 待付款(1) | 支付 | 待接单(2) |
| 待付款(1) | 用户取消/管理员取消 | 已取消(6) |
| 待接单(2) | 接单 | 已接单(3) |
| 待接单(2) | 用户取消/管理员取消(拒单) | 已取消(6) |
| 已接单(3) | 派送 | 派送中(4) |
| 已接单(3) | 管理员取消 | 已取消(6) |
| 派送中(4) | 完成 | 已完成(5) |
| 派送中(4) | 管理员取消 | 已取消(6) |
| 已完成(5) | 管理员取消 | 已取消(6) |
