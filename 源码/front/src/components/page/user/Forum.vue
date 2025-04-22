
<template>
  <div class="forum-container">
    <!-- 顶部操作区 -->
    <div class="forum-header">
      <div class="header-left">
        <el-tabs v-model="currentTab" @tab-click="handleTabClick">
            <el-tab-pane label="最新" name="newest"></el-tab-pane>
            <el-tab-pane label="热门" name="hot"></el-tab-pane>
          <el-tab-pane label="推荐" name="recommend"></el-tab-pane>
        </el-tabs>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchQuery"
          placeholder="搜索帖子"
          prefix-icon="el-icon-search"
          clearable
          @keyup.enter.native="handleSearch"
          class="search-input">
          <el-button slot="append" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </el-input>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="forum-content">
      <!-- 左侧帖子列表 -->
      <div class="post-list">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <!-- 作者信息 -->
          <div class="post-header">
            <div class="author-info">
              <img :src="post.userImage" :alt="post.realname" class="author-avatar">
              <div class="author-meta">
                <span class="author-name">{{ post.realname }}</span>
                <span class="post-time">{{ post.createTime }}</span>
              </div>
            </div>
            <el-dropdown v-if="post.isAuthor" trigger="click" @command="handleCommand">
              <i class="el-icon-more"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="edit">编辑</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <!-- 帖子内容 -->
          <div class="post-content" @click="viewPostDetail(post.id)">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-text" v-html="post.content"></p>

          </div>

          <!-- 互动栏 -->
          <div class="post-actions">
            <div class="action-item" @click="viewPostDetail(post.id)">
              <i :class="[post.isLiked ? 'el-icon-star-on' : 'el-icon-star-off', { 'liked': post.isLiked }]"></i>
              <span>{{ post.likes }} 赞</span>
            </div>
            <div class="action-item" @click="viewPostDetail(post.id)">
              <i class="el-icon-chat-dot-round"></i>
              <span>{{ post.comments }} 评论</span>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button type="text" @click="loadMore">
            加载更多 <i class="el-icon-arrow-down"></i>
          </el-button>
        </div>
      </div>

      <!-- 右侧信息栏 -->
      <div class="forum-sidebar">

        <!-- 收藏榜 -->
        <div class="sidebar-card star-ranking">
          <h3>收藏榜</h3>
          <div class="ranking-list">
            <div v-for="(post, index) in starRanking" 
                 :key="post.id"
                 class="ranking-item"
                 @click="viewPostDetail(post.id)">
              <span class="ranking-number" :class="{'top-three': index < 3}">{{ index + 1 }}</span>
              <div class="ranking-content">
                <div class="ranking-title">{{ post.title }}</div>
                <div class="ranking-count">{{ post.num }} 收藏</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 评论榜 -->
        <div class="sidebar-card comment-ranking">
          <h3>评论榜</h3>
          <div class="ranking-list">
            <div v-for="(post, index) in commentRanking" 
                 :key="post.id"
                 class="ranking-item"
                 @click="viewPostDetail(post.id)">
              <span class="ranking-number" :class="{'top-three': index < 3}">{{ index + 1 }}</span>
              <div class="ranking-content">
                <div class="ranking-title">{{ post.title }}</div>
                <div class="ranking-count">{{ post.num2 }} 评论</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Forum',
  data() {
    return {
      currentTab: 'newest',
      searchQuery: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      posts: [],
      starRanking: [], // 收藏榜
      commentRanking: [], // 评论榜
      hasMore:false,
      type:'',
      userInfo:{},
    }
  },
  created(){
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get("type");
    this.getData();
    this.getStarRanking();
    this.getCommentRanking();
  },
  methods: {
    getData() {
      if(this.currentTab=='recommend'){
        const param = {
            uid: this.type && this.userInfo && this.type=='02'?this.userInfo.id:null
        };
        this.$axios.post('/api/forum/recommend', param).then(res => {
        if(res.data.code == 200) {
          const posts = res.data.data.map(post => {
            if(post.content) {
              post.content = post.content.replace(/<img/g, 
                '<img style="max-width: 200px; height: auto; max-height: 400px; object-fit: contain; margin: 10px 0; border-radius: 4px;"'
              );
            }
            return post;
          });
          this.posts = posts;
        } else {
          this.$message.warning(res.data.msg);
        }
      });
      }else{
        const param = {
            currentPage: this.currentPage,
            pagesize: this.pageSize,
            title: this.searchQuery,
            sortBy: this.currentTab,
      };
        this.$axios.post('/api/forum/frontPage', param).then(res => {
        if(res.data.code == 200) {
          const posts = res.data.data.list.map(post => {
            if(post.content) {
              post.content = post.content.replace(/<img/g, 
                '<img style="max-width: 200px; height: auto; max-height: 400px; object-fit: contain; margin: 10px 0; border-radius: 4px;"'
              );
            }
            return post;
          });

          if(this.currentPage === 1) {
            this.posts = posts;
          } else {
            this.posts = [...this.posts, ...posts];
          }
          this.total = res.data.data.total;
          this.hasMore = this.posts.length < this.total;
        } else {
          this.$message.warning(res.data.msg);
        }
      });
      }
      
    },
    handleSearch() {
      this.currentPage = 1;
      this.getData();
    },
    handleTabClick(tab) {
      this.currentPage = 1;
      this.searchQuery = '';
      this.getData();
    },
    toggleLike(post) {
      if(this.type!=='02'){
            this.$message.warning("仅用户可以收藏");
            return;
        }
        if(this.userInfo!=null){
            var param = {
                fid: post.id,
                uid: this.userInfo.id
            };
            this.$axios.post('/api/stars/add', param).then(res => {
                if(res.data.code == '200'){
                    this.$message.success("收藏成功");
                    this.getData();
                }else{
                    this.$message.warning("收藏失败");
                }
            })
        }else{
            this.$message.warning("请先登录");
        }
    },
    viewPostDetail(id) {
      this.$router.push(`/user/forum/post/${id}`)
    },
    sharePost(post) {
      this.$message.success('分享功能开发中')
    },
    loadMore() {
      if(this.hasMore) {
        this.currentPage++;
        this.getData();
      }
    },
    viewTopic(topic) {
      console.log('查看话题:', topic.name)
    },
    viewUserProfile(userId) {
      console.log('查看用户:', userId)
    },
    // 获取收藏榜
    getStarRanking() {
      const param = {
        currentPage: 1,
        pagesize: 5,
        sortBy: 'hot'
      };
      this.$axios.post('/api/forum/frontPage', param).then(res => {
        if(res.data.code == 200) {
          this.starRanking = res.data.data.list;
        } else {
          this.$message.warning(res.data.msg);
        }
      });
    },
    // 获取评论榜
    getCommentRanking() {
      const param = {
        currentPage: 1,
        pagesize: 5,
        sortBy: 'comment'
      };
      this.$axios.post('/api/forum/frontPage', param).then(res => {
        if(res.data.code == 200) {
          this.commentRanking = res.data.data.list;
        } else {
          this.$message.warning(res.data.msg);
        }
      });
    }
  }
}
</script>

<style scoped>
.forum-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forum-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
}

.post-list {
  min-height: calc(100vh - 200px);
}

.post-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-meta {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  color: #303133;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.post-content {
  cursor: pointer;
}

.post-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
}

.post-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-images {
  display: grid;
  gap: 8px;
  margin-bottom: 15px;
}

.image-count-1 {
  grid-template-columns: 1fr;
}

.image-count-2, .image-count-4 {
  grid-template-columns: repeat(2, 1fr);
}

.image-count-3, .image-count-5, .image-count-6, .image-count-7, .image-count-8, .image-count-9 {
  grid-template-columns: repeat(3, 1fr);
}

.image-item {
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 4px;
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.image-item img:hover {
  transform: scale(1.05);
}

.post-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.post-actions {
  display: flex;
  gap: 30px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.action-item:hover {
  color: #f3c3cc;
}

.action-item i {
  font-size: 18px;
}

.action-item i.liked {
  color: #F56C6C;
}

.load-more {
  text-align: center;
  margin: 20px 0;
}

/* 右侧边栏样式 */
.forum-sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.sidebar-card h3 {
  margin-bottom: 15px;
  font-size: 16px;
  color: #303133;
}

.guide-content {
  color: #606266;
  margin-bottom: 15px;
}

.guide-content p {
  margin-bottom: 8px;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.topic-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.topic-item:hover {
  background-color: #f5f7fa;
}

.topic-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.topic-name {
  color: #303133;
  font-weight: 500;
}

.topic-count {
  font-size: 12px;
  color: #909399;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-item:hover {
  background-color: #f5f7fa;
}

.user-item img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  color: #303133;
  font-weight: 500;
}

.user-title {
  font-size: 12px;
  color: #909399;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .forum-content {
    grid-template-columns: 1fr;
  }
  
  .forum-sidebar {
    display: none;
  }
}

.header-right {
  display: flex;
  align-items: center;
}

.search-input {
  width: 300px;
}

/* 热门帖子样式 */
.hot-post-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-post-item {
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-post-item:hover {
  background-color: #f5f7fa;
  transform: translateX(5px);
}

.hot-post-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.hot-post-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

/* 排行榜通用样式 */
.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.ranking-item:hover {
  background-color: #f5f7fa;
  transform: translateX(5px);
}

.ranking-number {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #909399;
}

.ranking-number.top-three {
  color: #fff;
  background: #f3c3cc;
  border-radius: 50%;
}

.ranking-content {
  flex: 1;
  overflow: hidden;
}

.ranking-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ranking-count {
  font-size: 12px;
  color: #909399;
}

/* 响应式布局调整 */
@media screen and (max-width: 1200px) {
  .forum-sidebar {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }
}
</style> 