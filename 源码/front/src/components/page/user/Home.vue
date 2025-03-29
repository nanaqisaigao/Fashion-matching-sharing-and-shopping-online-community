<template>
  <div class="home-container">
    <!-- 轮播图部分 -->
    <div class="carousel-section">
      <el-carousel height="500px">
        <el-carousel-item v-for="item in carouselItems" :key="item.id">
          <img :src="item.image" :alt="item.title" class="carousel-image">
          <div class="carousel-caption">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 穿搭分享部分 -->
    <div class="section-title">
      <h2>精选穿搭</h2>
      <router-link to="/user/outfit" class="view-more">查看更多</router-link>
    </div>
    <div class="outfits-grid">
      <div v-for="outfit in outfits" :key="outfit.id" class="outfit-card" @click="viewDetail(outfit.id)">
        <img :src="outfit.image" :alt="outfit.name">
        <div class="outfit-info">
          <h3>{{ outfit.name|filtersText1 }}</h3>
          <p>{{ outfit.content|filtersText }}</p>
          <div class="user-info">
            <span>{{ outfit.realname }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品推荐部分 -->
    <div class="section-title">
      <h2>热门商品</h2>
      <router-link to="/user/shop" class="view-more">查看更多</router-link>
    </div>
    <div class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card" @click="viewDetail2(product.id)">
        <img :src="product.image" :alt="product.name">
        <div class="product-info">
          <h3>{{ product.name }}</h3>
          <p class="price">¥{{ product.money }}</p>
          <el-button type="primary" size="small" @click="addToCart(product)">加入购物车</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      carouselItems: [
        {
          id: 1,
          image: require('../../../assets/img/01.jpg'),
          title: '春季新品特惠',
          description: '精选春季穿搭，为你的衣橱增添新色彩'
        },
        {
          id: 2,
          image: require('../../../assets/img/02.jpg'),
          title: '时尚穿搭指南',
          description: '跟随潮流达人，打造专属个性装扮'
        },
        {
          id: 3,
          image: require('../../../assets/img/03.jpg'),
          title: '限时折扣活动',
          description: '多重优惠等你来'
        }
      ],
      outfits: [],
      products: []
    }
  },
  filters: {
    filtersText(val) {
      if (val != null && val != '') {
        let reg = /[\u4e00-\u9fa5]|[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/g;
        let names = val.match(reg);
        val = names ? names.join('') : '';
        return val.length > 30 ? val.substring(0, 30) + '...' : val;
      } else return '';
    },
    filtersText1(val) {
      if (val != null && val != '') {
        let reg = /[\u4e00-\u9fa5]|[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/g;
        let names = val.match(reg);
        val = names ? names.join('') : '';
        return val.length > 18 ? val.substring(0, 18) + '...' : val;
      } else return '';
    },
    },
  created() {
    this.getData();
    this.getGoods();
  },
  methods: {
    addToCart(product) {
      // 添加到购物车的逻辑
      this.$message.success('已添加到购物车')
    },
    //数据来源
    getData() {
        var param = {
            pagesize: 3, 
            currentPage: 1, 
            status:'已发布'
        };
        this.$axios.post('/api/outfit/frontPage',param).then(res => {
            if(res.data.code == 200){
                this.outfits = res.data.data.list;
            } else {
                this.$message.warning(res.data.msg);
            }
        });
    },
    getGoods() {
        var param = {
            pagesize: 4, 
            currentPage: 1, 
            status:'已发布'
        };
        this.$axios.post('/api/goods/frontPage',param).then(res => {
            if(res.data.code == 200){
                this.products = res.data.data.list;
            } else {
                this.$message.warning(res.data.msg);
            }
        });
    },
    viewDetail2(id) {
      this.$router.push(`/user/shop/product/${id}`)
    } ,
    viewDetail(id) {
      this.$router.push(`/user/outfit/${id}`)
    }
  }
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.carousel-section {
  margin-bottom: 40px;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 20%;
  left: 10%;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.carousel-caption h2 {
  font-size: 2.5em;
  margin-bottom: 10px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 40px 0 20px;
}

.view-more {
  color: #f3c3cc;
  text-decoration: none;
}

.outfits-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.outfit-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.outfit-card:hover {
  transform: translateY(-5px);
}

.outfit-card img {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.outfit-info {
  padding: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.user-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 15px;
  text-align: center;
}

.price {
  color: #f56c6c;
  font-size: 1.2em;
  margin: 10px 0;
}
</style> 