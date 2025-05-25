<template>
  <div class="menu-container">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="name"
        placeholder="请输入名称"
        suffix-icon="el-icon-search"
        class="filter-item"
      />
      <el-button type="primary" class="animated-btn filter-item" @click="load">搜索</el-button>
      <el-button type="warning" class="animated-btn" @click="reset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" class="animated-btn" @click="handleAdd(null)">
        新增 <i class="el-icon-circle-plus-outline" />
      </el-button>
      <el-popconfirm
        class="ml-5"
        confirm-button-text="确定"
        cancel-button-text="取消"
        icon="el-icon-info"
        icon-color="red"
        title="您确定批量删除这些数据吗？"
        @confirm="delBatch"
      >
        <el-button type="danger" class="animated-btn" slot="reference">
          批量删除 <i class="el-icon-remove-outline" />
        </el-button>
      </el-popconfirm>
    </div>

    <!-- 表格 -->
    <el-table
      class="menu-table"
      :data="tableData"
      border
      stripe
      row-key="id"
      default-expand-all
      :header-cell-class-name="'headerBg'"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="path" label="路径" />
      <el-table-column prop="pagePath" label="页面路径" />
      <el-table-column label="图标" align="center" label-class-name="fontSize12">
        <template slot-scope="scope">
          <i :class="scope.row.icon" class="menu-icon" />
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortNum" label="顺序" />
      <el-table-column label="操作" width="300" align="center">
        <template slot-scope="scope">
          <el-button
            v-if="!scope.row.pid && !scope.row.path"
            type="primary"
            size="mini"
            class="animated-btn"
            @click="handleAdd(scope.row.id)"
          >
            新增子菜单 <i class="el-icon-plus" />
          </el-button>
          <el-button type="success" size="mini" class="animated-btn" @click="handleEdit(scope.row)">
            编辑 <i class="el-icon-edit" />
          </el-button>
          <el-popconfirm
            class="ml-5"
            confirm-button-text="确定"
            cancel-button-text="取消"
            icon="el-icon-info"
            icon-color="red"
            title="您确定删除吗？"
            @confirm="del(scope.row.id)"
          >
            <el-button type="danger" size="mini" class="animated-btn" slot="reference">
              删除 <i class="el-icon-remove-outline" />
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog title="菜单信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="路径">
          <el-input v-model="form.path" autocomplete="off" />
        </el-form-item>
        <el-form-item label="页面路径">
          <el-input v-model="form.pagePath" autocomplete="off" />
        </el-form-item>
        <el-form-item label="图标">
          <el-select v-model="form.icon" placeholder="请选择" clearable style="width: 100%">
            <el-option
              v-for="item in options"
              :key="item.name"
              :label="item.name"
              :value="item.value"
            >
              <i :class="item.value" class="menu-icon" /> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="顺序">
          <el-input v-model="form.sortNum" autocomplete="off" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" autocomplete="off" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.menu-container {
  padding: 20px;
  background-color: #f7f9fc;
}

.filter-bar,
.action-bar {
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

.menu-table {
  border-radius: 8px;
  overflow: hidden;
}

.menu-table >>> .el-table__row:hover {
  background-color: #f0f9ff !important;
}

.menu-icon {
  font-size: 18px;
  margin-right: 5px;
}

.dialog-footer {
  text-align: right;
}
</style>

<script>
export default {
  name: "Menu",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      options: []
    }
  },
  created() {
    this.load()
  },
  methods: {
    load() {
      this.request.get("/menu", {
        params: {
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data
      })

      // 请求图标的数据
      this.request.get("/menu/icons").then(res => {
        this.options = res.data
      })
    },
    save() {
      this.request.post("/menu", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd(pid) {
      this.dialogFormVisible = true
      this.form = {}
      if (pid) {
        this.form.pid = pid
      }
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/menu/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/menu/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
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
  }
}
</script>


