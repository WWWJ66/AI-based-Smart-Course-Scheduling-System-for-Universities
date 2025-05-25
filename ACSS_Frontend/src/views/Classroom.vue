<template>
  <div class="container">
    <!-- 筛选条件卡片 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-container">
        <el-select 
          v-model="teachingBuilding"
          placeholder="请选择教学楼"
          class="animated-select"
          style="width: 200px">
          <el-option 
            v-for="item in teachingBuildings"
            :key="item.name"
            :label="item.name"
            :value="item.value"
            class="option-item">
            <i class="el-icon-school"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-select 
          v-model="classroomType"
          placeholder="请选择教室类型"
          class="ml-5 animated-select"
          style="width: 200px">
          <el-option 
            v-for="item in classroomTypes"
            :key="item.name"
            :label="item.name"
            :value="item.value"
            class="option-item">
            <i class="el-icon-menu"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-button 
          class="ml-5 search-btn"
          type="primary" 
          @click="load"
          icon="el-icon-search">
          搜索
        </el-button>
        <el-button 
          class="reset-btn"
          type="info" 
          @click="reset"
          icon="el-icon-refresh">
          重置
        </el-button>
      </div>
    </el-card>

    <!-- 操作按钮组 -->
    <el-card class="operation-card" shadow="hover">
      <div class="operation-btns">
        <el-button 
          type="primary" 
          @click="handleAdd"
          class="animated-btn"
          icon="el-icon-circle-plus-outline">
          新增
        </el-button>
        
        <el-popconfirm
          @confirm="delBatch"
          title="您确定批量删除这些数据吗？">
          <template #reference>
            <el-button 
              type="danger"
              class="animated-btn"
              icon="el-icon-remove-outline">
              批量删除
            </el-button>
          </template>
        </el-popconfirm>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table 
        :data="tableData"
        border 
        stripe
        highlight-current-row
        class="animated-table"
        style="width: 100%"
        :header-cell-class-name="'table-header'"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column label="教学楼" min-width="160">
          <template slot-scope="scope">
            <i class="el-icon-school"></i> 
            {{ getLabel(teachingBuildings, scope.row.teachingBuildingNo, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column label="教室类型" min-width="140">
          <template slot-scope="scope">
            <i class="el-icon-menu"></i>
            {{ getLabel(classroomTypes, scope.row.classroomType, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column prop="classroomNo" label="教室编号" min-width="120"></el-table-column>
        <el-table-column prop="classroomName" label="教室名称" min-width="180"></el-table-column>
        <el-table-column prop="capacity" label="教室容量" min-width="120"></el-table-column>
        
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template slot-scope="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button 
                type="success" 
                @click="handleEdit(scope.row)"
                icon="el-icon-edit"
                circle
                class="operation-icon">
              </el-button>
            </el-tooltip>
            
            <el-popconfirm
              @confirm="del(scope.row.id)"
              title="您确定删除吗？">
              <template #reference>
                <el-tooltip content="删除" placement="top">
                  <el-button 
                    type="danger"
                    icon="el-icon-delete"
                    circle
                    class="operation-icon">
                  </el-button>
                </el-tooltip>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- 弹窗 -->
    <el-dialog 
      :title="form.id ? '编辑教室' : '新增教室'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
        <el-form-item label="教学楼">
          <el-select 
            v-model="form.teachingBuildingNo"
            placeholder="请选择教学楼"
            class="full-width">
            <el-option 
              v-for="item in teachingBuildings"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-school"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="教室类型">
          <el-select 
            v-model="form.classroomType"
            placeholder="请选择教室类型"
            class="full-width">
            <el-option 
              v-for="item in classroomTypes"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-menu"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="教室编号">
          <el-input 
            v-model="form.classroomNo"
            disabled
            placeholder="自动生成">
          </el-input>
        </el-form-item>

        <el-form-item label="教室名称">
          <el-input 
            v-model="form.classroomName"
            placeholder="请输入教室名称">
            <template #prefix>
              <i class="el-icon-office-building"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="教室容量">
          <el-input 
            v-model="form.capacity"
            placeholder="请输入教室容量"
            type="number">
            <template #prefix>
              <i class="el-icon-user-solid"></i>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="save" class="confirm-btn">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script>
export default {
  name: "Classroom",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      teachingBuilding: "",
      teachingBuildings: [],
      classroomType: "",
      classroomTypes: [],
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
    }
  },
  created() {
    this.load()
    this.request.get("/dict/teachingBuilding").then(res => {
      this.teachingBuildings = res.data
    })
    this.request.get("/dict/classroom").then(res => {
      this.classroomTypes = res.data
    })
  },

  methods: {
    load() {
      this.request.get("/classroom/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          teachingBuilding: this.teachingBuilding,
          classroomType: this.classroomType
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/classroom", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
    },
    handleEdit(row) {
      this.form = row
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/classroom/" + id).then(res => {
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
      this.request.post("/classroom/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.teachingBuilding = ""
      this.classroomType = ""
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

    handleSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
    },
    performOperation() {

      // 只有在四个下拉框都选择了值时才执行操作
      if (this.form.teachingBuildingNo && this.form.classroomType) {
          this.generateClassroomNo();
      } else {
        console.log('修改信息');
      }
    },

    generateClassroomNo() {
      // 封装成对象
      const requestData = {
        teachingBuildingNo: this.form.teachingBuildingNo,
        classroomType: this.form.classroomType
      };
      // 发送请求到后台，将 requestData 传递给后台
      this.request.post("/classroom/generateClassroomNo", requestData).then(res => {
        // 处理响应
        if (res.code === '200') {
          this.$message.success(res.msg)
          console.log(res.data)
          //赋值到输入框
          this.$set(this.form, 'classroomNo', res.data)
        }
      });
    },
  }
}
</script>

<style scoped>
/* 保持统一样式 */
.container {
  padding: 20px;
  background: #f5f7fa;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
  background: linear-gradient(145deg, #ffffff, #f6f6f6);
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.animated-select {
  transition: all 0.3s ease;
}

.search-btn {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
}

.reset-btn {
  background: linear-gradient(45deg, #909399, #a6a9ad);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
}

.operation-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.operation-btns {
  display: flex;
  gap: 15px;
}

.animated-btn {
  transition: all 0.3s ease;
  border-radius: 8px;
  padding: 12px 24px;
}

.table-card {
  border-radius: 12px;
}

.table-header {
  background: linear-gradient(45deg, #f8f9fa, #f1f3f5) !important;
  font-weight: 600;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.custom-dialog {
  border-radius: 12px;
}

.dialog-form {
  padding: 20px 40px;
}

.full-width {
  width: 100%;
}

.cancel-btn {
  background: #f0f2f5;
  border: none;
  padding: 12px 28px;
}

.confirm-btn {
  background: linear-gradient(45deg, #409EFF, #66b1ff);
  border: none;
  padding: 12px 28px;
}

@media (max-width: 1200px) {
  .filter-container {
    flex-direction: column;
  }
  
  .ml-5 {
    margin-left: 0 !important;
    margin-top: 10px;
  }
}

/* 表格优化 */
.animated-table {
  width: 100% !important;
}

.el-table__fixed-right {
  box-shadow: -2px 0 6px rgba(0,0,0,0.05);
}

.el-table__body td {
  padding: 12px 0 !important;
}

.operation-icon {
  transition: all 0.3s ease;
}

.operation-icon:hover {
  transform: scale(1.1);
}
</style>

