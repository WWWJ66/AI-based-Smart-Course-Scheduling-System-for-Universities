<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-section">
      <el-card shadow="hover" class="search-card">
        <div class="flex-container">
          <el-select 
            v-if="isAdministrator"
            v-model="college"
            placeholder="请选择学院"
            class="decorated-select"
            popper-class="select-dropdown"
          >
            <el-option
              v-for="item in colleges"
              :key="item.name"
              :label="item.name"
              :value="item.value"
              class="select-option"
            />
          </el-select>

          <el-input
            v-model="title"
            placeholder="请输入名称"
            class="styled-input"
            suffix-icon="el-icon-search"
          />

          <div class="button-group">
            <el-button 
              type="primary" 
              class="action-btn search-btn"
              @click="load"
            >
              <i class="el-icon-search"></i> 搜索
            </el-button>
            <el-button 
              type="info" 
              class="action-btn reset-btn"
              @click="reset"
            >
              <i class="el-icon-refresh"></i> 重置
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 通知列表 -->
    <div class="content-section">
      <el-card shadow="never" class="list-card">
        <div 
          v-for="item in tableData" 
          :key="item.id"
          class="list-item animated-item"
          @click="goToNotificationDetail(item.id)"
        >
          <div class="item-content">
            <h3 class="item-title gradient-text">{{ item.title }}</h3>
            <div class="meta-info">
              <div class="meta-item">
                <i class="el-icon-user-solid icon-accent"></i>
                <span class="meta-text">{{ userNames[item.user] }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-time icon-accent"></i>
                <span class="meta-text">{{ item.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        background
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        class="styled-pagination"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>

export default {
  name: "Home",
  data() {
    return {
      tableData: [],
      title: '',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      college:"",
      colleges: [],
      isAdministrator:false,
      userRole:"",
      userCollege:"",
      userNames: {}, // 添加 userNames 对象用于存储用户名
    }
  },

  created() {
    this.load()
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
  },

  async mounted() {
    let userInfo;
    const userString = localStorage.getItem("user");
    if (userString) {
      userInfo = JSON.parse(userString); // 解析本地存储的用户信息
      this.token = userInfo.token;
      try {
        const res = await this.request.get("/user/getUserRoleAndCollege/" + userInfo.userNo);
        this.userCollege = res.data.college;
        this.userRole = res.data.role;
        // 在获取到数据后再进行处理
        if (this.userRole === "ROLE_ADMINISTRATOR") {
          this.isAdministrator = true;
        }
      } catch (error) {
        console.error('请求失败', error);
      }
    }
    // 如果不是管理员，设置默认学院编号
    if (!this.isAdministrator) {
      this.college = this.userCollege;
    }

  },
  methods: {
    async load() {
      try {
        console.log(localStorage.getItem("user"));

        const res = await this.request.get("/notification/page", {
          params: {
            title: this.title,
            college: this.college,
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          }
        });

        this.tableData = res.data.records;
        this.total = res.data.total;
        // 确保数据加载后再获取用户名
        await this.getUserNames();
      } catch (error) {
        console.error('加载数据失败', error);
      }
    },

    reset() {
      if (this.isAdministrator) {
        this.college = ""
      }
      this.title = ""
      this.load()
    },

    handleSizeChange(pageSize) {
      console.log(pageSize)
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      console.log(pageNum)
      this.pageNum = pageNum
      this.load()
    },
    goToNotificationDetail(id) {
      console.log(id)
      // 例如，在点击标题时导航到通知详情
      this.$router.push(`/notificationDetail/${id}`);
    },

    async getUserNames() {
      for (const item of this.tableData) {
        this.$set(this.userNames, item.user, await this.getUserName(item.user));
      }
    },

    async getUserName(userNo) {
      try {
        const response = await this.request.get(`/teacher/selectByTeacherNo/${userNo}`);
        if (response.code === '200') {
          return response.data
        } else {
          return userNo
        }
      } catch (error) {
        console.error('获取用户名失败', error)
        return userNo
      }
    }
  }
}
</script>

<style scoped>
.page-container {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px;
}

/* 搜索栏样式 */
.search-section {
  margin-bottom: 24px;
}

.search-card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
  background: linear-gradient(145deg, #ffffff, #f8f9fe);
}

.flex-container {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.decorated-select {
  width: 220px;
}

.styled-input {
  flex: 1;
  max-width: 400px;
}

.button-group {
  display: flex;
  gap: 10px;
}

.action-btn {
  border-radius: 8px;
  padding: 12px 24px;
  transition: all 0.3s ease;
  border: none;
}

.search-btn {
  background: linear-gradient(45deg, #409eff, #3375ff);
}

.reset-btn {
  background: linear-gradient(45deg, #909399, #6b7280);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 通知列表样式 */
.content-section {
  margin: 24px 0;
}

.list-card {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05);
}

.list-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.list-item:hover {
  background: #f8f9fe;
  transform: translateX(10px);
  box-shadow: 4px 0 12px rgba(63, 94, 251, 0.1);
}

.item-title {
  font-size: 18px;
  color: #303133;
  margin-bottom: 12px;
  transition: color 0.3s;
}

.gradient-text {
  background: linear-gradient(45deg, #3F5EFB, #6397ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.meta-info {
  display: flex;
  gap: 25px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-accent {
  color: #409eff;
  font-size: 16px;
}

.meta-text {
  color: #606266;
  font-size: 14px;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.styled-pagination {
  padding: 12px;
  border-radius: 8px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* 动画效果 */
.animated-item {
  animation: slideIn 0.5s ease forwards;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .flex-container {
    flex-direction: column;
  }
  
  .styled-input,
  .decorated-select {
    width: 100%;
    max-width: none;
  }
  
  .button-group {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
