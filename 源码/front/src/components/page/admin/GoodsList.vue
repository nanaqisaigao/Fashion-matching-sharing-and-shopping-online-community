<template>
  <div>
    <div class="container">
      <div class="handle-box">
         <el-select class="handle-input mr10" clearable v-model="query.cid" placeholder="请选择">
             <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id"></el-option>
         </el-select>
         <el-input v-model="query.name" placeholder="请选择名称" class="handle-input mr10"></el-input>
         <el-button type="primary" icon="el-icon-search" @click="getData">查询</el-button>
         <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="categoryName" label="分类名称" align="center"></el-table-column>
        <el-table-column prop="image" label="商品图片" align="center">
            <template slot-scope="scope">
                <img style="width: 60px;" :src="scope.row.image" />
            </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" align="center"></el-table-column>
        <el-table-column prop="remark" label="简介" align="center"></el-table-column>
        <el-table-column prop="money" label="价格" align="center"></el-table-column>
        <el-table-column prop="num" label="库存" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" align="center">
            <template slot-scope="scope">
                <el-button  type="success" size="mini" v-if="scope.row.status == '已发布'">已发布</el-button>
                <el-button  type="success" size="mini" v-if="scope.row.status == '已下架'">已下架</el-button>
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

    <!-- 弹出框 -->
    <el-dialog :title="dialogName" :visible.sync="dialogVisible" width="55%">
        <el-form ref="ruleForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类" prop="cid">
            <el-select v-model="form.cid" clearable placeholder="请选择">
                <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
            <el-upload class="avatar-uploader" action="mty" :show-file-list="false" :http-request="httpRequest">
                 <img v-if="imageUrl" :src="imageUrl" class="avatar">
                 <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-input type="hidden" v-model="form.image"></el-input>
        </el-form-item>
        <el-form-item label="名称" prop="name">
            <el-input v-model="form.name" placeholder="名称"></el-input>
        </el-form-item>
        <el-form-item label="简介" prop="remark">
            <el-input v-model="form.remark" placeholder="简介"></el-input>
        </el-form-item>
        <el-form-item label="价格" prop="money">
            <el-input v-model="form.money" placeholder="价格"></el-input>
        </el-form-item>
        <el-form-item label="库存" prop="num">
            <el-input type='number' oninput="if(value<0)value=0" v-model="form.num" placeholder="库存"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" clearable placeholder="状态">
            <el-option label="已发布" value="已发布"></el-option>
            <el-option label="已下架" value="已下架"></el-option>
            </el-select>
        </el-form-item>
         <el-card style="height: 610px;">
            <quill-editor v-model="form.content" ref="myQuillEditor" style="height: 500px;" :options="editorOption"></quill-editor>
         </el-card>
      </el-form>
       <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save('ruleForm')">确 定</el-button>
      </span>
    </el-dialog>


  </div>
</template>
<script>
import { quillEditor,Quill} from 'vue-quill-editor'
import { container, ImageExtend, QuillWatch } from "quill-image-super-solution-module";
Quill.register("modules/ImageExtend", ImageExtend);
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'
import { isIntNumer,isMoney } from '../../../utils/checkForm'
export default {
  name: "Goods",
  components: {
     quillEditor
  },
  data() {
      var validateIntNumber = (rule, value, callback) => {
             if(!value){
                callback();
             } else if (!isIntNumer(value)) {
                callback(new Error("请输入整数"));
             } else {
                callback();
             }
       };
      var validateMoney = (rule, value, callback) => {
             if(!value){
                callback();
             } else if (!isMoney(value)) {
                callback(new Error("请输入正确金额"));
             } else {
                callback();
             }
       };
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
        rules: {
          cid: [
              { required: true, message: '请输入分类', trigger: 'blur' },
          ],
          image: [
              { required: true, message: '请输入商品图片', trigger: 'blur' },
          ],
          name: [
              { required: true, message: '请输入名称', trigger: 'blur' },
          ],
          remark: [
              { required: true, message: '请输入简介', trigger: 'blur' },
          ],
          money: [
              { required: true, message: '请输入价格', trigger: 'blur' },
              { validator: validateMoney, trigger: 'blur'},
          ],
          num: [
              { required: true, message: '请输入库存', trigger: 'blur' },
              { validator: validateIntNumber, trigger: 'blur'},
          ],
          content: [
              { required: true, message: '请输入商品介绍', trigger: 'blur' },
          ],
          status: [
              { required: true, message: '请输入状态', trigger: 'blur' },
          ],
        },
        // 富文本框参数设置开始
        editorOption: {
           modules: {
              ImageExtend: {
                  // 图片参数名
                  name: "file",
                  action: "/api/file/imgUpload",
                  accept: "image/jpg, image/png, image/gif, image/jpeg, image/bmp, image/x-icon",
                  response: (res) => {
                     return res.url;
                  }
              },
              toolbar: {
                  container: container,
                  handlers: {
                      image: function() {
                           QuillWatch.emit(this.quill.id);
                      },
                   },
               },
           },
         },
        categoryList:[],
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
      this.querycategory()
  },
  methods: {
    querycategory(){
        this.categoryList = [];
        this.$axios.post('/api/category/queryAll',{}).then(res => {
            for(var i in res.data.data){
               this.categoryList.push({id:res.data.data[i].id+'',name:res.data.data[i].name});
            }
         });
     },
    //实现图片上传功能
    httpRequest(item) {
        const isJPG = item.file.type == 'image/jpeg' || item.file.type == 'image/png' || item.file.type == 'image/jpg';
        const isLt2M = item.file.size / 1024 / 1024 < 2;
        if (!isJPG) {
             this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
        }
        if (!isLt2M) {
             this.$message.error('上传图片大小不能超过 2MB!');
        }
        //图片格式大小信息没问题 执行上传图片的方法
        if (isJPG && isLt2M == true) {
            let App = this;
            let mf = new FormData();
            mf.append('file', item.file);
            this.$axios.post('/api/file/imgUpload',mf).then(res => {
                  if (res.data.result == "true") {
                      this.$message.success({
                          title: '温馨提示：',
                          message: res.data.message,
                      });
                      //将临时文件路径赋值给显示图片路径（前端显示的图片）
                      App.imageUrl =res.data.imgUrl;
                      //将后台传来的数据库图片路径赋值给对象的图片路径
                      App.form.image = res.data.imgUrl;
                  } else {
                      this.$message.error({title: '温馨提示：',message: res.data.message});
                  }
              });
          }
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
            cid: this.query.cid, 
            name: this.query.name, 
            pagesize: this.pagesize,  //每页显示的记录数
            currentPage: this.currentPage, //页码
        };
        this.$axios.post('/api/goods/selectPage',param).then(res => {
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
        this.imageUrl = row.image;
        this.querycategory();
        this.form.cid = this.form.cid+'';
        this.dialogVisible = true;   // 打开弹窗
        this.$refs['ruleForm'].clearValidate();
        this.dialogName = "商品编辑";
    },
    // 新增操作
    handleAdd() {
        if (this.$refs.rulform !== undefined) this.$refs.rulform.resetFields();
        this.dialogVisible = true;   // 打开弹窗
        this.imageUrl = '';
        this.querycategory();
        this.form = {};
        this.form.status='已发布';
        this.$refs['ruleForm'].clearValidate();
        this.dialogName = "商品新增";
    },
    // 保存操作，更新或新增调用
    save(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$axios.post(this.form.id?'/api/goods/edit' : '/api/goods/add', this.form).then(res => {
              if(res.data.code == 200){  // 表示成功
                this.$message.success(res.data.msg);
                this.dialogVisible = false;
                this.getData();
              } else {
                this.$message.warning(res.data.msg);  //错误信息
              }
            })
          } else {
            console.log('error submit!!');
            return false;
          }
        });
    },
    // 删除操作
    handleDelete(index, row) {
        this.$confirm('确定要删除吗？', '提示', {
           type: 'warning'
        }).then(() => {
             this.$axios.get('/api/goods/deleteById?id='+row.id).then(res => {
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