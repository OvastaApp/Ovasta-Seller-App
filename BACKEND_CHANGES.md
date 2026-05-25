# Backend Changes Required — FCM Push Notifications

## Overview

The Android app now implements FCM push notifications. The app retrieves a device token (FCM token) and sends it to the backend so that notifications can be pushed to specific users. There are **2 backend changes required**:

---

## 1. Update Login Endpoint — Accept `fcm_token`

**Endpoint:** `POST /login`

**Current request body:**
```json
{
  "mobile": "string",
  "password": "string"
}
```

**New request body (add `fcm_token` field):**
```json
{
  "mobile": "string",
  "password": "string",
  "fcm_token": "string"
}
```

| Field       | Type   | Required | Description                         |
|-------------|--------|----------|-------------------------------------|
| `mobile`    | string | Yes      | User phone number (existing)         |
| `password`  | string | Yes      | User password (existing)             |
| `fcm_token` | string | No       | Firebase Cloud Messaging device token |

**What the backend needs to do:**
- Accept the optional `fcm_token` field in the login request
- Store the `fcm_token` in the user record in the database
- When sending a push notification to this user, use the stored `fcm_token` as the FCM registration token
- If `fcm_token` is `null` or missing, the login should still succeed (backward compatible)

---

## 2. New Endpoint — Update FCM Token

**Endpoint:** `POST /fcm-token`

This endpoint is called when the FCM token changes while the user is already logged in (e.g., token rotation by Firebase). It must be authenticated (requires `Authorization: Bearer <token>` header).

**Request body:**
```json
{
  "fcm_token": "string"
}
```

| Field       | Type   | Required | Description          |
|-------------|--------|----------|----------------------|
| `fcm_token` | string | Yes      | New FCM device token |

**Response** (using existing `ApiResponse` wrapper):
```json
{
  "status": 200,
  "message": "Token updated successfully",
  "data": null,
  "last_page": 1,
  "page": 1,
  "per_page": 10,
  "total": 0,
  "token": ""
}
```

**Error responses:**

| Status | Message                    | Condition                    |
|--------|----------------------------|------------------------------|
| 401    | Unauthorized               | Missing or invalid auth token |
| 422    | Validation error           | `fcm_token` is empty or missing |

**What the backend needs to do:**
- Create a new `POST /fcm-token` endpoint (under the seller-app routes)
- Require authentication (Bearer token in Authorization header)
- Update the authenticated user's `fcm_token` in the database
- Return success response

---

## How the App Uses These Endpoints

### On Login
1. App retrieves FCM token from Firebase
2. App caches token in local storage (`SessionPreferences.fcmToken`)
3. App sends `fcm_token` field in the `POST /login` request body
4. Backend should store this token associated with the user

### On FCM Token Refresh (user already logged in)
1. Firebase calls `onNewToken()` in the app
2. App sends `POST /fcm-token` with the new token (with auth headers)
3. Backend updates the user's stored token

### On App Start (token comparison)
1. App fetches current FCM token from Firebase
2. Compares with cached token
3. If different and user is logged in: sends `POST /fcm-token` to update

---

## Sending Notifications (Backend Side)

To send a push notification to a user, use the stored `fcm_token` with Firebase Admin SDK or FCM HTTP v1 API:

```
POST https://fcm.googleapis.com/v1/projects/{project-id}/messages:send
Authorization: Bearer {google-oauth2-token}

{
  "message": {
    "token": "{fcm_token}",
    "notification": {
      "title": "New Order",
      "body": "You have a new order"
    },
    "data": {
      "order_id": "123",
      "type": "new_order"
    }
  }
}
```

The app handles both `notification` and `data` payloads:
- **`notification` payload** (title + body): Shows as a visible notification
- **`data` payload** (key-value pairs): If no `notification` payload, uses `title` and `body` keys from data to show a notification

---

## Database Change

Add an `fcm_token` column to the users/sellers table:

```sql
ALTER TABLE sellers ADD COLUMN fcm_token VARCHAR(255) DEFAULT NULL;
```

This column should be updated whenever the token changes (login or `/fcm-token` endpoint).