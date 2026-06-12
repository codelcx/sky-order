# 注释

所有实体字段的注释必须使用 `/** */` 单行 JSDoc 格式。禁止使用 `//` 行注释。

# 枚举

如果字段的可选值是确定且有限的，必须定义为枚举类型。禁止使用普通类型（如 `string`/`number`）加注释说明。

对比示例：

```typescript
// ❌ 不推荐
// status: 0-disabled 1-enabled
status: number;

// ✅ 推荐
/** Status */
enum Status {
  Disabled = 0,
  Enabled = 1,
}
status: Status;
```

```typescript
// ❌ 不推荐
// sex: 0-female 1-male
sex: string;

// ✅ 推荐
enum Gender {
  Female = 0,
  Male = 1,
}
sex: Gender;
```

# 类型分隔

接口文件中，类型定义需按「枚举 / 实体 / 请求参数 / 响应体」分类分组，每组前后用分隔线包裹，格式如下：

```typescript
// ============================================================================
// 枚举
// ============================================================================
enum Status { ... }

// ============================================================================
// 实体
// ============================================================================
interface User { ... }

// ============================================================================
// 请求参数
// ============================================================================
interface GetUserReq { ... }

// ============================================================================
// 响应体
// ============================================================================
interface GetUserResp { ... }
```

# 命名规范

文件夹命名除组件（`components/` 下的子功能组件）使用 PascalCase 外，其余均使用短横线分隔（kebab-case）。

对比示例：

```
# ✅ 推荐
src/api/dinner-table/
src/pages/order-list/
src/pages/tables/components/DinnerTableModal/   # 组件使用 PascalCase

# ❌ 不推荐
src/api/dinnerTable/
src/pages/orderList/
src/pages/tables/components/dinner-table-modal/
```

# 组件拆分

组件按功能维度拆分。每个功能模块的入口为 `index.vue`（或 `index.tsx`），统一放置在模块根目录。`components/` 目录存放该模块的子功能组件（如弹窗、卡片等），子功能组件均以文件夹形式创建，入口文件统一为 `index.vue` 或 `index.tsx`。若模块需要仅自身使用的 hooks / stores，同样放在 `components/` 目录下（与子功能组件平级），hooks 和 stores 为文件平铺，无需为每个文件创建文件夹。

目录结构示例：

```
modules/order/
├── index.vue                     # 功能模块入口（表格页面）
├── components/
│   ├── OrderDialog/              # 子功能组件（弹窗）
│   │   └── index.vue
│   ├── OrderDetailCard/          # 子功能组件（卡片）
│   │   └── index.vue
│   ├── hooks/                    # 模块私有 hooks，平铺
│   │   ├── useOrderList.ts
│   │   └── useOrderDetail.ts
│   └── stores/                   # 模块私有 stores，平铺
│       ├── orderStore.ts
│       └── orderFilterStore.ts
```
