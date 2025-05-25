<template>
  <div class="dict-container">
    <!-- 筛选区 -->
    <div class="filter-bar">
      <el-select class="filter-item" v-model="type" placeholder="请选择类型" clearable>
        <el-option
          v-for="item in types"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
      <el-button class="animated-btn filter-item" type="primary" @click="load">搜索</el-button>
      <el-button class="animated-btn" type="warning" @click="reset">重置</el-button>
    </div>

    <!-- 操作区 -->
    <div class="action-bar">
      <el-button class="animated-btn" type="primary" @click="handleAdd(null)">
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
        <el-button class="animated-btn" type="danger" slot="reference">
          批量删除 <i class="el-icon-remove-outline" />
        </el-button>
      </el-popconfirm>
    </div>

    <!-- 表格 -->
    <el-table
      class="dict-table"
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
      row-key="id"
      default-expand-all
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="value" label="值" />
      <el-table-column prop="type" label="类型" />
      <el-table-column label="操作" width="300" align="center">
        <template slot-scope="scope">
          <el-button class="animated-btn" type="success" size="mini" @click="handleEdit(scope.row)">
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
            <el-button class="animated-btn" type="danger" size="mini" slot="reference">
              删除 <i class="el-icon-remove-outline" />
            </el-button>
          </el-popconfirm>
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

    <!-- 字典信息弹窗 -->
    <el-dialog title="字典信息" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="名称">
          <el-input v-model="form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="值">
          <el-input v-model="form.value" autocomplete="off" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select clearable v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in uniqueOptions"
              :key="item.type"
              :label="item.type"
              :value="item.type"
            />
          </el-select>
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
.dict-container {
  padding: 20px;
  background-color: #f9fbfd;
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

.dict-table >>> .el-table__row:hover {
  background-color: #f0f9ff !important;
}

.pagination-bar {
  padding: 15px 0;
  display: flex;
  justify-content: center;
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
      pageSize: 50,
      name: "",
      type:"",
      types:[],
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      options: []
    }
  },
  created() {
    this.load()
  },
computed: {
  uniqueOptions()
  {
    const uniqueValues = new Set();
    return this.options.filter(item => {
      if (!uniqueValues.has(item.type)) {
        uniqueValues.add(item.type);
        return true;
      }
      return false;
    })
  }
},
  methods: {
    load() {
      this.request.get("/dict/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          type: this.type,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })

      // 请求图标的数据
      this.request.get("/dict").then(res => {
        this.options = res.data
      })
      // 请求图标的数据
      this.request.get("/dict/selectType").then(res => {
        this.types = res.data
      })
    },
    save() {
      this.request.post("/dict", this.form).then(res => {
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
      this.request.delete("/dict/" + id).then(res => {
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
      this.request.post("/dict/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.type = ""
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


