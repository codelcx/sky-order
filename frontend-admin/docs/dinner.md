# 餐桌管理接口文档

**基础路径：** `/api`

**全局响应格式：**
```json
{
  "code": 0,       // 0-成功，1-失败
  "msg": "success",
  "data": {}
}
```

**分页响应格式：**
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 100,
    "records": [],
    "pageSize": 10,
    "pageNum": 1
  }
}
```

---

## 1. 管理端接口 (Admin)

> 请求路径前缀：`/admin/dinnerTable`
> 需要管理端 JWT Token 鉴权。

### 1.1 新增餐桌

**POST** `/admin/dinnerTable`

**请求体 (JSON)：**
```json
{
  "tableNumber": 1,
  "capacity": 4
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| tableNumber | Integer | 是 | 桌号，唯一 |
| capacity | Integer | 是 | 座位数 |

**自动行为：** 状态默认设为 `0`（空闲），创建后自动生成二维码图片。

**响应：**
```json
{
  "code": 0,
  "msg": "success"
}
```

**异常：**
| code | msg | 条件 |
|------|-----|------|
| 1 | 桌号已存在，请重新输入 | 桌号重复 |
| 1 | 二维码生成失败 | 二维码文件写入失败 |

---

### 1.2 修改餐桌

**PUT** `/admin/dinnerTable`

**请求体 (JSON)：**
```json
{
  "id": 1,
  "tableNumber": 2,
  "capacity": 6
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 餐桌ID |
| tableNumber | Integer | 否 | 桌号 |
| capacity | Integer | 否 | 座位数 |

**自动行为：** 如果桌号变更且新桌号已存在则报错；更新后重新生成二维码。

**响应：**
```json
{
  "code": 0,
  "msg": "success"
}
```

---

### 1.3 删除餐桌

**DELETE** `/admin/dinnerTable/{id}`

**路径参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 餐桌ID |

**响应：**
```json
{
  "code": 0,
  "msg": "success"
}
```

---

### 1.4 根据ID查询

**GET** `/admin/dinnerTable/{id}`

**路径参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 餐桌ID |

**响应：**
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "tableNumber": 1,
    "capacity": 4,
    "status": 0,
    "qrCodeUrl": "/api/upload/qrcodes/table_1.png",
    "createTime": "2026-06-12 10:00:00",
    "updateTime": "2026-06-12 10:00:00",
    "createUser": 1,
    "updateUser": 1
  }
}
```

---

### 1.5 分页查询

**GET** `/admin/dinnerTable/page`

**查询参数：**
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页记录数 |
| tableNumber | Integer | 否 | — | 桌号筛选 |
| status | Integer | 否 | — | 状态筛选（0/1/2） |

**响应：**
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "total": 10,
    "records": [
      {
        "id": 1,
        "tableNumber": 1,
        "capacity": 4,
        "status": 0,
        "qrCodeUrl": "/api/upload/qrcodes/table_1.png",
        "createTime": "2026-06-12 10:00:00",
        "updateTime": "2026-06-12 10:00:00",
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

### 1.6 修改餐桌状态

**POST** `/admin/dinnerTable/status/{status}?id={id}`

**路径参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| status | Integer | 0-空闲，1-使用中，2-已预订 |

**查询参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| id | Long | 餐桌ID |

**响应：**
```json
{
  "code": 0,
  "msg": "success"
}
```

---

## 2. 用户端接口 (User)

> 请求路径前缀：`/user/dinnerTable`
> 无需鉴权。

### 2.1 根据桌号查询餐桌

**GET** `/user/dinnerTable/{tableNumber}`

**路径参数：**
| 参数 | 类型 | 说明 |
|------|------|------|
| tableNumber | Integer | 桌号（顾客扫码时传入） |

**响应：**
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "tableNumber": 1,
    "capacity": 4,
    "status": 0,
    "qrCodeUrl": "/api/upload/qrcodes/table_1.png",
    "createTime": "2026-06-12 10:00:00",
    "updateTime": "2026-06-12 10:00:00",
    "createUser": 1,
    "updateUser": 1
  }
}
```

---

## 3. 数据模型

### 3.1 数据库表结构

```sql
CREATE TABLE `dinner_table` (
  `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_number` int          NOT NULL                   COMMENT '桌号',
  `capacity`     int          NOT NULL DEFAULT '4'       COMMENT '座位数',
  `status`       int          NOT NULL DEFAULT '0'       COMMENT '状态 0:空闲 1:使用中 2:已预订',
  `qr_code_url`  varchar(500) DEFAULT NULL               COMMENT '二维码图片URL',
  `create_time`  datetime     DEFAULT NULL               COMMENT '创建时间',
  `update_time`  datetime     DEFAULT NULL               COMMENT '更新时间',
  `create_user`  bigint       DEFAULT NULL               COMMENT '创建人',
  `update_user`  bigint       DEFAULT NULL               COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_table_number` (`table_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='餐桌表';
```

### 3.2 状态枚举

| 值 | 含义 |
|----|------|
| 0 | 空闲 |
| 1 | 使用中 |
| 2 | 已预订 |

### 3.3 实体字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键ID |
| tableNumber | Integer | 桌号 |
| capacity | Integer | 座位数 |
| status | Integer | 状态（0/1/2） |
| qrCodeUrl | String | 二维码图片访问URL |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| createUser | Long | 创建人ID |
| updateUser | Long | 修改人ID |

### 3.4 校验规则

| 接口 | 规则 |
|------|------|
| 新增 (POST) | 分组 `Add.class`：tableNumber、capacity 不能为空 |
| 修改 (PUT) | 分组 `Update.class`：id 不能为空；tableNumber 变更时检查唯一性 |
| 删除 (DELETE) | 检查 ID 是否存在 |
| 修改状态 | status 范围 0~2（`@Range(max = 2L)`） |

---

## 4. 二维码说明

- **生成时机：** 新增餐桌 / 更新餐桌（桌号变更时重新生成）
- **存储位置：** `{upload-path}/qrcodes/table_{id}.png`
- **访问地址：** `GET /api/upload/qrcodes/table_{id}.png`
- **二维码内容：** `/api/user/dinnerTable/{tableNumber}`（查表接口路径，前端需据此构造扫码后跳转 URL）
- **生成工具：** ZXing (Google)
