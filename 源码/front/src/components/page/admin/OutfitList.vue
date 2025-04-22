<template>
  <div>
    <div class="container">
      <div class="handle-box">
         <el-input v-model="query.name" placeholder="请选择名称" class="handle-input mr10"></el-input>
         <el-button type="primary" icon="el-icon-search" @click="getData">查询</el-button>
         <el-button type="primary" icon="el-icon-plus" @click="handleAdd" v-if="type=='02'">添加</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="image" label="图片" align="center">
            <template slot-scope="scope">
                <img style="width: 60px;" :src="scope.row.image" />
            </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" align="center"></el-table-column>
        <el-table-column prop="season" label="季节" align="center"> </el-table-column>
        <el-table-column prop="name" label="名称" align="center">
            <template slot-scope="scope">
                <router-link :to="{path:'/user/outfit/'+scope.row.id }">
                    <span style="color: #1e8eee;">{{scope.row.name}}</span>
                </router-link>
            </template>
        </el-table-column>
        <el-table-column prop="realname" label="用户" align="center"></el-table-column>
        <el-table-column prop="num" label="浏览量" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" align="center">
            <template slot-scope="scope">
                <el-button  type="success" size="mini" v-if="scope.row.status == '已发布'">已发布</el-button>
                <el-button  type="warning" size="mini" v-if="scope.row.status == '已撤回'">已撤回</el-button>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" align="center"></el-table-column>
        <el-table-column label="操作" align="center" width="260" >
          <template slot-scope="scope">
            <el-button  type="primary" icon="el-icon-edit" size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
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

    <!-- 引用公共组件 -->
    <outfit-form-dialog
      :dialog-title="dialogName"
      :dialog-visible="dialogVisible"
      :form-data="form"
      @submit="handleSubmit"
      @update:dialog-visible="dialogVisible = $event"
    ></outfit-form-dialog>

    <!-- 商品选择弹窗 -->
    <el-dialog title="选择商品" :visible.sync="showProductDialog" width="80%">
        <div class="products-container">
            <div class="products-grid">
                <div v-for="product in paginatedProducts" :key="product.id" class="product-card" @click="toggleProductSelection(product.id)">
                    <div class="product-image">
                        <img :src="product.image" :alt="product.name" style="width: 80px; height: 80px; object-fit: cover;">
                    </div>
                    <div class="product-info">
                        <h3 class="product-name">{{ product.name }}</h3>
                        <p class="product-desc">{{ product.remark }}</p>
                        <div class="product-meta">
                            <span class="price">¥{{ product.money }}</span>
                        </div>
                    </div>
                    <div class="product-selection" v-if="selectedProducts.includes(product.id)">
                        <i class="el-icon-check"></i>
                    </div>
                </div>
            </div>
            <el-pagination
                @current-change="handlePageChange"
                :current-page="currentPage"
                :page-size="5"
                layout="prev, pager, next"
                :total="products.length">
            </el-pagination>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="showProductDialog = false">取消</el-button>
            <el-button type="primary" @click="confirmSelection">确定</el-button>
        </div>
    </el-dialog>


  </div>
</template>

<script>
import OutfitFormDialog from '@/components/common/OutfitFormDialog.vue';

export default {
  name: "Outfit",
  components: {
    OutfitFormDialog
  },
  data() {
    return {
      showProductDialog: false,
      products: [],
      selectedProducts: [],
      currentPage: 1,
      query: {},
      dialogName: '',
      tableData: [],
      pagesize: 10,
      totalCount: 0,
      dialogVisible: false,
      form: {
        productIds: '[]'
      },
      type: '',
      userInfo: {}
    };
  },
  created() {
    this.userInfo = this.common.getUserInfo('userInfo');
    this.type = this.common.get('type');
    this.getData();
  },
  methods: {
    handleAdd() {
      this.dialogName = "穿搭信息新增";
      this.form = {
        productIds: '[]',
        status: '已发布',
        num: 0,
        uid: this.userInfo.id
      };
      this.dialogVisible = true;
    },
    handleSubmit(formData) {
      this.$axios.post(formData.id ? '/api/outfit/edit' : '/api/outfit/add', formData).then(res => {
        if (res.data.code == 200) {
          this.$message.success(res.data.msg);
          this.dialogVisible = false;
          this.getData();
        } else {
          this.$message.warning(res.data.msg);
        }
      });
    },
    handlePageChange(page) {
        this.currentPage = page;
    },
    toggleProductSelection(id) {
        const index = this.selectedProducts.indexOf(id);
        if (index === -1) {
            this.selectedProducts.push(id);
        } else {
            this.selectedProducts.splice(index, 1);
        }
    },
    confirmSelection() {
        this.form.productIds = JSON.stringify(this.selectedProducts);
        this.showProductDialog = false;
    },
    fetchProducts() {
        this.$axios.post('/api/goods/frontPage', {
            currentPage: 1,
            pagesize: 100,
        }).then(res => {
            if (res.data && res.data.code === 200) {
                this.products = res.data.data.list || [];
            } else {
                this.$message.error(res.data.msg || '获取商品列表失败');
            }
        }).catch(error => {
            this.$message.error('请求商品列表失败：' + error.message);
        });
    },
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
            name: this.query.name,
            uid: this.type==='02'?this.userInfo.id:'',
            pagesize: this.pagesize,  //每页显示的记录数
            currentPage: this.currentPage, //页码
        };
        this.$axios.post('/api/outfit/selectPage',param).then(res => {
            if(res.data.code == 200){
                this.tableData = res.data.data.list;
                this.totalCount = res.data.data.total;
            } else {
                this.$message.warning(res.data.msg);
            }
        });
    },
    // 编辑操作
    handleEdit(index, row) {
        this.form = JSON.parse(JSON.stringify(row));
        this.form.uid = this.form.uid+'';
        this.dialogVisible = true;   // 打开弹窗
        this.dialogName = "穿搭信息编辑";
    },
    // 删除操作
    handleDelete(index, row) {
        this.$confirm('确定要删除吗？', '提示', {
           type: 'warning'
        }).then(() => {
             this.$axios.get('/api/outfit/deleteById?id='+row.id).then(res => {
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
};
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