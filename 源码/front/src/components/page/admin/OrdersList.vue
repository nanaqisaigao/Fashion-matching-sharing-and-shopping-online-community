<template>
  <div>
    <div class="container">
      <div class="handle-box">
         <el-input v-model="query.no" placeholder="请选择订单编号" class="handle-input mr10"></el-input>
         <el-button type="primary" icon="el-icon-search" @click="getData">查询</el-button>
      </div>
      <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header" >
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="no" label="订单编号" align="center"></el-table-column>
        <el-table-column prop="num" label="商品数量" align="center">
            <template slot-scope="scope">
                {{scope.row.num}}个
            </template>
        </el-table-column>
        <el-table-column prop="total" label="总价" align="center">
            <template slot-scope="scope">
                {{scope.row.total}}元
            </template>
        </el-table-column>
        <el-table-column prop="realname" label="用户" align="center"></el-table-column>
        <el-table-column prop="phone" label="手机号" align="center"></el-table-column>
        <el-table-column prop="address" label="地址" align="center"></el-table-column>
        <el-table-column prop="remark" label="备注" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" align="center">
            <template slot-scope="scope">
                <el-button  type="primary" size="mini" v-if="scope.row.status == '01'">待支付</el-button>
                <el-button  type="success" size="mini" v-if="scope.row.status == '02'">待发货</el-button>
                <el-button  type="warning" size="mini" v-if="scope.row.status == '03'">待收货</el-button>
                <el-button  type="danger" size="mini" v-if="scope.row.status == '04'">已完成</el-button>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" align="center" width="170"></el-table-column>
        <el-table-column label="操作" align="center" width="260" >
          <template slot-scope="scope">
            <el-button type="warning" icon="el-icon-check" v-if="type==='02' && scope.row.status === '01'" @click="shenpi(scope.row,'02')">支付</el-button>
            <el-button type="warning" icon="el-icon-upload2" v-if="type==='01' && scope.row.status === '02'" @click="shenpi(scope.row,'03')">发货</el-button>
            <el-button type="warning" icon="el-icon-download" v-if="type==='02' && scope.row.status === '03'" @click="shenpi(scope.row,'04')">收货</el-button>
            <el-button type="primary" icon="el-icon-zoom-in" @click="detail(scope.$index, scope.row)">详情</el-button>
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


    <el-dialog :title="dialogName" :visible.sync="dialogVisible" width="35%">
        <el-form ref="ruleForm" :model="form" label-width="90px">
        <el-form-item label="评论内容" prop="content">
            <el-input v-model="form.content" type="textarea" rows="3" placeholder="评论内容"></el-input>
        </el-form-item>
      </el-form>
       <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save('ruleForm')">确 定</el-button>
      </span>
    </el-dialog>



    <el-dialog title="订单详情" :visible.sync="dialogTableVisible">
        <el-table :data="orderItemData">
            <el-table-column property="gname" label="商品名称">
                <template slot-scope="scope">
                    <router-link :to="{path:'/user/shop/product/'+scope.row.gid }">
                        <span style="color: #1e8eee;">{{scope.row.goodsName}}</span>
                    </router-link>
                </template>
            </el-table-column>
            <el-table-column property="num" label="数量" ></el-table-column>
            <el-table-column property="money" label="价格(元)" ></el-table-column>
            <el-table-column label="操作" align="center" width="260" >
            <template slot-scope="scope">
                <el-button type="warning" icon="el-icon-s-promotion" v-if="type==='02' && scope.row.status === '04'" @click="comment(scope.row)">评论</el-button>
            </template>
            </el-table-column>
        </el-table>
    </el-dialog>

    <!-- 支付弹窗 -->
    <el-dialog title="订单支付" :visible.sync="paymentDialogVisible" width="500px" center>
      <div class="payment-dialog">
        <div class="payment-amount">
          <h2>支付金额</h2>
          <div class="amount">¥ {{ paymentOrder.total }}</div>
        </div>
        
        <div class="payment-methods">
          <h3>请选择支付方式</h3>
          <div class="methods-list">
            <div 
              v-for="method in paymentMethods" 
              :key="method.id" 
              class="payment-method-item" 
              :class="{ 'selected': selectedPaymentMethod === method.id }"
              @click="selectPaymentMethod(method.id)"
            >
              <img :src="method.icon" :alt="method.name">
              <span>{{ method.name }}</span>
            </div>
          </div>
        </div>

        <div v-if="showQRCode" class="qrcode-container">
          <h3>请使用{{ getSelectedPaymentMethodName() }}扫码支付</h3>
          <div class="qrcode">
            <img :src="qrCodeImage" alt="支付二维码">
          </div>
          <p class="hint">扫描成功后将自动完成支付</p>
          
          <!-- 模拟支付过程 -->
          <div class="mock-payment">
            <el-button type="primary" @click="mockScanComplete">模拟扫码完成</el-button>
          </div>
        </div>
        
        <div class="payment-actions">
          <el-button @click="paymentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="proceedToPayment" :disabled="!selectedPaymentMethod || showQRCode">
            确认支付
          </el-button>
        </div>
      </div>
    </el-dialog>

  </div>
</template>
<script>
import { isIntNumer,isMoney } from '../../../utils/checkForm'
import alipayIcon from '@/assets/img/支付宝支付.png';
import wechatIcon from '@/assets/img/微信.png';
import unionpayIcon from '@/assets/img/银联.png';



export default {
  name: "Orders",
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
        type:'',
        userInfo:{},
        dialogTableVisible:false,
        orderItemData:[],
        // 支付相关
        paymentDialogVisible: false,
        paymentOrder: {},
        selectedPaymentMethod: null,
        showQRCode: false,
        paymentLoading: false,
        qrCodeImage: 'https://t7.baidu.com/it/u=1819248061,230866778&fm=193&f=GIF',
        paymentMethods: [
          { id: 'alipay', name: '支付宝', icon: alipayIcon },
          { id: 'wechat', name: '微信支付', icon: wechatIcon },
          { id: 'unionpay', name: '银联支付', icon: unionpayIcon }
        ],
        paymentStatus: 'pending'
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
            no: this.query.no, 
            uid: this.type==='02'?this.userInfo.id:'',
            pagesize: this.pagesize,  //每页显示的记录数
            currentPage: this.currentPage, //页码
        };
        this.$axios.post('/api/orders/selectPage',param).then(res => {
            if(res.data.code == 200){
                this.tableData = res.data.data.list;
                this.totalCount = res.data.data.total;
            } else {
                this.$message.warning(res.data.msg);
            }
        });
    },
    detail(index, row){
          var param = {
              currentPage: 1,
              pagesize: 999,
              oid: row.id
          };
          this.$axios.post('/api/orderItem/queryAll',param).then(res => {
              this.orderItemData = res.data.data;
              this.dialogTableVisible = true;
          });
      },
       // 评论
    comment(row) {
        debugger
        this.form = JSON.parse(JSON.stringify(row));
        this.dialogVisible = true;   // 打开弹窗
        this.form.gid = row.gid;
        this.form.uid = this.userInfo.id;
        debugger
        this.dialogName = "商品评论";
    },
    // 保存操作，更新或新增调用
    save(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$axios.post('/api/review/add', this.form).then(res => {
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
             this.$axios.get('/api/orders/deleteById?id='+row.id).then(res => {
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
     //支付
    shenpi(row, status) {
      if (status === '02') {
        // 显示支付弹窗
        this.paymentOrder = row;
        this.paymentDialogVisible = true;
        this.selectedPaymentMethod = null;
        this.showQRCode = false;
        this.paymentStatus = 'pending';
      } else {
        // 其他状态的处理（发货/收货等）
        this.$confirm('是否' + (status === '03' ? '发货' : '收货') + '？', '提示', {
          type: 'warning'
        }).then(() => {
          this.processOrderStatusChange(row, status);
        }).catch(() => {});
      }
    },
    
    // 选择支付方式
    selectPaymentMethod(methodId) {
      this.selectedPaymentMethod = methodId;
    },
    
    // 获取选中的支付方式名称
    getSelectedPaymentMethodName() {
      const method = this.paymentMethods.find(m => m.id === this.selectedPaymentMethod);
      return method ? method.name : '';
    },
    
    // 进入支付流程
    proceedToPayment() {
      if (!this.selectedPaymentMethod) {
        this.$message.warning('请选择支付方式');
        return;
      }
      
      this.showQRCode = true;
    },
    
    // 模拟扫码完成
    mockScanComplete() {
      this.paymentLoading = true;
      
      // 模拟支付过程
      setTimeout(() => {
        this.paymentLoading = false;
        this.paymentDialogVisible = false;
        
        // 更新订单状态
        this.processOrderStatusChange(this.paymentOrder, '02');
        
        this.$message.success('支付成功');
      }, 1500);
    },
    
    // 处理订单状态变更
    processOrderStatusChange(row, status) {
      this.$axios.post('/api/orders/edit', {"id": row.id, "status": status}).then(res => {
        if (res.data.code === 200) {
          this.getData();
          this.$message.success("操作成功");
        } else {
          this.$message.warning("操作失败");
        }
      });
    }
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

    /* 支付弹窗样式 */
    .payment-dialog {
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    
    .payment-amount {
      text-align: center;
      margin-bottom: 30px;
    }
    
    .payment-amount h2 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 15px;
    }
    
    .payment-amount .amount {
      font-size: 28px;
      color: #f56c6c;
      font-weight: bold;
    }
    
    .payment-methods {
      width: 100%;
      margin-bottom: 30px;
    }
    
    .payment-methods h3 {
      font-size: 16px;
      margin-bottom: 15px;
      color: #606266;
    }
    
    .methods-list {
      display: flex;
      justify-content: space-around;
    }
    
    .payment-method-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 15px;
      border: 1px solid #EBEEF5;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      width: 120px;
    }
    
    .payment-method-item:hover {
      transform: translateY(-3px);
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }
    
    .payment-method-item.selected {
      border-color: #409EFF;
      background-color: #ecf5ff;
    }
    
    .payment-method-item img {
      width: 40px;
      height: 40px;
      margin-bottom: 8px;
    }
    
    .qrcode-container {
      text-align: center;
      margin-bottom: 20px;
    }
    
    .qrcode-container h3 {
      font-size: 16px;
      margin-bottom: 15px;
      color: #606266;
    }
    
    .qrcode {
      width: 200px;
      height: 200px;
      margin: 0 auto 15px;
      border: 1px solid #EBEEF5;
      padding: 5px;
    }
    
    .qrcode img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .hint {
      color: #909399;
      font-size: 14px;
      margin-bottom: 15px;
    }
    
    .mock-payment {
      margin-top: 15px;
    }
    
    .payment-actions {
      display: flex;
      justify-content: center;
      margin-top: 20px;
      width: 100%;
    }
    
    .payment-actions button {
      margin: 0 10px;
    }
</style>