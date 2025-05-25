<template>
  <div class="box">
    <div class="content">
      <div class="login-wrapper">
        <h1>欢迎登录</h1>
        <el-form :model="user" ref="userForm" class="login-form">
          <div class="username form-item">
            <el-input
              v-model="user.userNo"
              placeholder="请输入用户名"
              class="input-item"
            ></el-input>
          </div>
          <div class="password form-item">
            <el-input
              v-model="user.password"
              placeholder="请输入密码"
              show-password
              class="input-item"
            ></el-input>
          </div>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="login"
          >登 录</el-button>
        </el-form>
      </div>
    </div>
  </div>
</template>


<script>
import {setRoutes} from "@/router";
export default {
  name: "Login",
  data() {
    return {
      user: {
        userNo:"",
        password:""
      },
      loading: false, // 添加 loading 状态
    }
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.loading = true; // 开启 loading 状态
          
          this.request.post("/user/login", this.user).then(res => {
            if (res.code === '200') {
              localStorage.setItem("user", JSON.stringify(res.data))  // 存储用户信息到浏览器
              // 动态设置当前用户的路由
              setRoutes()
              this.$router.push("/")
              this.$message.success("登录成功")
            } else {
              this.$message.error(res.msg)
            }
          }).finally(() => {
            this.loading = false; // 关闭 loading 状态
          });
        }
      });
    }
  }
}
</script>


<style scoped>
/* 背景层 - 增加景深和质感 */
.box .content {
  width: 100vw;
  height: 100vh;
  background: 
    linear-gradient(45deg, rgba(64,158,255,0.1) 0%, rgba(255,255,255,0.8) 100%),
    url('@/assets/bg-login.jpg') no-repeat center/cover;
  position: relative;
  overflow: hidden;
}

/* 背景动态模糊效果 */
.box .content::before {
  content: '';
  position: absolute;
  width: 120%;
  height: 120%;
  background: inherit;
  filter: blur(10px) brightness(1.1);
  animation: bgBreath 12s infinite alternate;
  z-index: 0;
}

@keyframes bgBreath {
  0% { transform: scale(1) rotate(-1deg); }
  100% { transform: scale(1.05) rotate(1deg); }
}

/* 登录框容器 - 玻璃拟态效果 */
.box .content .login-wrapper {
  width: min(90%, 400px);
  position: relative;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 
    0 8px 32px rgba(31, 38, 135, 0.15),
    0 0 16px rgba(255,255,255,0.3) inset;
  transform: translateY(-50%);
  top: 50%;
  margin: 0 auto;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 1;
}

/* 标题美化 */
.box .content .login-wrapper h1 {
  text-align: center;
  font-size: 2.2rem;
  background: linear-gradient(135deg, #409EFF 30%, #66b1ff 70%);
  -webkit-background-clip: text;
  color: transparent;
  margin-bottom: 2.5rem;
  position: relative;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

/* 登录表单容器 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px; /* 增大间距 */
}

/* 输入框样式 */
.input-item >>> .el-input__inner {
  height: 52px;
  border-radius: 16px;
  padding: 0 24px;
  font-size: 16px;
  border: 2px solid #e3eaf3;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255,255,255,0.9);
  box-shadow: 0 2px 6px rgba(64,158,255,0.1);
  margin: 8px 0; /* 垂直间距补充 */
}

.input-item >>> .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 
    0 4px 12px rgba(64,158,255,0.2),
    0 0 0 3px rgba(64,158,255,0.1);
}

/* 按钮容器 */
.login-form > .el-form-item:last-child {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 16px; /* 顶部间距 */
}

/* 按钮样式 */
.login-btn {
  width: 60%; /* 适当宽度 */
  height: 52px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  box-shadow: 
    0 4px 6px rgba(64,158,255,0.2),
    0 1px 3px rgba(0,0,0,0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin: 0 auto; /* 双重居中保障 */
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 8px 12px rgba(64,158,255,0.25),
    0 3px 6px rgba(0,0,0,0.1);
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: 
    0 4px 6px rgba(64,158,255,0.2);
}

/* 响应式处理 */
@media (max-width: 768px) {
  .login-wrapper {
    width: 90%!important;
    padding: 30px!important;
  }
  
  h1 {
    font-size: 1.8rem!important;
  }
  
  .login-btn {
    width: 100%; /* 小屏幕全宽 */
  }
}

/* 其他优化 */
* {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>