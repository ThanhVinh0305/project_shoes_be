# 🎯 BEHAVIOR TRACKING SYSTEM - IMPLEMENTATION SUMMARY

## ✅ Đã hoàn thành (Phase 1: Behavior Tracking)

### 1. **Database Entities Created** (5 tables)

#### Entities:
- ✅ **UserBehavior** - Theo dõi mọi hành vi người dùng
- ✅ **ProductView** - Chi tiết lượt xem sản phẩm
- ✅ **SearchHistory** - Lịch sử tìm kiếm
- ✅ **UserPreference** - Sở thích người dùng (computed)
- ✅ **Recommendation** - Kết quả gợi ý AI

#### JPA Repositories:
- ✅ **UserBehaviorRepository** - CRUD + queries
- ✅ **ProductViewRepository** - Track views + aggregations
- ✅ **SearchHistoryRepository** - Search history + top keywords
- ✅ **UserPreferenceRepository** - Preferences management
- ✅ **RecommendationRepository** - Recommendations storage

**Total: 27 JPA Repositories** (tăng từ 22 → 27)

---

### 2. **Domain Layer** 

#### Adapters (Interfaces):
- ✅ **UserBehaviorAdapter**
- ✅ **ProductViewAdapter**
- ✅ **SearchHistoryAdapter**
- ✅ **UserPreferenceAdapter**
- ✅ **RecommendationAdapter**

#### Adapter Implementations:
- ✅ **UserBehaviorAdapterImpl**
- ✅ **ProductViewAdapterImpl**
- ✅ **SearchHistoryAdapterImpl**
- ✅ **UserPreferenceAdapterImpl**
- ✅ **RecommendationAdapterImpl**

---

### 3. **Use Cases**

#### Interface:
- ✅ **ITrackBehaviorUseCase**

#### Implementation:
- ✅ **TrackBehaviorUseCaseImpl** với các methods:
  - `trackProductView()` - Track khi xem sản phẩm
  - `trackSearch()` - Track khi tìm kiếm
  - `trackClick()` - Track khi click
  - `trackAddToCart()` - Track khi thêm vào giỏ
  - `trackPurchase()` - Track khi mua hàng

---

### 4. **REST APIs**

#### Controller: `IBehaviorController`

**Base URL:** `/api/v1/behavior`

| Endpoint | Method | Description | Request Body |
|----------|--------|-------------|--------------|
| `/view` | POST | Track product view | `{productId, viewDuration}` |
| `/search` | POST | Track search | `{keyword, filters, resultCount}` |
| `/click` | POST | Track click | `{productId, source}` |
| `/add-to-cart` | POST | Track add to cart | `{productId}` |

**Features:**
- ✅ Hỗ trợ cả **authenticated users** và **guest users**
- ✅ Tự động lấy userId từ security context
- ✅ Error handling graceful (không crash app)
- ✅ Async-friendly design

---

## 📊 DATABASE SCHEMA

### Bảng `user_behaviors`
```sql
- id (PK)
- user_id (nullable - cho guest)
- product_id
- behavior_type (VIEW, CLICK, SEARCH, ADD_TO_CART, PURCHASE, RATING)
- behavior_data (JSON)
- created_date
```

### Bảng `product_views`
```sql
- id (PK)
- user_id (nullable)
- product_id
- view_duration (seconds)
- view_count
- last_viewed_date
- created_date
```

### Bảng `search_histories`
```sql
- id (PK)
- user_id (nullable)
- search_keyword
- search_filters (JSON)
- result_count
- created_date
```

### Bảng `user_preferences`
```sql
- id (PK)
- user_id
- preference_type (BRAND, CATEGORY, PRICE_RANGE, COLOR, GENDER, STYLE)
- preference_value
- preference_score (0-100)
- last_updated
- created_date
```

### Bảng `recommendations`
```sql
- id (PK)
- user_id
- product_id
- recommendation_type (COLLABORATIVE_FILTERING, CONTENT_BASED, HYBRID, TRENDING, SIMILAR_PRODUCTS)
- recommendation_score
- reason (TEXT)
- is_shown
- is_clicked
- created_date
```

---

## 🚀 USAGE EXAMPLES

### Frontend Integration

#### 1. Track product view
```javascript
// Khi user xem chi tiết sản phẩm
fetch('/api/v1/behavior/view', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    productId: 123,
    viewDuration: 45 // seconds
  })
});
```

#### 2. Track search
```javascript
// Khi user search
fetch('/api/v1/behavior/search', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    keyword: "giày nike",
    filters: '{"brandId":1,"priceRange":"low"}',
    resultCount: 15
  })
});
```

#### 3. Track click
```javascript
// Khi user click vào sản phẩm từ recommendation
fetch('/api/v1/behavior/click', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    productId: 123,
    source: "recommendation" // or "search", "similar"
  })
});
```

#### 4. Track add to cart
```javascript
// Khi user thêm vào giỏ
fetch('/api/v1/behavior/add-to-cart', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    productId: 123
  })
});
```

---

## 📈 NEXT STEPS (Chưa implement)

### Phase 2: User Preference Calculation
- [ ] Python script `compute_user_preferences.py` cải tiến
- [ ] Scheduler chạy mỗi 6 giờ
- [ ] Weighted scoring với time decay
- [ ] API: `GET /api/v1/users/{id}/preferences`

### Phase 3: Behavior-Based Recommendation
- [ ] Session-based recommender
- [ ] Preference-based recommender
- [ ] Collaborative filtering upgrade
- [ ] Hybrid score blending

### Phase 4: Testing & Optimization
- [ ] A/B testing framework
- [ ] CTR & conversion metrics
- [ ] Performance optimization

---

## 🎁 IMPACT

**Trước:**
- Không track hành vi người dùng
- Recommendation chỉ dựa vào "sản phẩm bán chạy"
- Không personalization

**Sau (Phase 1 hoàn thành):**
- ✅ Track đầy đủ hành vi real-time
- ✅ Database ready cho AI recommendations
- ✅ 27 JPA repositories hoạt động
- ✅ APIs production-ready
- 🔜 Sẵn sàng cho Phase 2 (Preference Calculation)

---

## 🔧 DEPLOYMENT STATUS

- ✅ **Build:** Successful
- ✅ **Docker:** Running
- ✅ **MySQL:** Connected
- ✅ **APIs:** Available at `http://localhost:5252/v2/api/v1/behavior/*`
- ✅ **JPA Repositories:** 27/27 detected

---

## 📝 FILES CREATED

### Domain Models (5)
- `UserBehavior.java`
- `ProductView.java`
- `SearchHistory.java`
- `UserPreference.java`
- `Recommendation.java`

### Entity Classes (5)
- `UserBehaviorEntity.java`
- `ProductViewEntity.java`
- `SearchHistoryEntity.java`
- `UserPreferenceEntity.java`
- `RecommendationEntity.java`

### Repositories (5)
- `UserBehaviorRepository.java`
- `ProductViewRepository.java`
- `SearchHistoryRepository.java`
- `UserPreferenceRepository.java`
- `RecommendationRepository.java`

### Adapters (10 = 5 interfaces + 5 implementations)
- Interfaces: `*Adapter.java` (5 files)
- Implementations: `*AdapterImpl.java` (5 files)

### Use Cases (2)
- `ITrackBehaviorUseCase.java`
- `TrackBehaviorUseCaseImpl.java`

### Controllers (2)
- `IBehaviorController.java`
- `BehaviorControllerImpl.java`

### Request DTOs (4)
- `TrackViewRequest.java`
- `TrackSearchRequest.java`
- `TrackClickRequest.java`
- `TrackCartRequest.java`

**Total: 33 files created** ✨
