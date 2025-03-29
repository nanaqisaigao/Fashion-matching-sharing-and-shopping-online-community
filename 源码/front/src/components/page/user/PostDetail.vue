<template>
  <div class="post-detail-container">
    <!-- 帖子主体内容 -->
    <div class="post-main">
      <!-- 作者信息 -->
      <div class="author-section">
        <div class="author-info">
          <img :src="post.userImage" :alt="post.realname" class="author-avatar">
          <div class="author-meta">
            <span class="author-name">{{ post.realname }}</span>
            <span class="post-time">{{ post.createTime }}</span>
          </div>
        </div>
      </div>

      <!-- 帖子内容 -->
      <div class="post-content">
        <h1 class="post-title">{{ post.title }}</h1>
        <p class="post-text" v-html="post.content"></p>
      </div>

      <!-- 互动信息 -->
      <div class="interaction-bar">
        <div class="interaction-item" v-if="!post.liked" @click="toggleLike">
          <i :class="[post.liked ? 'el-icon-star-on' : 'el-icon-star-off', { 'liked': post.liked }]"></i>
          <span>{{ post.likes }} 赞</span>
        </div>
        <div class="interaction-item" v-else >
          <i :class="[post.liked ? 'el-icon-star-on' : 'el-icon-star-off', { 'liked': post.liked }]"></i>
          <span>{{ post.likes }} 赞</span>
        </div>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <h3>评论 {{ post.comments }}</h3>
      
      <!-- 评论输入框 -->
      <div class="comment-input">
        <el-input
          type="textarea"
          :rows="3"
          placeholder="写下你的评论..."
          v-model="newComment"
          :maxlength="500"
          show-word-limit>
        </el-input>
        <div class="input-actions">
          <el-button type="primary" @click="submitComment" :disabled="!newComment.trim()">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-for="comment in commentList" :key="comment.id" class="comment-item">
          <div class="comment-user">
            <img :src="comment.userImage" :alt="comment.realname" class="comment-avatar">
            <div class="comment-info">
              <div class="comment-header">
                <span class="user-name">{{ comment.realname }}</span>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
              <div class="comment-actions" v-if="comment.reply">
                <span @click="replyComment(comment)">回复：{{ comment.reply }}</span>
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
  name: 'PostDetail',
  data() {
    return {
      post: {},
      newComment: '',
      commentList: [],
      hasMore: true,
      postId:'',
      type:'',
      userInfo:{},
    }
  },
  methods: {
     // 添加获取详情数据的方法
     getDetail() {
        let uid = ''
        if(this.userInfo){
            uid = this.userInfo.id
        }
        this.$axios.get('/api/forum/frontOne?id='+this.postId+"&uid="+uid).then(res => {
            this.post = res.data.data;
            this.loadMessage();
             // 如果内容中包含图片，添加样式
             if(this.post.content) {
              this.post.content = this.post.content.replace(/<img/g, 
                '<img style="max-width: 600px; height: auto; max-height: 500px; object-fit: contain; margin: 10px 0; border-radius: 4px;"'
              );
            }
        });
    },
    loadMessage() {
        var param = {
            fid: this.postId,
        };
        this.$axios.post('/api/comment/frontAll',param).then(res => {
            this.commentList = res.data.data;
        });
    },
    toggleLike() {
      if(this.type!=='02'){
            this.$message.warning("仅用户可以收藏");
            return;
        }
        if(this.userInfo!=null){
            var param = {
                fid: this.postId,
                uid: this.userInfo.id
            };
            this.$axios.post('/api/stars/add', param).then(res => {
                if(res.data.code == '200'){
                    this.$message.success("收藏成功");
                    this.getDetail();
                }else{
                    this.$message.warning("收藏失败");
                }
            })
        }else{
            this.$message.warning("请先登录");
        }
    },
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
          fid: this.postId,
          uid: this.userInfo.id,
          content: this.newComment
        }
        this.$axios.post('/api/comment/add', comment).then(res => {
            if(res.data.code == 200){
                this.newComment = ''
                this.$message.success('评论发表成功')
                this.loadMessage();
            }else{
                this.$message.warning(res.data.msg);
            }
        })
    },
  },
  created() {
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get("type");
    this.postId = this.$route.params.id
    this.getDetail();
  }
}
</script>

<style scoped>
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.post-main {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.author-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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
  object-fit: cover;
}

.author-meta {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  font-size: 16px;
  color: #303133;
}

.post-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.post-content {
  margin-bottom: 30px;
}

.post-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}

.post-text {
  font-size: 16px;
  line-height: 1.8;
  color: #606266;
  margin-bottom: 20px;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.image-item {
  aspect-ratio: 1;
  overflow: hidden;
  border-radius: 8px;
  cursor: pointer;
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

.interaction-bar {
  display: flex;
  gap: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.interaction-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;
}

.interaction-item:hover {
  color: #f3c3cc;
}

.interaction-item i {
  font-size: 20px;
}

.interaction-item i.liked {
  color: #F56C6C;
}

.comments-section {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.comments-section h3 {
  margin-bottom: 20px;
  font-weight: 500;
}

.comment-input {
  margin-bottom: 30px;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-item {
  margin-bottom: 25px;
}

.comment-user {
  display: flex;
  gap: 15px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-info {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  line-height: 1.6;
  color: #606266;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 13px;
}

.comment-actions span {
  cursor: pointer;
}

.comment-actions span:hover {
  color: #f3c3cc;
}

.replies-list {
  margin-left: 55px;
  margin-top: 15px;
}

.reply-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.reply-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-content {
  flex: 1;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-text {
  color: #606266;
  line-height: 1.5;
}

.reply-to {
  color: #f3c3cc;
  font-weight: 500;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}
</style> 