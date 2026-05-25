# Ovasta Seller App — Home API Contract

> Generated from Android codebase models. Base URL: `{BASE_URL}/`

---

## Common Response Wrapper

All endpoints return responses wrapped in:

```json
{
  "status": 200,
  "message": "string",
  "data": "<T>",
  "last_page": 1,
  "page": 1,
  "per_page": 10,
  "total": 0,
  "token": "string"
}
```

| Field       | Type    | Description                        |
|-------------|---------|------------------------------------|
| `status`    | int     | HTTP-like status code              |
| `message`   | string  | Human-readable message             |
| `data`      | T       | Response payload (varies per API)  |
| `last_page` | int     | Pagination: last page number       |
| `page`      | int     | Pagination: current page           |
| `per_page`  | int     | Pagination: items per page         |
| `total`     | int     | Pagination: total items            |
| `token`     | string  | Auth token (if applicable)         |

---

## 1. Create Order

**`POST /create-order`**

### Request Body

```json
{
  "destination": "string",
  "client_phone": "string",
  "client_name": "string",
  "note": "string"
}
```

| Field          | Type   | Required | Description              |
|----------------|--------|----------|--------------------------|
| `destination`  | string | ✅       | Delivery destination     |
| `client_phone` | string | ✅       | Client phone number      |
| `client_name`  | string | ✅       | Client name              |
| `note`         | string | ✅       | Order note               |

### Response — `data: PointsInfo`

```json
{
  "status": 200,
  "message": "Order created successfully",
  "data": {
    "points": 150,
    "money": 75.5
  },
  ...
}
```

| Field    | Type    | Nullable | Description                  |
|----------|---------|----------|------------------------------|
| `points` | long    | ✅       | Points earned/balance        |
| `money`  | double  | ✅       | Delivery profit sum (money)  |

---

## 2. Get My Orders

**`POST /orders`**

### Request Body

_None (empty body)_

### Response — `data: List<OrderResponse>`

```json
{
  "status": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "client_name": "Ahmed",
      "client_address": "123 Main St",
      "client_phone": "+201234567890",
      "courier": {
        "id": 10,
        "name": "Mohamed",
        "phone": "+201098765432"
      }
    }
  ],
  ...
}
```

#### OrderResponse

| Field            | Type    | Nullable | Description             |
|------------------|---------|----------|-------------------------|
| `id`             | long    | ❌       | Order ID                |
| `client_name`    | string  | ✅       | Client name             |
| `client_address` | string  | ❌       | Client delivery address |
| `client_phone`   | string  | ❌       | Client phone number     |
| `courier`        | Courier | ❌       | Assigned courier object |

#### Courier

| Field   | Type   | Nullable | Description       |
|---------|--------|----------|-------------------|
| `id`    | long   | ❌       | Courier ID        |
| `name`  | string | ✅       | Courier name      |
| `phone` | string | ✅       | Courier phone     |

---

## 3. Get Points Info

**`GET /points`**

### Request

_No parameters_

### Response — `data: PointsInfo`

```json
{
  "status": 200,
  "message": "success",
  "data": {
    "points": 150,
    "money": 75.5
  },
  ...
}
```

| Field    | Type   | Nullable | Description                 |
|----------|--------|----------|-----------------------------|
| `points` | long   | ✅       | Points earned/balance       |
| `money`  | double | ✅       | Delivery profit sum (money) |

---

## Notes for Backend

- All JSON keys use **snake_case**.
- Pagination fields (`last_page`, `page`, `per_page`, `total`) should always be present in the wrapper, even if not applicable (use defaults).
- `token` field in wrapper can be empty string when not relevant.
- `money` field in `PointsInfo` is serialized as `"money"` on the wire but represents delivery profit sum on the client side.

---

## 4. Update FCM Token

**`POST /fcm-token`**

### Request Body

```json
{
  "fcm_token": "string"
}
```

| Field       | Type   | Required | Description          |
|-------------|--------|----------|----------------------|
| `fcm_token` | string | Yes      | FCM device token     |

### Response

```json
{
  "status": 200,
  "message": "Token updated successfully",
  "data": null,
  "token": ""
}
```

---

## Login FCM Token

The `POST /login` endpoint accepts an optional `fcm_token` field in the request body:

```json
{
  "mobile": "string",
  "password": "string",
  "fcm_token": "string (optional)"
}
```

| Field       | Type   | Required | Description                |
|-------------|--------|----------|----------------------------|
| `mobile`    | string | Yes      | User phone number          |
| `password`  | string | Yes      | User password              |
| `fcm_token` | string | No       | FCM device token for push notifications |

