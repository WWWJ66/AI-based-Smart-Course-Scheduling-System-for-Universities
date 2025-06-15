<template>
  <el-menu
    :default-openeds="['1', '3']"
    class="custom-menu"
    background-color="transparent"
    text-color="#f0f0f0"
    active-text-color="#ffcc00"
    :collapse-transition="false"
    :collapse="isCollapse"
    router
  >
    <div class="menu-header">
      <transition name="fade">
        
      </transition>
      <transition name="scale">
        <b v-show="logoTextShow" class="menu-title">{{ menuTitle }}</b>
      </transition>
    </div>

    <div v-for="item in menus" :key="item.id">
      <div v-if="item.path">
        <el-menu-item :index="item.path" class="menu-item">
          <i :class="item.icon" class="menu-icon"></i>
          <span>{{ item.name }}</span>
        </el-menu-item>
      </div>
      <div v-else>
        <el-submenu :index="item.id + ''" class="menu-sub">
          <template #title>
            <i :class="item.icon" class="menu-icon"></i>
            <span>{{ item.name }}</span>
          </template>
          <div v-for="subItem in item.children" :key="subItem.id">
            <el-menu-item :index="subItem.path" class="submenu-item">
              <i :class="subItem.icon" class="menu-icon"></i>
              <span>{{ subItem.name }}</span>
            </el-menu-item>
          </div>
        </el-submenu>
      </div>
    </div>
  </el-menu>
</template>

<style scoped>
/* 动画效果 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
.scale-enter-active {
  transition: transform 0.3s ease;
}
.scale-enter {
  transform: scale(0.9);
}

/* 自定义菜单样式 */
.custom-menu {
  min-height: 100%;
  overflow-x: hidden;
  background: linear-gradient(145deg, #2c3e50, #1a252f);
  border-right: none;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.3);
  border-radius: 0 10px 10px 0;
  padding-top: 10px;
}

/* LOGO 与标题区域 */
.menu-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  gap: 10px;
}

.logo-img {
  width: 28px;
  height: 28px;
  filter: drop-shadow(0 0 4px #ffd04b);
}

.menu-title {
  color: #ffffff;
  font-size: 18px;
  font-weight: bold;
  font-family: 'Segoe UI', sans-serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.4);
}

/* 菜单项样式 */
.menu-item, .submenu-item {
  border-radius: 6px;
  margin: 5px 8px;
  transition: background 0.3s ease, transform 0.2s;
}
.menu-item:hover, .submenu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateX(2px);
}

/* 图标样式 */
.menu-icon {
  margin-right: 10px;
  color: #ffd04b;
  transition: transform 0.2s;
}
.menu-icon:hover {
  transform: scale(1.2);
}

/* 子菜单样式 */
.el-submenu__title {
  border-radius: 6px;
  margin: 5px 8px;
  transition: background 0.3s ease;
}
.el-submenu__title:hover {
  background: linear-gradient(135deg, #6a11cb, #2575fc);
}
</style>


<script>
export default {
  name: "Aside",
  props: {
    isCollapse: Boolean,
    logoTextShow: Boolean
  },
  data() {
    return {
      menus: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")).menus : [],
    }
  },

  computed: {
    // 根据用户类型动态设置菜单标题
    menuTitle() {
      let userInfo;
      const userString = localStorage.getItem("user");
      if (userString) {
        userInfo = JSON.parse(userString);
        // 解析本地存储的用户信息
        return ["ROLE_ADMINISTRATOR", "ROLE_ADMIN"].includes(userInfo.role) ? "排课系统" : "课表查询系统";
      } else {
        // 处理无法获取用户信息的情况
        return "尚未登录";
      }
    }
  }
}

</script>

