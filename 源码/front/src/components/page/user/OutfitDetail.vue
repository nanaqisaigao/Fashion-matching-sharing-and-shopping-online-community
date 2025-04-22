<template>
  <div class="page-container">
    <div class="outfit-detail-container">
      <!-- 主要内容区 -->
      <div class="main-content">
        <!-- 图片展示区 -->
        <div class="image-section">
          <img :src="outfitDetail.images" :alt="outfitDetail.title" class="outfit-image">
        </div>

        <!-- 作者信息 -->
        <div class="author-section">
          <div class="author-info">
            <img :src="outfitDetail.userImage" :alt="outfitDetail.realname" class="author-avatar">
            <div class="author-text">
              <h3>{{ outfitDetail.realname }}</h3>
              <span class="publish-time">{{ outfitDetail.createTime }}</span>
            </div>
          </div>
        </div>

        <!-- 穿搭详情 -->
        <div class="detail-section">
          <h1 class="outfit-title">{{ outfitDetail.name }}</h1>
          <div class="interaction-bar">
            <span class="views"><i class="el-icon-view"></i> {{ outfitDetail.num }}</span>
            <span class="shares"><i class="el-icon-share"></i> 分享</span>
          </div>
          <div class="outfit-description">
            <p v-html="outfitDetail.content"></p>
          </div>

          <!-- 关联商品展示区 -->
          <div class="items-section" v-if="relatedProducts.length > 0">
            <h3>关联商品</h3>
            <div class="items-grid">
              <div v-for="product in relatedProducts" :key="product.id" class="item-card" @click="$router.push({ name: 'ProductDetail', params: { id: product.id } })">
                <img :src="product.image" :alt="product.name">
                <div class="item-info">
                  <h4>{{ product.name }}</h4>
                  <p class="price">¥{{ product.money }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h3>评论 ({{ outfitDetail.discusses.length }})</h3>
          <!-- 评论输入框 -->
          <div class="comment-input">
            <el-input
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
              v-model="newComment">
            </el-input>
            <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
              发表评论
            </el-button>
          </div>
          <!-- 评论列表 -->
          <div class="comments-list" v-if="outfitDetail.discusses">
            <div v-for="comment in outfitDetail.discusses" :key="comment.id" class="comment-item">
              <img :src="comment.userImage" :alt="comment.realname" class="comment-avatar">
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-user">{{ comment.realname }}</span>
                  <span class="comment-time">{{ comment.createTime }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions" style="margin-left: 20px;">
                  <span v-if="comment.reply!=null">回复：{{ comment.reply }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧推荐区 -->
      <div class="recommendations-section">
        <h3>相关推荐</h3>
        <div class="recommendations-grid">
          <div v-for="item in recommendations" :key="item.id" class="recommendation-card" @click="$router.push({ name: 'OutfitDetail', params: { id: item.id } })">
            <img :src="item.image" :alt="item.name">
            <div class="recommendation-info">
              <h4>{{ item.name }}</h4>
              <p class="views"><i class="el-icon-view"></i> {{ item.num }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OutfitDetail',
  data() {
    return {
      isFollowing: false,
      isLiked: false,
      newComment: '',
      outfitDetail: {},
      recommendations: [],
      relatedProducts: [],
      type: '',
      userInfo: {},
      id: ''
    }
  },
  created() {
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get("type");
    this.id = this.$route.params.id
    this.fetchOutfitDetail();
    this.recomment();
  },
  methods: {
    submitComment() {
        if (!this.newComment.trim()) {
          this.$message.warning('请输入评论内容')
          return
        }
        if (!this.userInfo) {
          this.$message({
            message: "请登录系统",
            type: "warning"
          });
          return;
        }
        if (this.type!=null && this.type!='02') {
          this.$message({
            message: "请登录用户",
            type: "warning"
          });
          return;
        }
        // 提交评论
        const comment = {
          oid: this.id,
          uid: this.userInfo.id,
          content: this.newComment
        }
        this.$axios.post('/api/discuss/add', comment).then(res => {
            if(res.data.code == 200){
                this.newComment = ''
                this.$message.success('评论发表成功')
                this.fetchOutfitDetail();
            }else{
                this.$message.warning(res.data.msg);
            }
        })
    },
    viewOutfit(id) {
      this.$router.push(`/user/outfit/${id}`)
    },
    // 添加获取详情数据的方法
    fetchOutfitDetail() {
        this.$axios.get('/api/outfit/frontOne?id=' + this.id).then(res => {
            this.outfitDetail = res.data.data;
            if (this.outfitDetail.productIds) {
              const productIds = JSON.parse(this.outfitDetail.productIds);
              this.$axios.post('/api/goods/listByIds', { ids: productIds }).then(res => {
                this.relatedProducts = res.data.data || [];
              });
            }
        });
    },
     // 添加获取详情数据的方法
     recomment() {
        this.$axios.post('/api/outfit/recommend',{}).then(res => {
          this.recommendations = res.data.data;
      });
    }
  }
}
</script>

<style scoped>
.page-container {
  width: 100%;
  min-height: 100%;
  padding: 20px 0;
}

.outfit-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.main-content {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.image-section {
  width: 100%;
  max-height: 600px;
  overflow: hidden;
}

.outfit-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-section {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.publish-time {
  color: #999;
  font-size: 14px;
}

.detail-section {
  padding: 20px;
}

.outfit-title {
  margin-bottom: 15px;
  font-size: 24px;
}

.interaction-bar {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  color: #606266;
}

.interaction-bar span {
  cursor: pointer;
}

.interaction-bar i {
  margin-right: 5px;
}

.outfit-description {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 30px;
}

.items-section {
  margin: 30px 0;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 15px;
}

.item-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.item-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.item-info {
  padding: 10px;
  text-align: center;
}

.price {
  color: #f56c6c;
  margin: 5px 0 10px;
}

.comments-section {
  padding: 20px;
}

.comment-input {
  margin: 20px 0;
}

.comment-input .el-button {
  margin-top: 10px;
  float: right;
}

.comment-item {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.comment-content {
  flex: 1;
}

.comment-header {
  margin-bottom: 5px;
}

.comment-user {
  font-weight: bold;
  margin-right: 10px;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-text {
  line-height: 1.5;
  margin-bottom: 8px;
}

.comment-actions {
  color: #999;
  font-size: 14px;
}

.comment-actions span {
  margin-right: 15px;
  cursor: pointer;
}

.comment-actions span:hover {
  color: #f3c3cc;
}

.recommendations-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  position: sticky;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.recommendation-card {
  margin-bottom: 15px;
  cursor: pointer;
}

.recommendation-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.recommendation-info {
  padding: 10px 0;
}

.recommendation-info h4 {
  margin: 0;
  font-size: 14px;
}

.recommendation-info p {
  color: #999;
  font-size: 12px;
}
</style> 