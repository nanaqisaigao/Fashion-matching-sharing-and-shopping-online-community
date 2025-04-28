<template>
    <div class="header">
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="collapseChage">
            <i v-if="!collapse" class="el-icon-s-operation"></i>
            <i v-else class="el-icon-s-home"></i>
        </div>
        <div class="logo">时尚穿搭社区</div>
        <div class="header-right">
            <div class="header-user-con">
                <div @click="goIndex" style="font-size: small;margin-right: 25px;cursor: pointer;">
                    返回首页
                </div>
                <el-dropdown class="user-name" trigger="click" @command="handleCommand">
                    <span class="el-dropdown-link">
                        <span v-if="userType=='01'">管理员：{{userInfo.username}}</span>
                        <span v-if="userType=='02'">用户：{{userInfo.realname}}</span>
                        <i class="el-icon-caret-bottom"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item divided command="query" v-if="userType!=='01'">个人资料</el-dropdown-item>
                        <el-dropdown-item divided command="update" v-if="userType!=='01'">修改信息</el-dropdown-item>
                        <el-dropdown-item divided command="password">修改密码</el-dropdown-item>
                        <el-dropdown-item divided command="loginout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </div>
        </div>



        <!-- 查看个人资料-->
        <el-dialog
            title="个人资料"
            :visible.sync="dialogVisible"
            width="40%"
            :before-close="handleClose">
            <el-form ref="ruleForm" :model="form" :rules="rules" label-width="120px">

                <el-form-item label="头像" prop="realname">
                    <img :src="form.image"  class="avatar">
                </el-form-item>
                <el-form-item label="姓名" prop="realname">
                    {{form.realname}}
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    {{form.phone}}
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    {{form.sex}}
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    {{form.age}}
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    {{form.address}}
                </el-form-item>
                <el-form-item label="注册时间" prop="age">
                    {{form.createTime}}
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click="dialogVisible = false">关 闭</el-button>
            </span>
        </el-dialog>


        <el-dialog
            title="修改信息"
            :visible.sync="eformVisible"
            width="40%"
            :before-close="handleClose2">
            <el-form ref="ruleForm" :model="eform" :rules="rules" label-width="120px">
                <el-form-item label="头像" prop="image">
                    <el-upload
                        class="avatar-uploader"
                        action="mty"
                        :show-file-list="false"
                        :http-request="httpRequest">
                        <img v-if="imageUrl" :src="imageUrl" class="avatar">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                    <el-input type="hidden" v-model="eform.image"></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="eform.phone"></el-input>
                </el-form-item>
                <el-form-item label="姓名" prop="realname">
                    <el-input v-model="eform.realname"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    <el-select v-model="eform.sex" placeholder="请选择性别">
                        <el-option label="男" value="男"></el-option>
                        <el-option label="女" value="女"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input v-model="eform.age" type='number' oninput="if(value<0)value=0"></el-input>
                </el-form-item>
                <el-form-item label="地址" prop="address">
                    <el-input v-model="eform.address"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="eformVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit('ruleForm')">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog
            title="修改密码"
            :visible.sync="pVisible"
            width="40%"
            :before-close="handleClose3">
            <el-form ref="ruleForm" :model="pform" :rules="rules" label-width="120px">
                <el-form-item label="原密码" prop="password">
                    <el-input v-model="pform.password"></el-input>
                </el-form-item>
                <el-form-item label="新密码" prop="newpassword">
                    <el-input v-model="pform.newpassword"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="repassword">
                    <el-input v-model="pform.repassword"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="pVisible = false">取 消</el-button>
                <el-button type="primary" @click="updatePad('ruleForm')">确 定</el-button>
            </span>
        </el-dialog>


    </div>
</template>
<script>
    import bus from './bus';
    import common from "../../../utils/common";
    import { isMobile } from '../../../utils/checkForm'
    export default {
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
            return {
                collapse: false,
                fullscreen: false,
                name: '暂未登录',
                userType:'',
                userInfo:{},
                message: 2,
                dialogVisible:false,
                form:{},
                eformVisible:false,
                eform:{},
                pVisible:false,
                pform:{},
                imageUrl:'',
                rules: {
                    phone: [
                        { required: true, message: '请输入手机号', trigger: 'blur' },
                        { validator: validateMobile, trigger: 'blur'},
                    ],
                    realname: [
                        { required: true, message: '请输入姓名', trigger: 'blur' }
                    ],
                    sex: [
                        { required: true, message: '请输入性别', trigger: 'blur' }
                    ],
                    age: [
                        { required: true, message: '请选择年龄', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                        { min: 6, max: 20, message: '密码长度必须介于6和20之间', trigger: 'blur' }
                    ],
                    newpassword: [
                        { required: true, message: '请输入新密码', trigger: 'blur' },
                        { min: 6, max: 20, message: '密码长度必须介于6和20之间', trigger: 'blur' }
                    ],
                    repassword: [
                        { required: true, message: '请输入确认密码', trigger: 'blur' },
                        { min: 6, max: 20, message: '密码长度必须介于6和20之间', trigger: 'blur' }
                    ],
                },
            };
        },
        created() {
            this.userInfo = this.common.getUserInfo('userInfo');
            this.userType = this.common.get('type');
            if(this.userInfo==null){
                this.$message.error('登录失效，请重新登录！');
            }else{
                this.form = this.userInfo;
                this.eform = this.userInfo;
            }
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
                            App.eform.image = res.data.imgUrl;
                        } else {
                            this.$message.error({title: '温馨提示：',message: res.data.message});
                        }
                    });
                }
            },
            handleClose(){
                this.dialogVisible = false;
            },
            handleClose2(){
                this.eformVisible = false;
            },
            handleClose3(){
                this.pVisible = false;
            },
            goIndex(){
                this.$router.push('/user/helloHome');
            },
            // 用户名下拉菜单选择事件
            handleCommand(command) {
                if (command == 'loginout') {
                    localStorage.clear();
                    this.common.cache.clearAll();
                    this.$router.push('/');
                }
                if (command == 'query') {
                    this.dialogVisible = true;
                }
                if (command == 'update') {
                    this.eformVisible = true;
                    this.imageUrl = this.form.image
                }
                if (command == 'password') {
                    if (this.$refs.ruleForm !== undefined) this.$refs.ruleForm.resetFields();
                    this.pVisible = true;
                }
            },
            // 保存编辑
            saveEdit(formName) {
                this.eform.type = this.userType;
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.$axios.post('/api/updateUser', this.eform).then(res => {
                            if(res.data.code == 200){
                                this.$message.success("修改成功");
                                localStorage.removeItem("userInfo");
                                localStorage.removeItem("token");
                                this.common.set('userInfo',JSON.stringify(res.data.data));
                                this.common.set('token',res.data.data.token);
                                
                                // 同时更新内存缓存
                                this.common.cache.set('userInfo', JSON.parse(JSON.stringify(res.data.data)));
                                this.common.cache.set('token', res.data.data.token);
                                
                                this.eformVisible = false;
                            }else{
                                this.$message.warning(res.msg);
                            }
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            // 编辑密码
            updatePad(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        //判端
                        if(this.pform.password != this.userInfo.password){
                            this.$message.warning("原密码错误");
                            return;
                        }
                        if(this.pform.newpassword != this.pform.repassword){
                            this.$message.warning("两次密码不一致");
                            return;
                        }
                        this.pVisible = false;
                        this.pform.id = this.userInfo.id;
                        this.pform.password = this.pform.newpassword;
                        this.pform.type = this.userType;
                        this.$axios.post('/api/updateUser', this.pform).then(res => {
                            if(res.data.code == 200){
                                this.$message.success("修改成功");
                                localStorage.removeItem("userInfo");
                                localStorage.removeItem("token");
                                this.common.set('userInfo',JSON.stringify(res.data.data));
                                this.common.set('token',res.data.data.token);
                            }else{
                                this.$message.warning(res.msg);
                            }
                        })
                    } else {
                        console.log('error submit!!');
                        return false;
                    }
                });
            },
            // 侧边栏折叠
            collapseChage() {
                this.collapse = !this.collapse;
                bus.$emit('collapse', this.collapse);
            },
        },
        mounted() {
            if (document.body.clientWidth < 1500) {
                this.collapseChage();
            }
        }
    };
</script>
<style scoped>
    .header {
        position: relative;
        box-sizing: border-box;
        width: 100%;
        height: 46px;
        font-size: 22px;
        color: #fff;
        background-color: #f3c3cc;
    }

    /* 修复下拉菜单问题 */
    .el-dropdown, .user-name {
        cursor: pointer !important;
        pointer-events: auto !important;
    }
    
    .el-dropdown-link {
        color: #fff;
        cursor: pointer;
        padding: 10px;
        display: inline-block;
        pointer-events: auto !important;
        user-select: none;
    }
    
    /* 确保下拉菜单可点击 */
    ::v-deep .el-dropdown-menu__item {
        cursor: pointer !important;
        pointer-events: auto !important;
    }
    
    .collapse-btn {
        float: left;
        padding: 0 21px;
        cursor: pointer;
        line-height: 50px;
    }

    .header .logo {
        float: left;
        width: 250px;
        line-height: 50px;
        font-size: 16px;
    }

    .header-right {
        float: right;
        padding-right: 50px;
    }

    .header-user-con {
        display: flex;
        height: 48px;
        align-items: center;
    }
    .collapse-btn:hover{
        background: rgb(18 98 219);
    }
    .btn-fullscreen {
        transform: rotate(45deg);
        margin-right: 5px;
        font-size: 24px;
    }

    .btn-bell,
    .btn-fullscreen {
        position: relative;
        width: 30px;
        height: 30px;
        text-align: center;
        border-radius: 15px;
        cursor: pointer;
    }

    .btn-bell-badge {
        position: absolute;
        right: 0;
        top: -2px;
        width: 8px;
        height: 8px;
        border-radius: 4px;
        background: #f56c6c;
        color: #fff;
    }

    .btn-bell .el-icon-bell {
        color: #fff;
    }

    .user-avator {
        margin-left: 20px;
    }

    .user-avator img {
        display: block;
        width: 40px;
        height: 40px;
        border-radius: 50%;
    }

    .avatar-uploader .el-upload {
        border: 1px dashed #d9d9d9;
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
    }
    .avatar-uploader .el-upload:hover {
        border-color: #f3c3cc;
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
