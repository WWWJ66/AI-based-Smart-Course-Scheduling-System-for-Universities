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
.box .content {
  width: 100vw;
  height: 100vh;
  background: url('@/assets/bg-login.jpg') no-repeat;
  background-size: 90% 100%;
  position: absolute;
  right: 15%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: #fff;
}
.box .content .login-wrapper {
  width: 30vw;
  position: absolute;
  right: 15%;
  top: 50%;
  transform: translateY(-50%);
}
.box .content .login-wrapper h1 {
  text-align: center;
  font-size: 45px;
  color: rgb(81, 100, 115);
  margin-bottom: 40px;
}
.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-item >>> .el-input__inner {
  height: 48px;
  border-radius: 24px;
  padding: 0 20px;
  font-size: 16px;
  border: 1px solid #dce1e7;
  transition: border 0.3s;
}

.input-item >>> .el-input__inner:focus {
  border-color: #409eff;
}

.login-btn {
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: bold;
  background-color: #409eff;
  border: none;
  color: white;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #66b1ff;
}
</style>