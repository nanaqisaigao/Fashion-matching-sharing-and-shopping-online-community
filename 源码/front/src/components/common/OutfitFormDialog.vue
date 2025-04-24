<template>
  <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="55%">
    <el-form ref="form" :model="formData" :rules="rules" label-width="90px">
      <el-form-item label="图片" prop="image">
        <el-upload class="avatar-uploader" action="mty" :show-file-list="false" :http-request="handleUpload">
          <img v-if="imageUrl" :src="imageUrl" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
        <el-input type="hidden" v-model="formData.image"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="formData.type" clearable placeholder="类型">
          <el-option label="休闲" value="休闲"></el-option>
          <el-option label="商务" value="商务"></el-option>
          <el-option label="运动" value="运动"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="季节" prop="season">
        <el-select v-model="formData.season" clearable placeholder="季节">
          <el-option label="春季" value="春季"></el-option>
          <el-option label="夏季" value="夏季"></el-option>
          <el-option label="秋季" value="秋季"></el-option>
          <el-option label="冬季" value="冬季"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="关联商品" prop="productIds">
        <el-button type="primary" @click="showProductDialog = true">选择商品</el-button>
        <div v-if="formData.productIds && formData.productIds !== '[]'" class="selected-products">
          <h4>已选商品:</h4>
          <div class="selected-products-list">
            <div v-for="product in displayProducts" :key="product.id" class="selected-product-item">
              <img :src="product.image" :alt="product.name" class="product-thumbnail">
              <div class="product-detail">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-price">¥{{ product.money }}</div>
              </div>
            </div>
            <div v-if="displayProducts.length === 0" class="no-products">
              加载关联商品中...
            </div>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="浏览量" prop="num">
        <el-input type='number' oninput="if(value<0)value=0" v-model="formData.num" placeholder="浏览量"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="formData.status" clearable placeholder="状态">
          <el-option label="已发布" value="已发布"></el-option>
          <el-option label="已撤回" value="已撤回"></el-option>
        </el-select>
      </el-form-item>
      <el-card style="height: 610px;">
        <quill-editor v-model="formData.content" ref="myQuillEditor" style="height: 500px;" :options="editorOption"></quill-editor>
      </el-card>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </span>

    <!-- 商品选择弹窗 -->
    <el-dialog title="选择商品" :visible.sync="showProductDialog" width="80%" append-to-body>
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
  </el-dialog>
</template>

<script>
import { quillEditor, Quill } from 'vue-quill-editor';
import { container, ImageExtend, QuillWatch } from "quill-image-super-solution-module";
Quill.register("modules/ImageExtend", ImageExtend);
import 'quill/dist/quill.core.css';
import 'quill/dist/quill.snow.css';
import 'quill/dist/quill.bubble.css';
import { isIntNumer } from '@/utils/checkForm';

export default {
  name: 'OutfitFormDialog',
  components: {
    quillEditor
  },
  props: {
    dialogTitle: {
      type: String,
      default: '穿搭信息'
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    formData: {
      type: Object,
      default: () => ({
        productIds: '[]',
        status: '已发布',
        num: 0,
        uid: ''
      })
    }
  },
  data() {
    const validateIntNumber = (rule, value, callback) => {
      if (!value) {
        callback();
      } else if (!isIntNumer(value)) {
        callback(new Error("请输入整数"));
      } else {
        callback();
      }
    };

    return {
      showProductDialog: false,
      products: [],
      selectedProducts: [],
      displayProducts: [],
      currentPage: 1,
      imageUrl: null,
      rules: {
        image: [
          { required: true, message: '请输入图片', trigger: 'blur' },
        ],
        type: [
          { required: true, message: '请输入类型', trigger: 'blur' },
        ],
        season: [
          { required: true, message: '请输入季节', trigger: 'blur' },
        ],
        name: [
          { required: true, message: '请输入名称', trigger: 'blur' },
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' },
        ],
        uid: [
          { required: true, message: '请输入发布人', trigger: 'blur' },
        ],
        num: [
          { required: true, message: '请输入浏览量', trigger: 'blur' },
          { validator: validateIntNumber, trigger: 'blur' },
        ],
        status: [
          { required: true, message: '请输入状态', trigger: 'blur' },
        ],
      },
      editorOption: {
        modules: {
          ImageExtend: {
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
    };
  },
  computed: {
    paginatedProducts() {
      const start = (this.currentPage - 1) * 5;
      const end = start + 5;
      return this.products.slice(start, end);
    }
  },
  methods: {
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
      this.formData.productIds = JSON.stringify(this.selectedProducts);
      this.loadSelectedProductDetails();
      this.showProductDialog = false;
    },
    fetchProducts() {
      this.$axios.post('/api/goods/frontPage', {
        currentPage: 1,
        pagesize: 100,
      }).then(res => {
        if (res.data && res.data.code === 200) {
          this.products = res.data.data.list || [];
          this.loadSelectedProductDetails();
        } else {
          this.$message.error(res.data.msg || '获取商品列表失败');
        }
      }).catch(error => {
        this.$message.error('请求商品列表失败：' + error.message);
      });
    },
    loadSelectedProductDetails() {
      if (this.formData.productIds && this.formData.productIds !== '[]') {
        try {
          const selectedIds = JSON.parse(this.formData.productIds);
          this.selectedProducts = selectedIds;
          
          if (this.products.length > 0) {
            this.displayProducts = this.products.filter(product => 
              selectedIds.includes(product.id)
            );
          } else {
            this.loadProductsByIds(selectedIds);
          }
        } catch (e) {
          console.error('解析已选商品ID失败:', e);
          this.displayProducts = [];
        }
      } else {
        this.displayProducts = [];
        this.selectedProducts = [];
      }
    },
    loadProductsByIds(productIds) {
      if (!productIds || productIds.length === 0) return;
      
      const promises = productIds.map(id => 
        this.$axios.get(`/api/goods/getById?id=${id}`)
      );
      
      Promise.all(promises)
        .then(responses => {
          const validProducts = responses
            .filter(res => res.data && res.data.code === 200)
            .map(res => res.data.data);
          
          this.displayProducts = validProducts;
        })
        .catch(error => {
          console.error('获取商品详情失败:', error);
        });
    },
    handleUpload(item) {
      const isJPG = item.file.type == 'image/jpeg' || item.file.type == 'image/png' || item.file.type == 'image/jpg';
      const isLt2M = item.file.size / 1024 / 1024 < 2;
      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
      }
      if (isJPG && isLt2M) {
        const formData = new FormData();
        formData.append('file', item.file);
        this.$axios.post('/api/file/imgUpload', formData).then(res => {
          if (res.data.result == "true") {
            this.$message.success(res.data.message);
            this.imageUrl = res.data.imgUrl;
            this.formData.image = res.data.imgUrl;
          } else {
            this.$message.error(res.data.message);
          }
        });
      }
    },
    handleSubmit() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.$emit('submit', this.formData);
          this.$emit('update:dialogVisible', false);
        }
      });
    }
  },
  created() {
    this.fetchProducts();
    this.loadSelectedProductDetails();
  },
  watch: {
    'formData.productIds': function(newVal) {
      this.loadSelectedProductDetails();
    },
    dialogVisible: function(newVal) {
      if (newVal) {
        this.loadSelectedProductDetails();
        this.imageUrl = this.formData.image;
      }
    }
  }
};
</script>

<style scoped>
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
.products-container {
  padding: 20px;
}
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}
.product-card {
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s;
}
.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.product-image {
  text-align: center;
}
.product-info {
  margin-top: 10px;
}
.product-name {
  font-size: 14px;
  margin: 0;
}
.product-desc {
  font-size: 12px;
  color: #999;
  margin: 5px 0;
}
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-weight: bold;
  color: #ff6700;
}
.product-selection {
  position: absolute;
  top: 5px;
  right: 5px;
  color: #67c23a;
}

/* 已选商品样式 */
.selected-products {
  margin-top: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}

.selected-products h4 {
  margin: 0 0 10px 0;
  color: #606266;
}

.selected-products-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.selected-product-item {
  display: flex;
  align-items: center;
  width: calc(50% - 15px);
  padding: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  transition: all 0.3s;
}

.selected-product-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.product-thumbnail {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 10px;
}

.product-detail {
  flex: 1;
}

.product-name {
  font-size: 14px;
  margin-bottom: 4px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 14px;
  color: #f56c6c;
  font-weight: bold;
}

.no-products {
  width: 100%;
  padding: 10px;
  color: #909399;
  text-align: center;
  font-style: italic;
}

@media (max-width: 768px) {
  .selected-product-item {
    width: 100%;
  }
}
</style>