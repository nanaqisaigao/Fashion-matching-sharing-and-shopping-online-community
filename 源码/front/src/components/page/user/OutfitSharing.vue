<template>
  <div class="outfit-container">
    <!-- 筛选栏 -->
    <div class="filter-section">
      <el-select v-model="filter.style" placeholder="风格" size="small" @change="handlePageChange(1)">
        <el-option label="全部" value=""></el-option>
        <el-option label="休闲" value="休闲"></el-option>
        <el-option label="商务" value="商务"></el-option>
        <el-option label="运动" value="运动"></el-option>
      </el-select>
      <el-select v-model="filter.season" placeholder="季节" size="small" @change="handlePageChange(1)">
        <el-option label="全部" value=""></el-option>
        <el-option label="春季" value="春季"></el-option>
        <el-option label="夏季" value="夏季"></el-option>
        <el-option label="秋季" value="秋季"></el-option>
        <el-option label="冬季" value="冬季"></el-option>
      </el-select>
    </div>

    <!-- 穿搭列表 -->
    <div class="outfit-grid">
      <div v-for="outfit in outfits" :key="outfit.id" class="outfit-card" @click="viewDetail(outfit.id)">
        <div class="outfit-image">
          <img :src="outfit.image" :alt="outfit.name">
          <div class="outfit-overlay">
            <span><i class="el-icon-view"></i> {{ outfit.num }}</span>
          </div>
        </div>
        <div class="outfit-info">
          <h3>{{ outfit.name|filtersText1 }}</h3>
          <p class="outfit-desc">{{ outfit.content|filtersText }}</p>
          <div class="outfit-author">
            <img :src="outfit.userImage" :alt="outfit.realname">
            <span>{{ outfit.realname }}</span>
            <span class="outfit-date">{{ outfit.createTime }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :current-page.sync="currentPage"
        @current-change="handlePageChange">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OutfitSharing',
  data() {
    return {
      filter: {
        style: '',
        season: ''
      },
      sortBy: 'newest',
      currentPage: 1,
      outfits: [],
      total:0,
    }
  },
  filters: { 
    filtersText1(val) {
      if (val != null && val != '') {
        let reg = /[\u4e00-\u9fa5]|[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/g;
        let names = val.match(reg);
        val = names ? names.join('') : '';
        return val.length > 12 ? val.substring(0, 12) + '...' : val;
      } else return '';
    },
    filtersText(val) {
      if (val != null && val != '') {
        let reg = /[\u4e00-\u9fa5]|[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/g;
        let names = val.match(reg);
        val = names ? names.join('') : '';
        return val.length > 30 ? val.substring(0, 30) + '...' : val;
      } else return '';
    }
  },
  mounted(){
    this.handlePageChange(1);
  },
  methods: {
    handlePageChange(page) {
      this.currentPage = page
      var param = {
          status:'已发布',
          type: this.filter && this.filter.style ? this.filter.style : '',
          season: this.filter && this.filter.season ? this.filter.season : '',
          currentPage: this.currentPage,
          pagesize: 10,
      };
      this.$axios.post('/api/outfit/frontPage',param).then(res => {
          this.outfits = res.data.data.list;
          this.total = res.data.data.total;
      });
    },
    viewDetail(id) {
      this.$router.push({
        path: `/user/outfit/${id}`,
        query: { timestamp: new Date().getTime() }
      })
    }
  }
}
</script>

<style scoped>
.outfit-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100%;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

.outfit-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.outfit-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s;
}

.outfit-card:hover {
  transform: translateY(-5px);
}

.outfit-image {
  position: relative;
  height: 320px;
}

.outfit-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.outfit-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 10px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  display: flex;
  justify-content: space-between;
}

.outfit-info {
  padding: 15px;
}

.outfit-desc {
  color: #666;
  margin: 8px 0;
  font-size: 14px;
}

.outfit-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}

.outfit-author img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.outfit-date {
  color: #999;
  margin-left: auto;
  font-size: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style> 