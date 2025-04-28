<template>
  <div class="cart-container">
    <!-- 购物车头部 -->
    <div class="cart-header">
      <h2>我的购物车</h2>
      <div class="cart-summary">
        共 <span class="highlight">{{ totalItems }}</span> 件商品
      </div>
    </div>

    <!-- 购物车主体 -->
    <div class="cart-main" v-if="cartItems.length">
      <!-- 商品列表 -->
      <div class="cart-list">
        <!-- 表头 -->
        <div class="cart-table-header">
          <el-checkbox 
            v-model="selectAll"
            @change="handleSelectAllChange">
            全选
          </el-checkbox>
          <span style="margin-left: 10px;" class="column-product">商品信息</span>
          <span class="column-price">单价</span>
          <span class="column-quantity">数量</span>
          <span class="column-total">小计</span>
          <span class="column-action">操作</span>
        </div>

        <!-- 商品项 -->
        <div v-for="item in cartItems" 
             :key="item.id" 
             class="cart-item"
             :class="{ 'out-of-stock': item.goodsNum === 0 }">
          <el-checkbox 
            v-model="item.selected"
            :disabled="item.goodsNum === 0"
            @change="handleItemSelectChange">
          </el-checkbox>
          
          <div class="product-info">
            <img :src="item.goodsImage" :alt="item.goodsName">
            <div class="product-detail">
              <router-link :to="`/user/shop/product/${item.id}`" class="product-name">
                {{ item.goodsName }}
              </router-link>
              <div class="stock-status" v-if="item.goodsNum < item.num">
                <i class="el-icon-warning"></i> 已售罄
              </div>
              <div class="stock-status" v-else>
                库存：{{item.goodsNum  }}
              </div>
            </div>
          </div>

          <div class="product-price">
            <span class="current-price">¥{{ item.goodsMoney }}</span>
          </div>

          <div class="quantity-control">
            <el-input-number 
              v-model="item.num" 
              :min="1"
              :max="item.goodsNum"
              :disabled="item.goodsNum === 0"
              size="small"
              @change="handleQuantityChange(item)">
            </el-input-number>
          </div>

          <div class="subtotal">
            ¥{{ (item.goodsMoney * item.num).toFixed(2) }}
          </div>

          <div class="item-actions">
            <el-button type="text" class="delete-btn" @click="removeItem(item)">删除</el-button>
          </div>
        </div>
      </div>

      <!-- 购物车底部 -->
      <div class="cart-footer">
        <div class="left-operations">
          <el-checkbox 
            v-model="selectAll"
            @change="handleSelectAllChange">
            全选
          </el-checkbox>
        </div>
        
        <div class="right-settlement">
          <div class="settlement-info">
            <div class="selected-count">
              已选择 <span class="highlight">{{ selectedCount }}</span> 件商品
            </div>
            <div class="total-price">
              合计：<span class="highlight">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="large"
            :disabled="selectedCount === 0"
            @click="checkout">
            结算
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空购物车状态 -->
    <div v-else class="empty-cart">
      <img src="https://img.alicdn.com/tfs/TB1NQ1d4VP7gK0jSZFjXXc5aXXa-280-280.png" 
           alt="空购物车">
      <p>购物车还是空的</p>
      <el-button type="primary" @click="goShopping">去逛逛</el-button>
    </div>

    <!-- 推荐商品 -->
    <div class="recommendations" v-if="recommendations.length">
      <h3>- 为您推荐 -</h3>
      <div class="recommendations-grid">
        <div v-for="item in recommendations" 
             :key="item.id" 
             class="recommendation-item"
             @click="viewProduct(item.id)">
          <img :src="item.image" :alt="item.name">
          <h4>{{ item.name }}</h4>
          <p class="recommendation-price">¥{{ item.money }}</p>
          <el-button type="primary" size="small" @click.stop="addToCart(item)">
            加入购物车
          </el-button>
        </div>
      </div>
    </div>

    <!-- 添加结算对话框 -->
    <el-dialog
      title="确认订单"
      :visible.sync="showCheckoutDialog"
      width="500px"
      :before-close="handleDialogClose">
      <div class="checkout-dialog-content">
        <!-- 商品列表概览 -->
        <div class="checkout-items">
          <div v-for="item in selectedItems" :key="item.id" class="checkout-item">
            <img :src="item.goodsImage" :alt="item.goodsName">
            <div class="item-info">
              <div class="item-name">{{ item.goodsName }}</div>
              <div class="item-meta">
                <span class="item-price">¥{{ item.goodsMoney }}</span>
                <span class="item-quantity">x {{ item.num }}</span>
              </div>
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
          <span class="amount">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleDialogClose">取 消</el-button>
        <el-button type="primary" @click="confirmCheckout" :loading="submitting">
          确认结算
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Cart',
  data() {
    return {
      selectAll: false,
      type:'',
      userInfo:{},
      cartItems: [],
      recommendations: [],
      showCheckoutDialog: false,
      orderRemark: '',
      submitting: false
    }
  },
  computed: {
    totalItems() {
      return this.cartItems.length
    },
    selectedCount() {
      return this.cartItems.filter(item => item.selected && item.goodsNum > 0).length
    },
    totalPrice() {
      return this.cartItems
        .filter(item => item.selected && item.goodsNum > 0)
        .reduce((total, item) => total + item.goodsMoney * item.num, 0)
    },
    selectedItems() {
      return this.cartItems.filter(item => item.selected && item.goodsNum > 0)
    }
  },
  created(){
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get("type");
    this.handleChange();
    this.queryRecommend();
  },
  methods: {
    queryRecommend(){
        this.$axios.post('/api/goods/frontBySales',{}).then(res => {
            this.recommendations = res.data.data;
         });
     },
    handleChange() {
      var param = {
        uid: this.type && this.userInfo && this.type=='02' ? this.userInfo.id : null
      };
      this.$axios.post('/api/cart/frontAll', param).then(res => {
        this.cartItems = res.data.data.map(item => ({
          ...item,
          selected: false
        }))
      })
    },
    handleSelectAllChange(val) {
      this.cartItems.forEach(item => {
        if (item.goodsNum > 0) {
          this.$set(item, 'selected', val)
        }
      })
      this.$nextTick(() => {
        this.handleItemSelectChange()
      })
    },
    handleItemSelectChange() {
      const availableItems = this.cartItems.filter(item =>item.goodsNum > 0)
      this.selectAll = availableItems.length > 0 && availableItems.every(item => item.selected)
    },
    handleQuantityChange(item) {
      console.log('数量更新:', item)
    },
    removeItem(item) {
      this.$confirm('确定将该商品从购物车中删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.get('/api/cart/deleteById?id='+item.id).then(res => {
            this.$message.success('删除成功')
            this.handleChange();
        });
      }).catch(() => {})
    },
    checkout() {
      if (this.selectedCount === 0) {
        this.$message.warning('请先选择商品')
        return
      }
      this.showCheckoutDialog = true
    },
    handleDialogClose() {
      this.showCheckoutDialog = false
      this.orderRemark = ''
    },
    confirmCheckout() {
      const itemIds = this.selectedItems.map(item => item.id).join(',')
      
      this.submitting = true
      this.$axios.post('/api/orderItem/add', {
        gids: itemIds,
        uid: this.userInfo.id,
        remark: this.orderRemark
      }).then(res => {
        if (res.data.code === 200) {
          this.$message.success('下单成功，请前往个人中心“我的订单”进行支付')
          this.showCheckoutDialog = false
          this.orderRemark = ''
          this.handleChange()
        } else {
          this.$message.warning(res.data.msg)
        }
      }).finally(() => {
        this.submitting = false
      })
    },
    goShopping() {
      this.$router.push('/user/shop')
    },
    viewProduct(id) {
      this.$router.push(`/user/shop/product/${id}`)
    },
    addToCart(item) {
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
        if(item.num<1){
            this.$message.warning("库存不足！");
            return;
        }
        this.$axios.post('/api/cart/add', {gid: item.id,uid:this.userInfo.id,num: 1}).then(res => {
            if(res.data.code == 200){  // 表示成功
                this.$message.success("加入购物车成功！");
                this.handleChange();
            }else{
                this.$message.warning("加入购物车失败！");
            }
        });
    }
  }
}
</script>

<style scoped>
.cart-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.cart-header h2 {
  font-weight: 500;
  color: #303133;
}

.cart-summary {
  color: #606266;
}

.highlight {
  color: #F56C6C;
  font-weight: bold;
}

.cart-main {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.cart-table-header {
  display: grid;
  grid-template-columns: 50px 3fr 1fr 1fr 1fr 1fr;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  color: #909399;
}

.cart-item {
  display: grid;
  grid-template-columns: 50px 3fr 1fr 1fr 1fr 1fr;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.cart-item.out-of-stock {
  opacity: 0.6;
}

.product-info {
  display: flex;
  gap: 15px;
}

.product-info img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
}

.product-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  color: #303133;
  text-decoration: none;
  font-weight: 500;
}

.product-name:hover {
  color: #f3c3cc;
}

.product-spec {
  font-size: 12px;
  color: #909399;
}

.spec-item {
  margin-right: 15px;
}

.stock-status {
  color: #F56C6C;
  font-size: 12px;
}

.product-price {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.current-price {
  color: #F56C6C;
  font-weight: bold;
}

.original-price {
  color: #909399;
  text-decoration: line-through;
  font-size: 12px;
}

.quantity-control {
  width: 120px;
}

.subtotal {
  color: #F56C6C;
  font-weight: bold;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.delete-btn {
  color: #F56C6C;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
}

.left-operations {
  display: flex;
  align-items: center;
  gap: 20px;
}

.right-settlement {
  display: flex;
  align-items: center;
  gap: 20px;
}

.settlement-info {
  text-align: right;
}

.empty-cart {
  text-align: center;
  padding: 50px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.empty-cart img {
  width: 180px;
  margin-bottom: 20px;
}

.empty-cart p {
  color: #909399;
  margin-bottom: 20px;
}

.recommendations {
  margin-top: 40px;
}

.recommendations h3 {
  text-align: center;
  color: #606266;
  margin-bottom: 20px;
}

.recommendations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.recommendation-item {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.recommendation-item:hover {
  transform: translateY(-5px);
}

.recommendation-item img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 10px;
}

.recommendation-item h4 {
  margin: 10px 0;
  color: #303133;
}

.recommendation-price {
  color: #F56C6C;
  font-weight: bold;
  margin-bottom: 10px;
}

/* 结算对话框样式 */
.checkout-dialog-content {
  padding: 10px 0;
}

.checkout-items {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 20px;
  padding-right: 10px;
}

.checkout-item {
  display: flex;
  gap: 15px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.checkout-item:last-child {
  border-bottom: none;
}

.checkout-item img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 14px;
  color: #303133;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #606266;
}

.item-price {
  color: #F56C6C;
  font-weight: bold;
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

/* 滚动条样式优化 */
.checkout-items::-webkit-scrollbar {
  width: 6px;
}

.checkout-items::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.checkout-items::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

.checkout-items::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style> 