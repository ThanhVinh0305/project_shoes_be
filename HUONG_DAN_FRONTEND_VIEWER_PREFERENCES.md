# Hướng Dẫn Frontend - Viewer Preferences (Lượt Xem Cao Nhất)

## 📌 Tổng Quan

Backend đã thêm **block gợi ý mới** dựa trên AI tracking hành vi người dùng: **`viewer_preferences`**

**Tên hiển thị**: "Lượt Xem Cao Nhất" hoặc "Dành Cho Bạn"

---

## 🔗 API Endpoint

### **GET** `/v2/open-api/recommend/blocks`

**Query Parameters:**
- `userId` (optional): ID của user đã login
  - Nếu **có userId**: Trả về 5 blocks (bao gồm `viewer_preferences`)
  - Nếu **không có userId** (guest): Chỉ trả về 2 blocks (`guest_sale`, `guest_today`)

**Response Format:**
```json
{
  "success": true,
  "data": {
    "guest_sale": [...],           // Sản phẩm giảm giá (cho cả guest)
    "guest_today": [...],          // Gợi ý hôm nay random (cho cả guest)
    "user_top_search": [...],      // Top tìm kiếm (user only, hiện tại disabled)
    "user_top_viewed": [...],      // Lượt xem cao từ tất cả users
    "viewer_preferences": [...]    // ⭐ BLOCK MỚI - Gợi ý dựa trên hành vi
  }
}
```

---

## ⚙️ Logic Block `viewer_preferences`

### **Cách hoạt động:**

1. **Khi user chưa có dữ liệu hành vi** (user mới):
   - Trả về **random products** theo giới tính user
   - Giống block `guest_today` nhưng filter theo gender

2. **Khi user đã có hành vi** (click, view, add-to-cart, search):
   - Script Python `compute_user_preferences.py` tính điểm cho từng **brand/product** user quan tâm
   - Backend lấy **top 3 brands** user thích nhất từ bảng `user_preferences`
   - Gợi ý **sản phẩm mới** từ các brands đó
   - **Loại trừ** sản phẩm user đã xem/click (tránh trùng lặp)
   - **Filter theo giới tính** user

### **Điều kiện để có gợi ý:**

✅ User đã track hành vi qua các API:
- `POST /v2/api/v1/behavior/view` - Xem sản phẩm
- `POST /v2/api/v1/behavior/click` - Click vào sản phẩm
- `POST /v2/api/v1/behavior/add-to-cart` - Thêm vào giỏ
- `POST /v2/api/v1/behavior/search` - Tìm kiếm

✅ Script AI đã chạy (chạy tự động lúc 2:00 AM hằng ngày)

---

## 🎨 Frontend Implementation

### **1. Call API**

```typescript
// Angular Service
getRecommendBlocks(userId?: number): Observable<RecommendBlocksResponse> {
  const params = userId ? { userId: userId.toString() } : {};
  return this.http.get<ApiResponse<RecommendBlocksResponse>>(
    `${API_BASE_URL}/open-api/recommend/blocks`,
    { params }
  );
}
```

### **2. Component Usage**

```typescript
export class HomeComponent implements OnInit {
  guestSale: Product[] = [];
  guestToday: Product[] = [];
  viewerPreferences: Product[] = []; // ⭐ Block mới
  userTopViewed: Product[] = [];

  ngOnInit() {
    const userId = this.authService.getCurrentUserId(); // Lấy từ JWT token
    
    this.recommendService.getRecommendBlocks(userId).subscribe(response => {
      if (response.success) {
        this.guestSale = response.data.guest_sale || [];
        this.guestToday = response.data.guest_today || [];
        this.viewerPreferences = response.data.viewer_preferences || [];
        this.userTopViewed = response.data.user_top_viewed || [];
      }
    });
  }
}
```

### **3. Template Display**

```html
<!-- Block: Sản phẩm giảm giá (cho tất cả users) -->
<div class="product-section" *ngIf="guestSale.length > 0">
  <h2>⚡ Flash Sale</h2>
  <app-product-carousel [products]="guestSale"></app-product-carousel>
</div>

<!-- Block: Gợi ý hôm nay (cho tất cả users) -->
<div class="product-section" *ngIf="guestToday.length > 0">
  <h2>🎯 Gợi Ý Hôm Nay</h2>
  <app-product-carousel [products]="guestToday"></app-product-carousel>
</div>

<!-- Block: Lượt Xem Cao Nhất - AI Personalized (user only) ⭐ -->
<div class="product-section" *ngIf="viewerPreferences.length > 0">
  <h2>👀 Lượt Xem Cao Nhất</h2>
  <p class="subtitle">Dựa trên sở thích của bạn</p>
  <app-product-carousel [products]="viewerPreferences"></app-product-carousel>
</div>

<!-- Block: Top lượt xem (user only) -->
<div class="product-section" *ngIf="userTopViewed.length > 0">
  <h2>🔥 Đang Hot</h2>
  <app-product-carousel [products]="userTopViewed"></app-product-carousel>
</div>
```

---

## 🧪 Testing Flow

### **Bước 1: Tạo dữ liệu test**

```bash
# 1. Track một số hành vi (dùng Postman hoặc frontend)
POST http://localhost:5252/v2/api/v1/behavior/view
Headers: Authorization: Bearer <JWT_TOKEN>
Body: {
  "productId": 1,
  "viewDuration": 30
}

POST http://localhost:5252/v2/api/v1/behavior/click
Body: {
  "productId": 2,
  "clickSource": "home_page"
}

# 2. Chạy script Python để tính preferences (thay vì đợi 2:00 AM)
cd "ai/scripts"
python3 compute_user_preferences.py
# Output: ✓ Done. Brands: 5 entries, Colors: 8, Products: 10
```

### **Bước 2: Test API**

```bash
# Guest user (không có userId)
curl "http://localhost:5252/v2/open-api/recommend/blocks"
# Kết quả: Chỉ có guest_sale và guest_today

# Logged-in user
curl "http://localhost:5252/v2/open-api/recommend/blocks?userId=1"
# Kết quả: Có đủ 5 blocks (bao gồm viewer_preferences)
```

### **Bước 3: Verify Response**

```json
{
  "success": true,
  "data": {
    "guest_sale": [
      {
        "id": 5,
        "name": "Nike Air Force 1",
        "price": 2200000,
        "thumbnail": "http://localhost:9000/products/thumbnails/...",
        "brand": { "name": "Nike" }
      }
    ],
    "viewer_preferences": [
      {
        "id": 12,
        "name": "Nike Dunk Low",
        "price": 2800000,
        "thumbnail": "...",
        "brand": { "name": "Nike" }  // ← Từ brand user thích
      },
      {
        "id": 15,
        "name": "Adidas Superstar",
        "brand": { "name": "Adidas" }  // ← Brand khác user quan tâm
      }
    ]
  }
}
```

---

## 🎯 Best Practices

### **1. Hiển thị có điều kiện**

```typescript
// Chỉ hiển thị khi có ít nhất 3 sản phẩm
get shouldShowViewerPreferences(): boolean {
  return this.viewerPreferences.length >= 3;
}
```

### **2. Fallback khi chưa có data**

```html
<div *ngIf="viewerPreferences.length === 0 && isLoggedIn">
  <p>Chúng tôi đang tìm hiểu sở thích của bạn...</p>
  <p>Hãy xem thêm sản phẩm để nhận gợi ý cá nhân hóa!</p>
</div>
```

### **3. Tracking Behavior từ Frontend**

```typescript
// Khi user xem sản phẩm
onProductView(productId: number) {
  this.behaviorService.trackView(productId, this.viewDuration).subscribe();
}

// Khi user click vào sản phẩm
onProductClick(productId: number) {
  this.behaviorService.trackClick(productId, 'recommendation_block').subscribe();
  this.router.navigate(['/product', productId]);
}

// Khi thêm vào giỏ
onAddToCart(productId: number, quantity: number) {
  this.cartService.addToCart(productId, quantity);
  this.behaviorService.trackAddToCart(productId, quantity).subscribe();
}
```

---

## 📊 Data Flow

```
Frontend User Actions
    ↓
POST /api/v1/behavior/view, /click, /add-to-cart
    ↓
MySQL Tables: user_behaviors, product_views, search_histories
    ↓
Python Script (2:00 AM daily): compute_user_preferences.py
    ↓
MySQL Table: user_preferences (brand/color/product scores)
    ↓
GET /open-api/recommend/blocks?userId=1
    ↓
RecommendBlockUseCaseImpl.queryViewerPreferences()
    ↓
Frontend: viewer_preferences array
```

---

## ❓ FAQs

**Q: Tại sao `viewer_preferences` trả về empty array?**

A: Có 3 lý do:
1. User chưa track hành vi nào (cần call behavior APIs)
2. Script Python chưa chạy (chạy thủ công hoặc đợi 2:00 AM)
3. User chưa có đủ dữ liệu (cần ít nhất 5-10 interactions)

**Q: Có cần authenticate không?**

A: 
- **Block `viewer_preferences`**: Cần userId (user đã login)
- **Blocks `guest_sale`, `guest_today`**: Không cần, guest cũng xem được

**Q: Làm sao biết script Python đã chạy?**

A: Check database:
```sql
SELECT COUNT(*) FROM user_preferences WHERE user_id = 1;
-- Nếu > 0: Script đã chạy
```

**Q: Thời gian response bao lâu?**

A: Trung bình 100-300ms (tùy số lượng behaviors và products)

---

## 🚀 Next Steps

1. ✅ **Backend đã sẵn sàng** - API hoạt động
2. ⏳ **Frontend cần implement**:
   - Call API `/open-api/recommend/blocks?userId={id}`
   - Parse response `viewer_preferences`
   - Display carousel/grid với title "Lượt Xem Cao Nhất"
3. ⏳ **Track behaviors**:
   - Implement behavior tracking services
   - Call APIs khi user view/click/add-to-cart
4. ⏳ **Monitor**:
   - Check `user_preferences` table có data chưa
   - Verify recommendations có relevant không

---

## 📞 Support

Nếu cần debug:
```bash
# Check logs
docker logs spring-boot-app --tail 50

# Check database
docker exec -it mysqldb mysql -uroot -ptuanhung1999 e-commerce
> SELECT * FROM user_preferences WHERE user_id = 1 LIMIT 10;
> SELECT * FROM user_behaviors WHERE user_id = 1 LIMIT 10;
```

---

**Version**: 1.0  
**Last Updated**: 2026-01-08  
**Author**: AI Development Team
