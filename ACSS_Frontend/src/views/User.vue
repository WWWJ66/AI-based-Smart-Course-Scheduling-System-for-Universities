<template>
  <div class="user-role-container">
    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select class="filter-item" v-model="role" placeholder="请选择角色" clearable>
        <el-option
          v-for="item in roles"
          :key="item.name"
          :label="item.name"
          :value="item.value"
        />
      </el-select>

      <el-input
        class="filter-item"
        placeholder="请输入用户账号"
        suffix-icon="el-icon-search"
        v-model="userNo"
        clearable
      />

      <el-button class="animated-btn filter-item" type="primary" @click="load">搜索</el-button>
      <el-button class="animated-btn" type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      class="user-role-table"
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
    >
      <el-table-column prop="id" label="ID" width="180" />
      <el-table-column prop="userNo" label="账号" width="350" />
      <el-table-column prop="username" label="用户名" width="200" />

      <el-table-column label="用户头像" width="200" align="center">
        <template slot-scope="scope">
          <img
            :src="scope.row.avatarUrl"
            alt="Avatar"
            class="avatar-img"
          />
        </template>
      </el-table-column>

      <el-table-column prop="role" label="用户角色" width="520" align="center">
        <template slot-scope="scope">
          <el-dropdown trigger="click">
            <el-button type="primary" icon="el-icon-user" class="animated-btn small-btn">
              {{ getLabel(roles, scope.row.role, 'value', 'name') || '选择角色' }}
              <i class="el-icon-arrow-down el-icon--right" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="r in roles"
                :key="r.value"
                @click.native="changeUserRole(scope.row.id, r.value)"
              >
                {{ r.name }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>

      <el-table-column label="重置密码" width="250" align="center">
        <template slot-scope="scope">
          <el-button class="animated-btn" type="danger" size="mini" @click="resetPassword(scope.row.id)">
            重置密码
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[5, 10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>
  </div>
</template>

<style scoped>
.user-role-container {
  padding: 20px;
  background-color: #f9fbfd;
}

.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.filter-item {
  width: 200px;
}

.animated-btn {
  transition: all 0.3s ease;
  border-radius: 6px;
}

.animated-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.small-btn {
  font-size: 13px;
  padding: 6px 12px;
}

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
}

.user-role-table >>> .el-table__row:hover {
  background-color: #f0f9ff !important;
}

.pagination-bar {
  padding: 15px 0;
  display: flex;
  justify-content: center;
}
</style>


<script>
export default {
  name: "User",
  data() {
    return {
      tableData: [],
      role: "",
      roles: [
        {
          name: "超级管理员",
          value: "ROLE_ADMINISTRATOR"
        },
        {
          name: "管理员",
          value: "ROLE_ADMIN"
        },
        {
          name: "教师",
          value: "ROLE_TEACHER"
        },
        {
          name: "学生",
          value: "ROLE_STUDENT"
        }
      ],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      userNo: "",
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/user/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          role: this.role,
          userNo: this.userNo,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    reset() {
      this.role = ""
      this.userNo = ""
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
    getLabel(list, id, value, label) {
      if (id !== '' && Array.isArray(list) && list.length !== 0) {
        return !list.find(item => item[value] === id) ? id : list.find(item => item[value] === id)[label]
      } else {
        return id
      }
    },
    changeUserRole(id, role) {      // 在这里添加处理用户选择角色的逻辑
      this.request.get("/user/editUserRole",{
        params: {
          id: id,
          role:role,
        }
      }).then(res => {
        if (res.code === '200') {
          this.$message.success("用户角色修改成功")
          this.load()
        } else {
          this.$message.error("用户角色修改失败")
        }
      })
    },
    resetPassword(id){
      this.request.get("/user/resetPassword/"+id).then(res => {
        if (res.code === '200') {
          this.$message.success("用户密码重置成功")
          this.load()
        } else {
          this.$message.error("用户密码重置失败")
        }
      })
    }
  }
}

</script>
