# 套餐管理 — 管理端 API 接口文档

## 1. 基础说明

### 1.1 请求前缀

```
/api/admin
```

所有接口均需在请求头中携带 JWT Token：

```
Authorization: Bearer <token>
```

### 1.2 统一返回格式

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | `int` | 0 成功，1 失败 |
| `msg` | `string` | 提示消息 |
| `data` | `T` | 响应数据 |

**成功示例**：

```json
{ "code": 0, "msg": "success", "data": null }
```

**失败示例**：

```json
{ "code": 1, "msg": "添加菜品失败", "data": null }
```

### 1.3 分页返回格式

| 字段 | 类型 | 说明 |
|------|------|------|
| `total` | `long` | 总记录数 |
| `records` | `array` | 当前页数据 |
| `pageSize` | `long` | 每页大小 |
| `pageNum` | `long` | 当前页码 |

---

## 2. 菜品管理（Dish）

### 2.1 新增菜品

`POST /admin/dish`

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 菜品名称（唯一） |
| `categoryId` | `long` | 是 | 菜品分类 ID |
| `price` | `decimal` | 是 | 价格 |
| `image` | `string` | 是 | 图片 URL |
| `description` | `string` | 否 | 描述 |
| `status` | `int` | 否 | 0:停售 1:起售（默认 1） |
| `flavors` | `array` | 否 | 口味列表，见下方 |

**口味（flavors）元素结构**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 口味名称，如"辣度" |
| `value` | `string` | 是 | 口味数据，如"微辣,中辣,特辣" |

**请求示例**：

```json
{
  "name": "鱼香肉丝",
  "categoryId": 1,
  "price": 28.00,
  "image": "https://...",
  "description": "经典川菜",
  "status": 1,
  "flavors": [
    { "name": "辣度", "value": "微辣,中辣,特辣" },
    { "name": "规格", "value": "大份,小份" }
  ]
}
```

**响应**：`Result<?>`

---

### 2.2 分页查询菜品

`GET /admin/dish/page`

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | 1 | 页码 |
| `pageSize` | `int` | 否 | 10 | 每页数量 |
| `name` | `string` | 否 | — | 菜品名称（模糊） |
| `categoryId` | `int` | 否 | — | 分类 ID |
| `status` | `int` | 否 | — | 0:禁用 1:启用 |

**响应**：`Result<PageResult<DishVO>>`

**DishVO 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `long` | 菜品 ID |
| `name` | `string` | 菜品名称 |
| `categoryId` | `long` | 分类 ID |
| `price` | `decimal` | 价格 |
| `image` | `string` | 图片 URL |
| `description` | `string` | 描述 |
| `status` | `int` | 0:停售 1:起售 |
| `updateTime` | `string` | 更新时间（yyyy-MM-dd HH:mm:ss） |
| `categoryName` | `string` | 分类名称 |
| `flavors` | `array` | 口味列表 |

**响应示例**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 50,
    "records": [
      {
        "id": 1,
        "name": "鱼香肉丝",
        "categoryId": 1,
        "categoryName": "川菜",
        "price": 28.00,
        "image": "https://...",
        "description": "经典川菜",
        "status": 1,
        "updateTime": "2025-01-01 12:00:00",
        "flavors": [
          { "id": null, "dishId": null, "name": "辣度", "value": "微辣,中辣,特辣" }
        ]
      }
    ],
    "pageSize": 10,
    "pageNum": 1
  }
}
```

---

### 2.3 删除菜品

`DELETE /admin/dish`

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `ids` | `array<long>` | 是 | 需要删除的菜品 ID 列表 |

**请求示例**：`/admin/dish?ids=1,2,3`

**响应**：`Result<?>`

**业务规则**：
- 起售中的菜品不可删除
- 关联了套餐的菜品不可删除

---

### 2.4 根据 ID 查询菜品

`GET /admin/dish/{id}`

**路径参数**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `long` | 菜品 ID |

**响应**：`Result<DishVO>`

---

### 2.5 修改菜品

`PUT /admin/dish`

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `long` | 是 | 菜品 ID |
| `name` | `string` | 是 | 菜品名称 |
| `categoryId` | `long` | 是 | 分类 ID |
| `price` | `decimal` | 是 | 价格 |
| `image` | `string` | 是 | 图片 URL |
| `description` | `string` | 否 | 描述 |
| `status` | `int` | 否 | 0:停售 1:起售 |
| `flavors` | `array` | 否 | 口味列表 |

**响应**：`Result<?>`

---

### 2.6 菜品起售 / 停售

`POST /admin/dish/status/{status}`

**路径参数**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `status` | `int` | 是 | 0:停售 1:起售 |

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `long` | 是 | 菜品 ID |

**请求示例**：`POST /admin/dish/status/0?id=1`

**响应**：`Result<?>`

---

### 2.7 根据分类 ID 查询菜品

`GET /admin/dish/list`

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `categoryId` | `long` | 是 | 分类 ID |

**响应**：`Result<List<Dish>>`

**Dish 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `long` | 菜品 ID |
| `name` | `string` | 名称 |
| `categoryId` | `long` | 分类 ID |
| `price` | `decimal` | 价格 |
| `image` | `string` | 图片 |
| `description` | `string` | 描述 |
| `status` | `int` | 0:停售 1:起售 |
| `createTime` | `string` | 创建时间 |
| `updateTime` | `string` | 更新时间 |
| `flavors` | `string` | 口味 JSON |

> 注：此接口返回的是 Dish 实体而非 DishVO，flavors 为 JSON 字符串，不含 categoryName。

---

## 3. 套餐管理（Setmeal）

### 3.1 新增套餐

`POST /admin/setmeal`

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `name` | `string` | 是 | 套餐名称（唯一） |
| `categoryId` | `long` | 是 | 分类 ID |
| `price` | `decimal` | 是 | 价格 |
| `status` | `int` | 是 | 0:停用 1:启用 |
| `image` | `string` | 是 | 图片 URL |
| `description` | `string` | 否 | 描述 |
| `setmealDishes` | `array` | 是 | 关联菜品列表，见下方 |

**setmealDishes 元素结构**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `dishId` | `long` | 是 | 菜品 ID |
| `price` | `decimal` | 是 | 菜品原价（冗余） |
| `copies` | `int` | 是 | 份数（>=1） |
| `name` | `string` | 否 | 菜品名称（冗余） |
| `setmealId` | `long` | 否 | 套餐 ID（新增时可不传） |

**请求示例**：

```json
{
  "name": "超值午餐",
  "categoryId": 2,
  "price": 39.90,
  "status": 1,
  "image": "https://...",
  "description": "超值午餐套餐",
  "setmealDishes": [
    { "dishId": 1, "price": 28.00, "copies": 1, "name": "鱼香肉丝" },
    { "dishId": 2, "price": 5.00, "copies": 2, "name": "米饭" }
  ]
}
```

**响应**：`Result<?>`

**业务规则**：
- 套餐名称不可重复
- 关联的菜品必须存在且为起售状态

---

### 3.2 套餐分页查询

`GET /admin/setmeal/page`

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| `page` | `int` | 否 | 1 | 页码 |
| `pageSize` | `int` | 否 | 10 | 每页记录数 |
| `name` | `string` | 否 | — | 套餐名称（模糊） |
| `categoryId` | `int` | 否 | — | 分类 ID |
| `status` | `int` | 否 | — | 0:禁用 1:启用 |

**响应**：`Result<PageResult<SetmealVO>>`

**SetmealVO 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `long` | 套餐 ID |
| `categoryId` | `long` | 分类 ID |
| `name` | `string` | 套餐名称 |
| `price` | `decimal` | 价格 |
| `status` | `int` | 0:停用 1:启用 |
| `description` | `string` | 描述 |
| `image` | `string` | 图片 URL |
| `updateTime` | `string` | 更新时间（yyyy-MM-dd HH:mm:ss） |
| `categoryName` | `string` | 分类名称 |
| `setmealDishes` | `array` | 关联菜品列表 |

**响应示例**：

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "categoryId": 2,
        "categoryName": "套餐分类",
        "name": "超值午餐",
        "price": 39.90,
        "status": 1,
        "description": "超值午餐套餐",
        "image": "https://...",
        "updateTime": "2025-01-01 12:00:00",
        "setmealDishes": [
          {
            "id": 1,
            "setmealId": 1,
            "dishId": 1,
            "name": "鱼香肉丝",
            "price": 28.00,
            "copies": 1
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

### 3.3 根据 ID 查询套餐

`GET /admin/setmeal/{id}`

**路径参数**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | `long` | 套餐 ID |

**响应**：`Result<SetmealVO>`

---

### 3.4 修改套餐

`PUT /admin/setmeal`

**请求体**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `long` | 是 | 套餐 ID |
| `categoryId` | `long` | 是 | 分类 ID |
| `name` | `string` | 是 | 套餐名称 |
| `price` | `decimal` | 是 | 价格 |
| `status` | `int` | 是 | 0:停用 1:启用 |
| `image` | `string` | 是 | 图片 URL |
| `description` | `string` | 否 | 描述 |
| `setmealDishes` | `array` | 是 | 关联菜品列表 |

**响应**：`Result<?>`

> 修改套餐时，会删除原有套餐-菜品关联并重新插入。

---

### 3.5 修改套餐状态

`POST /admin/setmeal/status/{status}`

**路径参数**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `status` | `int` | 是 | 0:停用 1:启用 |

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | `long` | 是 | 套餐 ID |

**请求示例**：`POST /admin/setmeal/status/0?id=1`

**响应**：`Result<?>`

---

### 3.6 批量删除套餐

`DELETE /admin/setmeal`

**请求参数（Query）**：

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `ids` | `array<long>` | 是 | 需要删除的套餐 ID 列表 |

**请求示例**：`/admin/setmeal?ids=1,2,3`

**响应**：`Result<?>`

---

## 4. 状态码汇总

| code | 含义 |
|------|------|
| 0 | 成功 |
| 1 | 失败（具体原因见 msg） |

## 5. 错误消息示例

| 场景 | msg |
|------|-----|
| 添加菜品失败 | `"添加菜品失败"` |
| 删除失败（菜品起售中） | `"删除失败"` |
| 菜品名称重复 | 数据库约束 `Duplicate entry` |
| 套餐关联菜品不可用 | `"更新失败"` |
| 参数校验失败 | 全局异常处理器返回 `code=1` 的 Result |

---

## 6. 实体关联图

```
category (1) ──< (N) dish
category (1) ──< (N) setmeal
setmeal  (1) ──< (N) setmeal_dish >── (1) dish
```

- `dish.flavors` 为 JSON 字符串，存储 `DishFlavor[]`
- `setmeal_dish` 为关联表，冗余存储 `name`、`price`、`copies`
