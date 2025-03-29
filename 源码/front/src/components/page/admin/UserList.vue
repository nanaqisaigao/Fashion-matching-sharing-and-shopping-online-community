<template>
  <div>
    <div class="container">
      <div class="handle-box">
         <el-input v-model="query.phone" placeholder="请选择手机号" class="handle-input mr10"></el-input>
         <el-input v-model="query.realname" placeholder="请选择姓名" class="handle-input mr10"></el-input>
         <el-button type="primary" icon="el-icon-search" @click="getData">查询</el-button>
         <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="image" label="头像" align="center">
            <template slot-scope="scope">
                <img style="width: 60px;" :src="scope.row.image" />
            </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" align="center"></el-table-column>
        <el-table-column prop="password" label="密码" align="center"></el-table-column>
        <el-table-column prop="realname" label="姓名" align="center"></el-table-column>
        <el-table-column prop="sex" label="性别" align="center"> </el-table-column>
        <el-table-column prop="age" label="年龄" align="center"></el-table-column>
        <el-table-column prop="address" label="地址" align="center"></el-table-column>
        <el-table-column prop="createTime" label="注册时间" align="center"></el-table-column>
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
    <el-dialog :title="dialogName" :visible.sync="dialogVisible" width="35%">
        <el-form ref="ruleForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="头像" prop="image">
            <el-upload class="avatar-uploader" action="mty" :show-file-list="false" :http-request="httpRequest">
                 <img v-if="imageUrl" :src="imageUrl" class="avatar">
                 <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <el-input type="hidden" v-model="form.image"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="手机号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" placeholder="密码"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="realname">
            <el-input v-model="form.realname" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
            <el-select v-model="form.sex" clearable placeholder="性别">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
            </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
            <el-input type='number' oninput="if(value<0)value=0" v-model="form.age" placeholder="年龄"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
            <el-input type="textarea" rows="5" v-model="form.address" placeholder="地址"></el-input>
        </el-form-item>
      </el-form>
       <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save('ruleForm')">确 定</el-button>
      </span>
    </el-dialog>


  </div>
</template>
<script>
import { isMobile,isIntNumer } from '../../../utils/checkForm'
export default {
  name: "User",
  data() {
      var validateMobile = (rule, value, callback) => {
             if(!value){
                callback();
             } else if (!isMobile(value)) {
                callback(new Error("请输入正确的手机号码"));
             } else {
                callback();
             }
       };
      var validateIntNumber = (rule, value, callback) => {
             if(!value){
                callback();
             } else if (!isIntNumer(value)) {
                callback(new Error("请输入整数"));
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
          image: [
              { required: true, message: '请输入头像', trigger: 'blur' },
          ],
          phone: [
              { required: true, message: '请输入手机号', trigger: 'blur' },
              { validator: validateMobile, trigger: 'blur'},
          ],
          password: [
              { required: true, message: '请输入密码', trigger: 'blur' },
          ],
          realname: [
              { required: true, message: '请输入姓名', trigger: 'blur' },
          ],
          sex: [
              { required: true, message: '请输入性别', trigger: 'blur' },
          ],
          age: [
              { required: true, message: '请输入年龄', trigger: 'blur' },
              { validator: validateIntNumber, trigger: 'blur'},
          ],
          address: [
              { required: true, message: '请输入地址', trigger: 'blur' },
          ],
        },
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
            phone: this.query.phone, 
            realname: this.query.realname, 
            pagesize: this.pagesize,  //每页显示的记录数
            currentPage: this.currentPage, //页码
        };
        this.$axios.post('/api/user/selectPage',param).then(res => {
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
        this.dialogVisible = true;   // 打开弹窗
        this.$refs['ruleForm'].clearValidate();
        this.dialogName = "用户编辑";
    },
    // 新增操作
    handleAdd() {
        if (this.$refs.rulform !== undefined) this.$refs.rulform.resetFields();
        this.dialogVisible = true;   // 打开弹窗
        this.imageUrl = '';
        this.form = {};
        this.$refs['ruleForm'].clearValidate();
        this.dialogName = "用户新增";
    },
    // 保存操作，更新或新增调用
    save(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$axios.post(this.form.id?'/api/user/edit' : '/api/user/add', this.form).then(res => {
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
             this.$axios.get('/api/user/deleteById?id='+row.id).then(res => {
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