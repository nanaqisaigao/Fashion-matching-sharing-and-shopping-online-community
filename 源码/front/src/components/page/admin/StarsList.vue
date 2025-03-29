<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-input v-model="query.forumTitle" placeholder="请输入标题" class="handle-input mr10"></el-input>
         <el-button type="primary" icon="el-icon-search" @click="getData">查询</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="forumTitle" label="标题" align="center">
          <template slot-scope="scope">
                <router-link :to="{path:'/user/forum/post/'+scope.row.fid }">
                    <span style="color: #1e8eee;">{{scope.row.forumTitle}}</span>
                </router-link>
            </template>
        </el-table-column>
        <el-table-column prop="realname" label="用户" align="center"></el-table-column>
        <el-table-column prop="createTime" label="收藏时间" align="center"></el-table-column>
        <el-table-column label="操作" align="center" width="260" >
          <template slot-scope="scope">
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div align="center">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="pagesize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalCount">
        </el-pagination>
      </div>
    </div>


  </div>
</template>
<script>
export default {
  name: "Stars",
  data() {
    return {
        query: {},
        dialogName: '', 
        tableData: [],    // 所有的数据
        currentPage: 1,   // 当前的页码
        pagesize: 10,     // 每页显示的个数
        totalCount: 0,    // 总条数
        imageUrl: null,
        fileUrl: null,
        dialogVisible: false,
        form: {},
        type:'',
        userInfo:{},
    };
  },
  created() {
      this.type = this.common.get('type');
      this.userInfo = this.common.getUserInfo('userInfo');
      if(this.userInfo===null){
         this.$router.push('/');
      }
      this.getData();
  },
  methods: {
    //每页显示数据量变更
    handleSizeChange: function(val) {
        this.pagesize = val;
        this.getData();
    },
    //页码变更
    handleCurrentChange: function(val) {
        this.currentPage = val;
        this.getData();
    },
    //数据来源
    getData() {
        var param = {
            forumTitle: this.query.forumTitle,
            uid: this.type==='02'?this.userInfo.id:'',
            pagesize: this.pagesize,  //每页显示的记录数
            currentPage: this.currentPage, //页码
        };
        this.$axios.post('/api/stars/selectPage',param).then(res => {
            if(res.data.code == 200){
                this.tableData = res.data.data.list;
                this.totalCount = res.data.data.total;
            } else {
                this.$message.warning(res.data.msg);
            }
        });
    },
    // 删除操作
    handleDelete(index, row) {
        this.$confirm('确定要删除吗？', '提示', {
           type: 'warning'
        }).then(() => {
             this.$axios.get('/api/stars/deleteById?id='+row.id).then(res => {
                if(res.data.code == 200){// 操作成功
                    this.$message.success(res.data.msg);
                    this.tableData.splice(index, 1);
                    this.getData();
            }else{
              this.$message.warning(res.data.msg);
            }
          });
        }).catch(() => {
        })
    },
  }
}
</script>

<style scoped>
    .handle-box {
         margin-bottom: 20px;
     }
    .handle-input {
         width: 300px;
         display: inline-block;
     }
    .table {
         width: 100%;
         font-size: 14px;
     }
    .mr10 {
         margin-right: 10px;
     }
    .table-td-thumb {
         display: block;
         margin: auto;
         width: 40px;
         height: 40px;
     }
    .avatar-uploader .el-upload {
         border: 1px dashed #d9d9d9;
         border-radius: 6px;
         cursor: pointer;
         position: relative;
         overflow: hidden;
     }
    .avatar-uploader .el-upload:hover {
         border-color: #eeab1e;
     }
    .avatar-uploader-icon {
         font-size: 28px;
         color: #8c939d;
         width: 178px;
         height: 178px;
         line-height: 178px;
         text-align: center;
     }
    .avatar {
         width: 100% !important;
         height: 178px;
         display: block;
     }
    .video-js .vjs-icon-placeholder {
         width: 80%;
         height: 80%;
         display: block;
     }
    ::v-deep .el-upload--text{
         width: 100px !important;
         height: 100px !important;
     }
    .avatar-uploader-icon {
         font-size: 28px;
         color: #8c939d;
         width: 100px;
         height: 100px;
         line-height: 100px;
         text-align: center;
     }
    .avatar {
         width: 100px !important;
         height: 100px;
         display: block;
     }

</style>