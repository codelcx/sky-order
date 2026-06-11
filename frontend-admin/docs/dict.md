# 字典管理接口文档

## 基础信息

- **基础路径：** `/admin/dict/type`（字典类型）、`/admin/dict/data`（字典数据）
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

### dict_type（字典类型）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(100) | 字典名称 |
| code | varchar(100) | 字典编码（唯一索引） |
| description | varchar(500) | 描述 |
| status | tinyint | 状态（0:禁用 1:启用） |
| sort | int | 排序值 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| create_user | bigint | 创建人ID |
| update_user | bigint | 修改人ID |

### dict_data（字典数据）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| dict_type_id | bigint | 字典类型ID（外键，级联删除） |
| label | varchar(100) | 字典标签 |
| value | varchar(100) | 字典键值 |
| is_default | tinyint | 是否默认（0:否 1:是） |
| status | tinyint | 状态（0:禁用 1:启用） |
| sort | int | 排序值 |
| remark | varchar(500) | 备注 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| create_user | bigint | 创建人ID |
| update_user | bigint | 修改人ID |

---

## 字典类型管理

### 1. 字典类型分页查询

- **URL：** `GET /admin/dict/type/page`
- **描述：** 分页查询字典类型列表，支持按名称、编码、状态筛选

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 是 | 1 | 页码 |
| pageSize | int | 是 | 10 | 每页记录数 |
| name | string | 否 | - | 字典名称（模糊匹配） |
| code | string | 否 | - | 字典编码（模糊匹配） |
| status | int | 否 | - | 状态（0:禁用 1:启用） |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 1,
    "records": [
      {
        "id": 1,
        "name": "订单状态",
        "code": "order_status",
        "description": "订单流转状态",
        "status": 1,
        "sort": 1,
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

---

### 2. 新增字典类型

- **URL：** `POST /admin/dict/type`
- **描述：** 新增字典类型，名称和编码均唯一

**请求体：**

```json
{
  "name": "订单状态",
  "code": "order_status",
  "description": "订单流转状态",
  "sort": 1
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | string | 是 | 字典名称（唯一） |
| code | string | 是 | 字典编码（唯一） |
| description | string | 否 | 描述 |
| sort | int | 是 | 排序值 |

**业务逻辑：**
- 校验字典名称和编码是否已存在，重复则返回对应错误
- 新增时 `status` 默认为 `0`（禁用状态）

**成功响应：** `{ "code": 0, "msg": "success" }`

**失败响应：** `{ "code": 1, "msg": "字典名称已存在" }`

---

### 3. 修改字典类型

- **URL：** `PUT /admin/dict/type`
- **描述：** 修改字典类型信息

**请求体：**

```json
{
  "id": 1,
  "name": "订单状态",
  "code": "order_status",
  "description": "订单流转状态（含退款）",
  "sort": 2
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典类型ID |
| name | string | 是 | 字典名称（修改时若名称变更则校验唯一性） |
| code | string | 是 | 字典编码（修改时若编码变更则校验唯一性） |
| description | string | 否 | 描述 |
| sort | int | 否 | 排序值 |

**业务逻辑：**
- 校验字典类型ID是否存在
- 若名称变更，校验新名称是否唯一
- 若编码变更，校验新编码是否唯一

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 4. 删除字典类型

- **URL：** `DELETE /admin/dict/type/{id}`
- **描述：** 根据ID删除字典类型及其关联的所有字典数据（级联删除）

**请求参数（路径）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典类型ID |

**业务逻辑：**
- 校验字典类型ID是否存在
- `dict_data` 表设置了 `ON DELETE CASCADE`，删除类型时自动删除所有关联数据

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 5. 根据ID查询字典类型

- **URL：** `GET /admin/dict/type/{id}`
- **描述：** 查询单个字典类型详情

**请求参数（路径）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典类型ID |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "订单状态",
    "code": "order_status",
    "description": "订单流转状态",
    "status": 1,
    "sort": 1,
    "createTime": "2024-01-01 12:00:00",
    "updateTime": "2024-01-01 12:00:00",
    "createUser": 1,
    "updateUser": 1
  }
}
```

---

### 6. 查询所有启用的字典类型

- **URL：** `GET /admin/dict/type/list`
- **描述：** 查询所有启用状态的字典类型（供前端下拉框使用）

**请求参数：** 无

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "订单状态",
      "code": "order_status",
      "sort": 1,
      "status": 1
    }
  ]
}
```

**业务逻辑：**
- 仅返回 `status = 1`（启用状态）的数据
- 按 `sort` 升序排序

---

### 7. 启用/禁用字典类型

- **URL：** `PUT /admin/dict/type/status/{status}?id={id}`
- **描述：** 启用或禁用指定字典类型

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int（路径） | 是 | 状态值（0:禁用 1:启用） |
| id | long（Query） | 是 | 字典类型ID |

**业务逻辑：**
- 校验字典类型ID是否存在

**成功响应：** `{ "code": 0, "msg": "success" }`

---

## 字典数据管理

### 1. 字典数据分页查询

- **URL：** `GET /admin/dict/data/page`
- **描述：** 分页查询字典数据列表，支持按字典类型编码、标签、状态筛选

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 是 | 1 | 页码 |
| pageSize | int | 是 | 10 | 每页记录数 |
| dictTypeCode | string | 否 | - | 字典类型编码（精确匹配） |
| label | string | 否 | - | 字典标签（模糊匹配） |
| status | int | 否 | - | 状态（0:禁用 1:启用） |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 5,
    "records": [
      {
        "id": 1,
        "dictTypeId": 1,
        "label": "待付款",
        "value": "1",
        "isDefault": 1,
        "status": 1,
        "sort": 1,
        "remark": "订单已提交，等待支付",
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

---

### 2. 新增字典数据

- **URL：** `POST /admin/dict/data`
- **描述：** 新增字典数据条目

**请求体：**

```json
{
  "dictTypeId": 1,
  "label": "待付款",
  "value": "1",
  "isDefault": 1,
  "sort": 1,
  "remark": "订单已提交，等待支付"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dictTypeId | long | 是 | 字典类型ID |
| label | string | 是 | 字典标签 |
| value | string | 是 | 字典键值（同一类型下唯一） |
| isDefault | int | 否 | 是否默认（0:否 1:是） |
| sort | int | 是 | 排序值 |
| remark | string | 否 | 备注 |

**业务逻辑：**
- 校验字典类型ID是否存在
- 校验同一字典类型下 `value` 是否重复
- 新增时 `status` 默认为 `0`（禁用状态）

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 3. 修改字典数据

- **URL：** `PUT /admin/dict/data`
- **描述：** 修改字典数据信息

**请求体：**

```json
{
  "id": 1,
  "label": "待支付",
  "value": "1",
  "isDefault": 0,
  "sort": 2,
  "remark": "订单已提交"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典数据ID |
| label | string | 否 | 字典标签 |
| value | string | 否 | 字典键值（修改时若值变更则校验唯一性） |
| isDefault | int | 否 | 是否默认 |
| sort | int | 否 | 排序值 |
| remark | string | 否 | 备注 |

**业务逻辑：**
- 校验字典数据ID是否存在
- 若 `value` 变更，校验同一类型下是否重复

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 4. 删除字典数据

- **URL：** `DELETE /admin/dict/data/{id}`
- **描述：** 根据ID删除字典数据

**请求参数（路径）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典数据ID |

**业务逻辑：**
- 校验字典数据ID是否存在

**成功响应：** `{ "code": 0, "msg": "success" }`

---

### 5. 根据ID查询字典数据

- **URL：** `GET /admin/dict/data/{id}`
- **描述：** 查询单个字典数据详情

**请求参数（路径）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | long | 是 | 字典数据ID |

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "dictTypeId": 1,
    "label": "待付款",
    "value": "1",
    "isDefault": 1,
    "status": 1,
    "sort": 1,
    "remark": "订单已提交，等待支付",
    "createTime": "2024-01-01 12:00:00",
    "updateTime": "2024-01-01 12:00:00",
    "createUser": 1,
    "updateUser": 1
  }
}
```

---

### 6. 根据字典类型编码查询字典数据

- **URL：** `GET /admin/dict/data/listByCode/{code}`
- **描述：** 根据字典类型编码查询该类型下所有字典数据（仅返回启用状态的数据）

**请求参数（路径）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| code | string | 是 | 字典类型编码 |

**业务逻辑：**
- 校验字典编码是否存在
- 仅返回 `status = 1`（启用状态）的数据
- 按 `sort` 升序排序

**成功响应：**

```json
{
  "code": 0,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "dictTypeId": 1,
      "label": "待付款",
      "value": "1",
      "isDefault": 1,
      "status": 1,
      "sort": 1
    },
    {
      "id": 2,
      "dictTypeId": 1,
      "label": "待接单",
      "value": "2",
      "isDefault": 0,
      "status": 1,
      "sort": 2
    }
  ]
}
```

---

### 7. 启用/禁用字典数据

- **URL：** `PUT /admin/dict/data/status/{status}?id={id}`
- **描述：** 启用或禁用指定字典数据

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | int（路径） | 是 | 状态值（0:禁用 1:启用） |
| id | long（Query） | 是 | 字典数据ID |

**业务逻辑：**
- 校验字典数据ID是否存在

**成功响应：** `{ "code": 0, "msg": "success" }`
