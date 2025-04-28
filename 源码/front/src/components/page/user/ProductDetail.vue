<template>
  <div class="product-detail-container">
    <!-- 商品主要信息区域 -->
    <div class="product-main">
      <!-- 左侧图片展示 -->
      <div class="product-gallery">
        <div class="main-image">
          <img :src="product.image" :alt="product.name" class="gallery-image">
        </div>
      </div>

      <!-- 右侧商品信息 -->
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>
        <div class="product-meta">
          <div class="price-section">
            <span class="price-label">价格</span>
            <span class="price">¥{{ product.money }}</span>
<!--            <span class="original-price" v-if="product.money">¥{{ product.money }}</span>-->
          </div>
          <div class="sales-info">
            <span>销量 {{ product.sale }}</span>
            <span>累计评价 {{ product.reviews.length }}</span>
          </div>
        </div>

        <!-- 商品选择区 -->
        <div class="product-options">

          <!-- 数量选择 -->
          <div class="option-group">
            <div class="option-label">数量</div>
            <div class="option-values">
              <el-input-number 
                v-model="quantity" 
                :min="1" 
                :max="3"
                size="small">
              </el-input-number>
              <span class="stock-info">库存 {{ product.num }} 件</span>
            </div>
          </div>
        </div>

        <!-- 购买按钮组 -->
        <div class="purchase-actions">
          <el-button type="danger" size="large" @click="showBuyDialog = true">立即购买</el-button>
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
        </div>

        <!-- 添加购买对话框 -->
        <el-dialog
          title="确认购买"
          :visible.sync="showBuyDialog"
          width="400px"
          :before-close="handleDialogClose">
          <div class="buy-dialog-content">
            <!-- 商品信息概览 -->
            <div class="dialog-product-info">
              <img :src="product.image" :alt="product.name" class="dialog-product-image">
              <div class="dialog-product-detail">
                <h4>{{ product.name }}</h4>
                <div class="dialog-product-price">¥{{ product.money }}</div>
                <div class="dialog-product-quantity">
                  数量：{{ quantity }} 件
                </div>
              </div>
            </div>

            <!-- 备注输入框 -->
            <div class="remark-input">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="请输入备注信息（选填）"
                v-model="orderRemark"
                maxlength="200"
                show-word-limit>
              </el-input>
            </div>

            <!-- 订单金额信息 -->
            <div class="order-amount">
              <span>订单总额：</span>
              <span class="amount">¥{{ (product.money * quantity).toFixed(2) }}</span>
            </div>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button @click="handleDialogClose">取 消</el-button>
            <el-button type="primary" @click="confirmBuy" :loading="submitting">
              确认下单
            </el-button>
          </span>
        </el-dialog>

        <!-- 服务承诺 -->
        <div class="service-promises">
          <div class="promise-item">
            <i class="el-icon-check"></i>
            <span>正品保证</span>
          </div>
          <div class="promise-item">
            <i class="el-icon-truck"></i>
            <span>极速发货</span>
          </div>
          <div class="promise-item">
            <i class="el-icon-refresh-right"></i>
            <span>7天无理由退换</span>
          </div>
        </div>
      </div>
    </div>


      <!-- 相关帖子模块 -->
      <div class="related-outfits-section">
      <h3>相关穿搭分享</h3>
      <div class="outfits-grid">
        <div v-for="outfit in relatedOutfits" :key="outfit.id" class="outfit-card" @click="$router.push({ path: `/user/outfit/${outfit.id}`, query: { timestamp: new Date().getTime() } })">
          <img :src="outfit.image" :alt="outfit.name">
          <div class="outfit-info">
            <h4>{{ outfit.name }}</h4>
            <p class="views"><i class="el-icon-view"></i> {{ outfit.num }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情和评价区域 -->
    <div class="product-detail-tabs">
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="商品详情" name="details">
          <div class="details-content">
            <div class="detail-section">
              <h3>商品描述</h3>
              <p v-html="product.content"></p>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="商品评价" name="reviews">
          <div class="reviews-section">

            <!-- 评价列表 -->
            <div class="reviews-list" v-if="product.reviews.length>0">
              <div v-for="review in product.reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <img :src="review.userImage" :alt="review.realname" class="user-avatar">
                  <span class="user-name">{{ review.realname }}</span>
                </div>
                <div class="review-content">{{ review.content }}</div>
                <div class="review-footer">
                  <span class="review-time">{{ review.createTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    
  </div>
</template>

<script>
export default {
  name: 'ProductDetail',
  data() {
    return {
      activeTab: 'details',
      selectedColor: '',
      selectedSize: '',
      quantity: 1,
      isFavorite: false,
      currentImageIndex: 0,
      productId:'',
      product: {},
      userInfo: {},
      type:'',
      showBuyDialog: false, // 控制购买对话框显示
      orderRemark: '', // 订单备注
      submitting: false, // 提交状态
      relatedOutfits: [] // 新增：存储相关穿搭数据
    }
  },
  computed: {
    currentImage() {
      return this.product.images[this.currentImageIndex]
    }
  },
  methods: {
    // 新增：获取相关穿搭数据
    fetchRelatedOutfits() {
      this.$axios.get(`/api/outfit/relatedToProduct?productId=${this.productId}`).then(res => {
        if (res.data.code === 200) {
          this.relatedOutfits = res.data.data || [];
        }
      });
    },
    // 处理对话框关闭
    handleDialogClose() {
      this.showBuyDialog = false
      this.orderRemark = ''
    },
    
    // 确认购买
    confirmBuy() {
      if (!this.userInfo) {
        this.$message.warning('请先登录')
        return
      }
      if (this.type !== '02') {
        this.$message.warning('仅用户可以购买')
        return
      }
      if (this.quantity > this.product.num) {
        this.$message.warning('库存不足')
        return
      }

      this.submitting = true
      this.$axios.post('/api/orderItem/xiadan', {
        gid: this.productId,
        uid: this.userInfo.id,
        num: this.quantity,
        remark: this.orderRemark // 添加备注信息
      }).then(res => {
        if (res.data.code === 200) {
          this.$message.success('下单成功，请前往个人中心“我的订单”进行支付')
          this.showBuyDialog = false
          this.orderRemark = ''
          this.getData()
        } else {
          this.$message.warning(res.data.msg)
        }
      }).finally(() => {
        this.submitting = false
      })
    },
    getData(){
             // 根据 productId 获取商品详情
            this.$axios.get('/api/goods/frontOne?id='+this.productId).then(res => {
                this.product = res.data.data;
            });
        },
    addToCart() {
      if (!this.userInfo) {
            this.$message({
            type: 'warning',
            message: '请登录系统！'
            })
            return
        }
        if (this.type!=='02') {
            this.$message({
                type: 'warning',
                message: '请登录用户！'
            })
            return
        }
        if(this.quantity>this.product.num){
            this.$message.warning("库存不足！");
            return;
        }
        this.$axios.post('/api/cart/add', {gid:this.product.id,uid:this.userInfo.id,num:this.quantity}).then(res => {
            if(res.data.code == 200){  // 表示成功
                this.$message.success("加入购物车成功！");
                this.quantity = 1;
                this.getData()
            }else{
                this.$message.warning("加入购物车失败！");
            }
        });
    },
    toggleFavorite() {
      this.isFavorite = !this.isFavorite
      this.$message.success(this.isFavorite ? '已收藏' : '已取消收藏')
    },
    previewImage(image) {
      // 实现图片预览逻辑
    },
    selectImage(index) {
      this.currentImageIndex = index
    }
  },
  created() {
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get("type");
    this.productId = this.$route.params.id;
    this.getData();
    this.fetchRelatedOutfits(); // 新增：调用获取相关穿搭数据
  }
}
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 主要信息区域样式 */
.product-main {
  display: grid;
  grid-template-columns: 500px 1fr;
  gap: 40px;
  margin-bottom: 30px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  padding: 30px;
}

/* 左侧图片区域样式 */
.product-gallery {
  display: flex;
  gap: 20px;
}

.main-image {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.thumbnail-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 右侧信息区域样式 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 详情和评价区域样式 */
.product-detail-tabs {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  margin-top: 20px;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.gallery-image:hover {
  transform: scale(1.05);
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.thumbnail-item:hover {
  border-color: #f3c3cc;
}

.thumbnail-item.active {
  border-color: #f3c3cc;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name {
  font-size: 24px;
  margin: 0 0 20px;
}

.product-meta {
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
}

.price-section {
  margin-bottom: 15px;
}

.price-label {
  font-size: 14px;
  color: #666;
}

.price {
  font-size: 28px;
  color: #F56C6C;
  margin: 0 10px;
  font-weight: bold;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 16px;
}

.sales-info {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.sales-info span {
  display: flex;
  align-items: center;
}

.product-options {
  margin: 5px 0;
}

.option-group {
  margin-bottom: 20px;
}

.option-label {
  margin-bottom: 10px;
  color: #666;
}

.option-values {
  display: flex;
  align-items: center;
  gap: 10px;
}

.stock-info {
  margin-left: 10px;
  color: #666;
}

.purchase-actions {
  margin: 20px 0;
  display: flex;
  gap: 15px;
}

.service-promises {
  display: flex;
  gap: 30px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.promise-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
}

.promise-item i {
  color: #67C23A;
}

.details-content {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 30px;
}

.detail-section h3 {
  margin-bottom: 15px;
}

.parameters-list {
  list-style: none;
  padding: 0;
}

.parameters-list li {
  margin-bottom: 10px;
  display: flex;
}

.param-label {
  width: 100px;
  color: #666;
}

.detail-images img {
  width: 100%;
  margin-bottom: 20px;
}

.reviews-section {
  padding: 20px 0;
}

.reviews-summary {
  display: flex;
  align-items: center;
  gap: 40px;
  margin-bottom: 30px;
}

.rating {
  text-align: center;
}

.rating-score {
  font-size: 36px;
  color: #F56C6C;
}

.rating-stars {
  color: #FFBA00;
}

.rating-stars i.active {
  color: #FFBA00;
}

.rating-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.review-rating {
  margin-left: auto;
  color: #FFBA00;
}

.review-content {
  margin: 10px 0;
  line-height: 1.6;
}

.review-images {
  display: flex;
  gap: 10px;
  margin: 10px 0;
}

.review-images img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.review-footer {
  color: #999;
  font-size: 12px;
}

.review-time {
  margin-right: 20px;
}

/* 响应式布局 */
@media screen and (max-width: 1200px) {
  .product-main {
    grid-template-columns: 1fr;
  }

  .product-gallery {
    justify-content: center;
  }
}

/* 添加购买对话框相关样式 */
.buy-dialog-content {
  padding: 10px 0;
}

.dialog-product-info {
  display: flex;
  gap: 15px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.dialog-product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.dialog-product-detail {
  flex: 1;
}

.dialog-product-detail h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #303133;
}

.dialog-product-price {
  color: #F56C6C;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.dialog-product-quantity {
  color: #606266;
  font-size: 14px;
}

.remark-input {
  margin-bottom: 20px;
}

.order-amount {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.amount {
  color: #F56C6C;
  font-size: 20px;
  font-weight: bold;
  margin-left: 10px;
}

/* 新增：相关帖子模块样式 */
.related-outfits-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.related-outfits-section h3 {
  margin-bottom: 15px;
  font-size: 18px;
  color: #333;
}

.outfits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.outfit-card {
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
}

.outfit-card:hover {
  transform: translateY(-5px);
}

.outfit-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.outfit-info {
  padding: 10px;
}

.outfit-info h4 {
  margin: 0 0 5px;
  font-size: 14px;
  color: #333;
}

.outfit-info .views {
  font-size: 12px;
  color: #999;
}
</style> 