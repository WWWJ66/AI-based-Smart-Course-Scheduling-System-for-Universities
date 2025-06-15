<template>
  <div class="detail-container">
    <!-- 标题区 -->
    <el-card shadow="never" class="header-card">
      <div class="header-content">
        <h1 class="title-gradient">{{ notification.title }}</h1>
        <div class="meta-wrapper">
          <div class="meta-group">
            <div class="meta-item">
              <i class="el-icon-user-solid icon-highlight"></i>
              <span class="meta-text">{{ userName }}</span>
            </div>
            <div class="meta-item">
              <i class="el-icon-time icon-highlight"></i>
              <span class="meta-text">{{ notification.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 内容区 -->
    <el-card shadow="never" class="content-card">
      <div class="editor-wrapper">
        <mavon-editor
          class="styled-editor"
          :value="notification.content"
          :subfield="false"
          :defaultOpen="'preview'"
          :toolbarsFlag="false"
          :editable="false"
          :scrollStyle="true"
          :ishljs="true"
        />
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "Notification",
  data() {
    return {
      notification: {},
      userName: "",
      dialogFormVisible: false
    };
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      // 通过 `vm` 访问组件实例
      vm.loadNotification(to.params.id);
    });
  },
  beforeRouteUpdate(to, from, next) {
    // 当路由更新时加载通知数据
    this.loadNotification(to.params.id);
    next();
  },

  methods: {
    async loadNotification(id) {
      try {
        const res = await this.request.get("/notification/" + id);
        this.notification = res.data;

        // 获取用户名
        if (this.notification.user) {
          const userNameResponse = await this.getUserName(this.notification.user);
          this.userName = userNameResponse.code === '200' ? userNameResponse.data : this.notification.user;
        }
      } catch (error) {
        console.error('加载通知数据失败', error);
      }
    },

    async getUserName(userNo) {
      try {
        const response = await this.request.get(`/teacher/selectByTeacherNo/${userNo}`);
        return response;
      } catch (error) {
        console.error('获取用户名失败', error);
        return { code: '500', data: userNo };  //返回一个默认值或者错误信息
      }
    }
  }
};
</script>

<style scoped>
.detail-container {
  background: #f5f7fa;
  padding: 24px;
  min-height: 100vh;
}

/* 头部卡片样式 */
.header-card {
  border-radius: 12px;
  margin-bottom: 24px;
  background: linear-gradient(145deg, #ffffff, #f8f9fe);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  border: none;
}

.header-content {
  padding: 20px;
}

.title-gradient {
  font-size: 28px;
  margin: 0 0 20px 0;
  background: linear-gradient(45deg, #3F5EFB, #6397ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s ease;
  cursor: pointer;
}

.title-gradient:hover {
  transform: translateX(5px);
}

/* 元信息样式 */
.meta-wrapper {
  border-top: 1px dashed #eee;
  padding-top: 16px;
}

.meta-group {
  display: flex;
  gap: 32px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.icon-highlight {
  color: #409eff;
  font-size: 18px;
  vertical-align: middle;
}

.meta-text {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

/* 内容区样式 */
.content-card {
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.editor-wrapper {
  padding: 20px;
  min-height: 600px;
}

/* 自定义编辑器样式 */
.styled-editor {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
}

.styled-editor ::v-deep .v-note-wrapper {
  border: none !important;
  background: #fafbff;
}

.styled-editor ::v-deep .v-show-content {
  background: #ffffff !important;
  padding: 30px 40px !important;
}

.styled-editor ::v-deep code {
  background: #f3f4ff !important;
  padding: 2px 6px;
  border-radius: 4px;
}

.styled-editor ::v-deep pre code {
  background: #f8f9fe !important;
  padding: 15px !important;
  border-radius: 8px !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-container {
    padding: 15px;
  }
  
  .title-gradient {
    font-size: 22px;
  }
  
  .meta-group {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .editor-wrapper {
    padding: 10px;
  }
  
  .styled-editor ::v-deep .v-show-content {
    padding: 20px 15px !important;
  }
}

@keyframes contentAppear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.styled-editor {
  animation: contentAppear 0.6s ease;
}
</style>
