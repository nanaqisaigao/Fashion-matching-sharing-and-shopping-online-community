<template>
  <div class="login-wrap">
    <div class="ms-login">
      <div class="login-header">
        <h2>Fashion Style</h2>
        <p>欢迎来到时尚穿搭社区</p>
      </div>

      <el-form :model="param" :rules="rules" ref="login" label-width="0px" class="ms-content">
        <el-form-item prop="phone">
          <el-input v-model="param.phone" placeholder="请输入账号">
            <i slot="prefix" class="el-input__icon el-icon-user"></i>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            type="password"
            placeholder="请输入密码"
            v-model="param.password"
            @keyup.enter.native="submitForm()">
            <i slot="prefix" class="el-input__icon el-icon-lock"></i>
          </el-input>
        </el-form-item>
        <el-form-item prop="type">
          <el-select v-model="param.type" placeholder="请选择登录类型" style="width: 100%">
            <el-option label="用户" value="02"></el-option>
            <el-option label="管理员" value="01"></el-option>
          </el-select>
        </el-form-item>
        <div class="login-btn">
          <el-button type="primary" round @click="submitForm()">登 录</el-button>
        </div>
      </el-form>

      <div class="login-footer">
        <router-link to="/" class="footer-link">返回首页</router-link>
        <router-link to="/userregister" class="footer-link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
    import SIdentify from '../../common/admin/SIdentify.vue'

    export default {
        data() {
            return {
                activeName: 'first',
                stretch:true,
                validCode: '',
                param: {
                    phone: '',
                    password: '',
                    type:'01',
                },
                rules: {
                    phone: [{required: true, message: '请输入账号', trigger: 'blur'}],
                    password: [{required: true, message: '请输入密码', trigger: 'blur'}],
                    type: [{required: true, message: '请选择人员类型', trigger: 'blur'}],
                },
            }
        },
        components: {SIdentify},
        methods: {
            submitForm() {
                this.$refs.login.validate(valid => {
                    if (valid) {
                        this.$axios.post('/api/login', this.param).then(res => {
                            if (res.data.code === 200) {
                                this.$message.success(res.data.msg);
                                this.common.set('userInfo',JSON.stringify(res.data.data));
                                this.common.set('token',res.data.data.token);
                                this.common.set('type',this.param.type);
                                this.$router.push('/user/helloHome');
                            } else
                                this.$message.error(res.data.msg);
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
        }
    };
</script>
<style scoped>
.login-wrap {
  position: relative;
  width: 100%;
  height: 100%;
  background-image: linear-gradient(to right, rgba(243, 195, 204, 0.8), rgba(243, 195, 204, 0.4)), url(../../../assets/img/1.jpg);
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ms-login {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
  font-family: 'Playfair Display', serif;
}

.login-header p {
  color: #666;
  font-size: 16px;
}

.ms-content {
  margin: 20px 0;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input {
  height: 40px;
}

.el-input__inner {
  height: 40px;
  line-height: 40px;
  padding-left: 44px !important;
  background: #f5f7fa;
  border: none;
  border-radius: 20px;
}

.el-input__icon {
  line-height: 40px;
  color: #909399;
  font-size: 16px;
  margin-left: 8px;
}

.el-select {
  width: 100%;
}

.login-btn {
  text-align: center;
  margin-top: 30px;
}

.login-btn button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  background: #f3c3cc;
  border: none;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.login-btn button:hover {
  background: #f1b5c1;
  transform: translateY(-2px);
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-link {
  color: #666;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #f3c3cc;
}

/* 覆盖 Element UI 的默认样式 */
:deep(.el-select .el-input__inner) {
  padding-left: 15px !important;
}

:deep(.el-select .el-input.is-focus .el-input__inner) {
  border-color: #f3c3cc;
}

:deep(.el-select-dropdown__item.selected) {
  color: #f3c3cc;
}

:deep(.el-form-item.is-error .el-input__inner) {
  border-color: #F56C6C;
}

:deep(.el-button--primary:focus, .el-button--primary:hover) {
  background: #f1b5c1;
  border-color: #f1b5c1;
}
</style>