v# 分类管理接口文档

## 基础信息

- **基础路径：** `/admin/category`（管理端）、`/user/category`（用户端）
- **Content-Type：** `application/json`（POST/PUT）
- **统一响应格式：**

```json
{
  "code": 0,       // 0-成功，1-失败
  "msg": "success",
  "data": {}       // 泛型数据
}
```

## 数据库表结构

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| type | int | 类型（1:菜品分类 2:套餐分类） |
| name | varchar(32) | 分类名称（唯一索引） |
| sort | int | 排序值 |
| status | int | 状态（0:禁用 1:启用） |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| create_user | bigint | 创建人ID |
| update_user | bigint | 修改人ID |

---

## 管理端接口

### 1. 分类分页查询

- **URL：** `GET /admin/category/page`
- **描述：** 分页查询分类列表，支持按名称和类型筛选

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 是 | 1 | 页码 |
| pageSize | int | 是 | 10 | 每页记录数 |
| name | string | 否 | - | 分类名称（模糊匹配） |
| type | int | 否 | - | 分类类型（1:菜品 2:套餐） |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 11,
    "records": [
      {
        "id": 1,
        "type": 1,
        "name": "川菜",
        "sort": 1,
        "status": 1,
        "dishCount": 5,
        "setmealCount": 3,
        "createTime": "2024-01-01 12:00:00",
        "updateTime": "2024-01-01 12:00:00",
        "createUser": 1,
        "updateUser": 1
      }
    ],
    "pageSize": 10,
    "pageNum": 1
  }
}
```

> `dishCount` 和 `setmealCount` 为实时统计字段，分别表示该分类下关联的菜品数量和套餐数量，不由数据库表直接存储。

---

### 2. 新增分类

- **URL：** `POST /admin/category`
- **描述：** 新增分类，分类名称唯一

**请求体：**

```json
{
  "type": 1,
  "name": "川菜",
  "sort": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | int | 是 | 分类类型（1:菜品分类 2:套餐分类） |
| name | string | 是 | 分类名称（唯一） |
| sort | int | 是 | 排序值 |

**业务逻辑：**
- 校验分类名称是否已存在，重复则返回 `"分类名称已存在，请重新输入"`
- 新增时 `status` 默认为 `0`（禁用状态）

**成功响应：** `{ "code": 0, "msg": "success" }`

**失败响应：** `{ "code": 1, "msg": "分类名称已存在，请重新输入" }`

---

### 3. 修改分类

- **URL：** `PUT /admin/category`
- **描述：** 修改分类信息

**请求体：**

```json
{
  "id": 1,
  "type": 1,
  "name": "湘菜",
  "sort": 2
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 分类ID |
| type | int | 否 | 分类类型（1:菜品分类 2:套餐分类） |
| name | string | 是 | 分类名称（修改时若名称变更则校验唯一性） |
| sort | int | 否 | 排序值 |

**业务逻辑：**
- 校验分类ID是否存在
- 若名称变更，校验新名称是否唯一

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 4. 删除分类

- **URL：** `DELETE /admin/category?id={id}`
- **描述：** 根据ID删除分类

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 分类ID |

**业务逻辑：**
- 校验分类ID是否存在
- 校验分类下是否关联了菜品或套餐，有关联则禁止删除

**错误响应：**

```json
{
  "code": 1,
  "msg": "删除失败，当前分类下存在套餐或菜品，请检查"
}
```

---

### 5. 启用/禁用分类

- **URL：** `POST /admin/category/status/{status}?id={id}`
- **描述：** 启用或禁用指定分类

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int（路径） | 是 | 状态值（0:禁用 1:启用） |
| id | long（Query） | 是 | 分类ID |

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 6. 根据类型查询分类

- **URL：** `GET /admin/category/list?type={type}`
- **描述：** 根据分类类型查询分类列表（仅返回启用状态的数据）

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | int | 是 | 分类类型（1:菜品分类 2:套餐分类） |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "type": 1,
      "name": "川菜",
      "sort": 1,
      "status": 1
    }
  ]
}
```

---

## 用户端接口

### 1. 用户端查询分类

- **URL：** `GET /user/category/list`
- **描述：** 查询所有启用状态的分类列表

**请求参数：** 无

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "type": 1,
      "name": "川菜",
      "sort": 1,
      "status": 1
    }
  ]
}
```

**业务逻辑：**
- 仅返回 `status = 1`（启用状态）的分类
- 按 `sort` 升序排序
