# 员工管理接口文档


## 3. 新增员工

> `POST /admin/employee`

默认密码为 `123456`（MD5 加密存储）。

### Request Body

```json
{
  "username": "zhangsan",
  "name": "张三",
  "phone": "13800138000",
  "sex": "1",
  "idNumber": "110101199001011234"
}
```

### 字段说明

| 字段名 | 类型 | 必填 | 约束 | 说明 |
|--------|------|------|------|------|
| username | String | 是 | 3~8 字符 | 登录账号 |
| name | String | 是 | 2~4 字符 | 姓名 |
| phone | String | 是 | 11 位手机号 | 手机号码 |
| sex | String | 是 | `0` 或 `1` | 性别（0:女，1:男） |
| idNumber | String | 是 | 15 或 18 位 | 身份证号 |

### Response

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

---

## 4. 员工分页查询

> `GET /admin/employee/page`

### Query Parameters

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| name | String | 否 | - | 员工姓名（模糊查询） |
| page | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页记录数 |

### Response

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 100,
    "records": [
      {
        "id": 1,
        "username": "admin",
        "name": "管理员",
        "phone": "13800138000",
        "sex": "1",
        "idNumber": "110101199001011234",
        "status": 1,
        "createTime": "2024-01-01T00:00:00",
        "updateTime": "2024-01-01T00:00:00",
        "createUser": 1,
        "updateUser": 1
      }
    ],
    "pageSize": 10,
    "pageNum": 1
  }
}
```

| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Long | 总记录数 |
| records | Array | 员工列表（详见下方 Entity 字段） |
| pageSize | int | 每页数量 |
| pageNum | int | 当前页码 |

### Employee 实体字段

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 主键 ID |
| username | String | 用户名 |
| name | String | 姓名 |
| phone | String | 手机号 |
| sex | String | 性别（0:女，1:男） |
| idNumber | String | 身份证号 |
| status | Integer | 状态（0:禁用，1:启用） |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| createUser | Long | 创建人 ID |
| updateUser | Long | 修改人 ID |

> 注：`password` 字段不参与序列化（`@JsonIgnore`），不会返回给前端。

---

## 5. 修改员工状态

> `POST /admin/employee/status/{status}`

### Path & Query Parameters

| 参数名 | 类型 | 必填 | 约束 | 说明 |
|--------|------|------|------|------|
| status | Integer | 是 | 0 或 1 | 目标状态（0:禁用，1:启用） |
| id | Long | 是 | - | 员工 ID（Query 参数） |

### Request Example

```
POST /admin/employee/status/1?id=1
```

### Response

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```

---

## 6. 根据 ID 查询员工

> `GET /admin/employee/{id}`

### Path Parameters

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 员工 ID |

### Response

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "name": "管理员",
    "phone": "13800138000",
    "sex": "1",
    "idNumber": "110101199001011234",
    "status": 1,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00",
    "createUser": 1,
    "updateUser": 1
  }
}
```

---

## 7. 更新员工信息

> `PUT /admin/employee`

### Request Body

```json
{
  "id": 1,
  "username": "zhangsan",
  "name": "张三",
  "phone": "13800138000",
  "sex": "1",
  "idNumber": "110101199001011234"
}
```

### 字段说明

| 字段名 | 类型 | 必填 | 约束 | 说明 |
|--------|------|------|------|------|
| id | Long | 是（仅更新时） | `@NotNull` | 员工 ID |
| username | String | 否 | 3~8 字符 | 登录账号 |
| name | String | 否 | 2~4 字符 | 姓名 |
| phone | String | 否 | 11 位手机号 | 手机号码 |
| sex | String | 否 | `0` 或 `1` | 性别 |
| idNumber | String | 否 | 15 或 18 位 | 身份证号 |

### Response

```json
{
  "code": 0,
  "msg": "success",
  "data": null
}
```
