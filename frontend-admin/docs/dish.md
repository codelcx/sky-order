# 菜品管理接口文档

## 1. 概述

菜品管理模块提供管理后台对菜品的增删改查、口味管理、起售/停售功能。

- **基础路径**: `/api/admin/dish`
- **认证方式**: 管理端 JWT Token（请求头 `admin-token`）
- **统一返回格式**: `Result<T>` {code, msg, data}
- **分页返回格式**: `PageResult<T>` {total, records, pageSize, pageNum}

## 2. 数据库表

### dish（菜品表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键，自增 |
| name | varchar(32) | 菜品名称（唯一索引） |
| category_id | bigint | 分类ID |
| price | decimal(10,2) | 价格 |
| image | varchar(255) | 图片URL |
| description | varchar(255) | 描述 |
| status | int | 0-停售，1-起售（默认1） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| create_user | bigint | 创建人ID |
| update_user | bigint | 修改人ID |
| flavors | varchar(2048) | 口味JSON |

## 3. 接口列表

### 3.1 新增菜品

**说明**: 创建菜品并关联口味数据。

**请求**

```
POST /api/admin/dish
Content-Type: application/json
```

**请求参数（Body）**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 菜品名称 |
| categoryId | Long | 是 | 分类ID |
| price | BigDecimal | 是 | 价格 |
| image | String | 是 | 图片URL |
| description | String | 否 | 描述 |
| status | Integer | 否 | 0-停售，1-起售 |
| flavors | List\<DishFlavor\> | 否 | 口味列表 |

**DishFlavor 字段**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 口味名称 |
| value | String | 是 | 口味数据 JSON 数组（如 `["不辣","微辣","中辣","重辣"]`） |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

**业务规则**
- 菜品名称不可重复
- 分类ID必须存在

---

### 3.2 分页查询菜品列表

**说明**: 支持按名称模糊搜索、按分类ID筛选、按状态筛选。

**请求**

```
GET /api/admin/dish/page
```

**请求参数（Query）**

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页数量 |
| name | String | 否 | — | 菜品名称（模糊匹配） |
| categoryId | Integer | 否 | — | 分类ID |
| status | Integer | 否 | — | 0-停售，1-起售 |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "name": "清炒小油菜",
        "categoryId": 19,
        "categoryName": "时蔬",
        "price": 18.00,
        "image": "https://...image.png",
        "description": "原料：小油菜",
        "status": 1,
        "updateTime": "2022-06-10 09:51:46",
        "flavors": [
          {
            "name": "忌口",
            "value": "[\"不要葱\",\"不要蒜\",\"不要香菜\"]"
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

### 3.3 删除菜品

**说明**: 批量删除菜品，同时删除关联的口味数据。

**请求**

```
DELETE /api/admin/dish?ids=1,2,3
```

**请求参数（Query）**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | List\<Long\> | 是 | 需要删除的菜品ID列表 |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

**业务规则**
- 起售中的菜品不可删除
- 已关联套餐的菜品不可删除

---

### 3.4 根据ID查询菜品

**说明**: 查询单个菜品详情，包含关联口味数据。

**请求**

```
GET /api/admin/dish/{id}
```

**路径参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜品ID |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 54,
    "name": "清炒小油菜",
    "categoryId": 19,
    "categoryName": "时蔬",
    "price": 18.00,
    "image": "https://...image.png",
    "description": "原料：小油菜",
    "status": 1,
    "updateTime": "2022-06-10 09:51:46",
    "flavors": [
      {
        "name": "忌口",
        "value": "[\"不要葱\",\"不要蒜\",\"不要香菜\"]"
      }
    ]
  }
}
```

---

### 3.5 修改菜品

**说明**: 修改菜品基本信息，同时替换口味数据。

**请求**

```
PUT /api/admin/dish
Content-Type: application/json
```

**请求参数（Body）**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜品ID |
| name | String | 是 | 菜品名称 |
| categoryId | Long | 是 | 分类ID |
| price | BigDecimal | 是 | 价格 |
| image | String | 是 | 图片URL |
| description | String | 否 | 描述 |
| status | Integer | 否 | 0-停售，1-起售 |
| flavors | List\<DishFlavor\> | 否 | 口味列表（替换原口味数据） |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

**业务规则**
- 菜品ID必须存在
- 修改后的名称不可与其他菜品重复

---

### 3.6 菜品起售/停售

**说明**: 修改菜品的售卖状态。

**请求**

```
POST /api/admin/dish/status/{status}?id=1
```

**路径参数**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 0-停售，1-起售 |

**请求参数（Query）**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜品ID |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

**业务规则**
- 菜品ID必须存在
- 若关联了套餐则不可停售

---

### 3.7 根据分类ID查询菜品

**说明**: 查询指定分类下的菜品列表（不包含口味数据，包含所有状态）。

**请求**

```
GET /api/admin/dish/list?categoryId=19
```

**请求参数（Query）**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Long | 是 | 分类ID |

**响应**

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 54,
      "name": "清炒小油菜",
      "categoryId": 19,
      "price": 18.00,
      "image": "https://...image.png",
      "description": "原料：小油菜",
      "status": 1,
      "createTime": "2022-06-10 09:51:46",
      "updateTime": "2022-06-10 09:51:46",
      "createUser": 1,
      "updateUser": 1
    }
  ]
}
```

## 4. 公共响应状态

| code | msg | 说明 |
|------|-----|------|
| 0 | success | 操作成功 |
| 1 | 各类错误消息 | 操作失败，具体错误见 msg |

## 5. 业务异常说明

| 触发条件 | 异常说明 |
|----------|----------|
| 新增/修改时菜品名称重复 | "菜品名称重复" |
| 菜品ID不存在 | "菜品ID不存在" |
| 分类ID不存在 | "分类ID不存在" |
| 删除起售中的菜品 | "删除失败，菜品ID为：... 状态为起售中" |
| 删除已关联套餐的菜品 | "删除失败，菜品ID为：... 存在关联套餐" |
| 停售已关联套餐的菜品 | "修改状态失败，菜品ID为：... 存在关联套餐" |
