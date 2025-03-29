<template>
  <div class="shop-container">
    <!-- 搜索和筛选区域 -->
    <div class="filter-section">
      <!-- 搜索框区域 -->
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称"
          prefix-icon="el-icon-search"
          @keyup.enter.native="handleSearch"
          class="search-input"
        >
          <el-button slot="append" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </el-input>
        
      </div>

      <!-- 高级筛选区域 -->
      <div class="advanced-filter">
        <div class="filter-row">
          <span class="filter-label">分类：</span>
          <div class="filter-options">
            <el-tag :type="filter.category === '' ? 'primary' : ''"
                   @click="filter.category = ''"
                   class="filter-tag">全部</el-tag>
            <el-tag v-for="item in categories"
                   :key="item.value"
                   :type="filter.category === item.value ? 'primary' : ''"
                   @click="filter.category = item.value"
                   class="filter-tag">
              {{ item.label }}
            </el-tag>
          </div>
        </div>
        
        <div class="filter-row">
          <span class="filter-label">价格：</span>
          <div class="filter-options">
            <el-tag :type="filter.price === '' ? 'primary' : ''"
                   @click="filter.price = ''"
                   class="filter-tag">全部</el-tag>
            <el-tag v-for="item in priceRanges"
                   :key="item.value"
                   :type="filter.price === item.value ? 'primary' : ''"
                   @click="filter.price = item.value"
                   class="filter-tag">
              {{ item.label }}
            </el-tag>
          </div>
        </div>

        <div class="filter-row">
          <span class="filter-label">排序：</span>
          <div class="filter-options">
            <el-radio-group v-model="sortBy" size="small">
              <el-radio-button label="1">默认排序</el-radio-button>
              <el-radio-button label="2">价格从低到高</el-radio-button>
              <el-radio-button label="3">价格从高到低</el-radio-button>
              <el-radio-button label="4">销量优先</el-radio-button>
              <el-radio-button label="5">最新上架</el-radio-button>
            </el-radio-group>
          </div>
        </div>

        <!-- 已选条件展示 -->
        <div class="selected-filters" v-if="hasActiveFilters">
          <span class="filter-label">已选：</span>
          <el-tag v-if="filter.category" 
                 closable 
                 @close="filter.category = ''">
            分类：{{ getCategoryLabel(filter.category) }}
          </el-tag>
          <el-tag v-if="filter.price" 
                 closable 
                 @close="filter.price = ''">
            价格：{{ getPriceLabel(filter.price) }}
          </el-tag>
          <el-button type="text" @click="resetFilters">清空筛选</el-button>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card" @click="viewDetail(product.id)">
        <div class="product-image">
          <img :src="product.image" :alt="product.name">
          <div class="product-tags">
            <span class="tag 新品">{{ product.categoryName }}</span>
          </div>
        </div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-desc">{{ product.remark }}</p>
          <div class="product-meta">
            <span class="price">¥{{ product.money }}</span>
            <span class="sales">已售 {{ product.sale }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :total="total"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        @current-change="handlePageChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Shop',
  data() {
    return {
      searchQuery: '',
      filter: {
        category: '',
        price: ''
      },
      sortBy: '1',
      currentPage: 1,
      pageSize: 12,
      total: 100,
      categories: [],
      priceRanges: [
        { value: '1', label: '¥0-100' },
        { value: '2', label: '¥100-300' },
        { value: '3', label: '¥300-500' },
        { value: '4', label: '¥500-1000' },
        { value: '5', label: '¥1000以上' }
      ],
      products: []
    }
  },
  computed: {
    hasActiveFilters() {
      return this.filter.category || this.filter.price
    }
  },
  created() {
    this.queryCate();
    this.handleSearch();
  },
  methods: {
    queryCate(){
        this.categories = [];
        this.$axios.post('/api/category/frontAll',{}).then(res => {
            for(var i in res.data.data){
               this.categories.push({value:res.data.data[i].id+'',label:res.data.data[i].name});
            }
         });
     },
    handleSearch() {
      var param = {
          name:this.searchQuery,
          status:'已发布',
          cid: this.filter && this.filter.category ? this.filter.category : '',
          money: this.filter && this.filter.price ? this.filter.price : '',
          sortBy:this.sortBy,
          currentPage: this.currentPage,
          pagesize: this.pageSize,
      };
      this.$axios.post('/api/goods/frontPage',param).then(res => {
          this.products = res.data.data.list;
          this.total = res.data.data.total;
      });
    },
    getCategoryLabel(value) {
      const category = this.categories.find(item => item.value === value)
      return category ? category.label : ''
    },
    getPriceLabel(value) {
      const price = this.priceRanges.find(item => item.value === value)
      return price ? price.label : ''
    },
    resetFilters() {
      this.filter.category = ''
      this.filter.price = ''
      this.sortBy = 'default'
      this.handleSearch();
    },
    handlePageChange(page) {
      this.currentPage = page
      // 加载对应页面数据
    },
    viewDetail(id) {
      this.$router.push(`/user/shop/product/${id}`)
    },
  }
}
</script>

<style scoped>
.shop-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.search-box {
  position: relative;
  margin-bottom: 20px;
}

.search-input {
  width: 500px;
}

.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  width: 500px;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  z-index: 1000;
  padding: 12px;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  color: #909399;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.history-item {
  display: inline-flex;
  align-items: center;
  padding: 4px 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
  cursor: pointer;
}

.history-item:hover {
  background: #ecf5ff;
  color: #f3c3cc;
}

.history-item i {
  margin-right: 4px;
  font-size: 12px;
}

.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-item {
  cursor: pointer;
}

.advanced-filter {
  background: #fff;
  padding: 16px;
  border-radius: 4px;
}

.filter-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.filter-label {
  width: 60px;
  color: #606266;
  font-size: 14px;
}

.filter-options {
  flex: 1;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-tag {
  cursor: pointer;
}

.selected-filters {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #dcdfe6;
}

.selected-filters .el-button {
  margin-left: auto;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  position: relative;
  height: 280px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 5px;
}

.tag {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #fff;
}

.tag.新品 {
  background-color: #f3c3cc;
}

.tag.热销 {
  background-color: #F56C6C;
}

.tag.特惠 {
  background-color: #E6A23C;
}

.product-info {
  padding: 15px;
}

.product-name {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
}

.product-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
  height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.price {
  color: #F56C6C;
  font-size: 18px;
  font-weight: bold;
}

.sales {
  color: #999;
  font-size: 12px;
}

.product-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style> 