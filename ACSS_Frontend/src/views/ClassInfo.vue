<template>
  <div class="container">
    <!-- 筛选条件卡片 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-container">
        <el-select 
          v-model="college"
          placeholder="请选择学院"
          class="animated-select"
          v-if="isAdministrator"
          style="width: 200px">
          <el-option 
            v-for="item in colleges"
            :key="item.name"
            :label="item.name"
            :value="item.value"
            class="option-item">
            <i class="el-icon-office-building"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-input 
          clearable 
          placeholder="请输入班级名称" 
          class="ml-5 animated-input"
          v-model="className"
          style="width: 200px">
          <template #prefix>
            <i class="el-icon-search"></i>
          </template>
        </el-input>

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
        <el-table-column label="学院" min-width="160">
          <template slot-scope="scope">
            {{ getLabel(colleges, scope.row.collegeNo, 'value', 'name') }}
          </template>
        </el-table-column>
        
        <el-table-column prop="gradeNo" label="年级" min-width="120">
          <template slot-scope="scope">
            {{ getLabel(grades, scope.row.gradeNo, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column label="专业" min-width="180">
          <template slot-scope="scope">
            {{ getLabel(majors, scope.row.majorNo, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column prop="classNo" label="班级编号" min-width="140"></el-table-column>
        <el-table-column prop="className" label="班级名称" min-width="200"></el-table-column>
        <el-table-column prop="teacherNo" label="班主任" min-width="140">
          <template slot-scope="scope">
            {{ getLabel(teachers, scope.row.teacherNo, 'teacherNo', 'teacherName') }}
          </template>
        </el-table-column>
        <el-table-column prop="studentNumber" label="班级人数" min-width="120"></el-table-column>
        
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
      :title="form.id ? '编辑班级' : '新增班级'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
        <el-form-item label="年级">
          <el-select 
            v-model="form.gradeNo"
            placeholder="请选择年级"
            class="full-width">
            <el-option 
              v-for="item in grades"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-notebook-2"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="学院" v-if="isAdministrator">
          <el-select 
            v-model="form.collegeNo"
            placeholder="请选择学院"
            class="full-width">
            <el-option 
              v-for="item in colleges"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-office-building"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="专业">
          <el-select 
            v-model="form.majorNo"
            placeholder="请选择专业"
            class="full-width"
            filterable>
            <el-option 
              v-for="item in majors"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="班级编号">
          <el-input 
            v-model="form.classNo"
            disabled
            placeholder="自动生成">
          </el-input>
        </el-form-item>

        <el-form-item label="班级名称">
          <el-input 
            v-model="form.className"
            placeholder="请输入班级名称">
            <template #prefix>
              <i class="el-icon-collection-tag"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="班主任">
          <el-select 
            v-model="form.teacherNo"
            placeholder="请选择班主任"
            class="full-width">
            <el-option 
              v-for="item in formTeachers"
              :key="item.teacherNo"
              :label="item.teacherName"
              :value="item.teacherNo">
              {{ item.teacherName }}
            </el-option>
          </el-select>
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
  name: "ClassInfo",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      className: "",
      college: "",
      colleges: [],
      grades: [],
      teachers: [],
      formTeachers: [],
      majors: [],
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      isAdministrator:false,
      userRole:"",
      userCollege:"",
    }
  },
  created() {
    this.load(),
        this.request.get("/dict/college").then(res => {
          this.colleges = res.data
        }),
        this.request.get("/dict/grade").then(res => {
          this.grades = res.data
        }),
        this.request.get("/dict/major").then(res => {
          this.majors = res.data
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
      this.request.get("/teacher/selectByCollege/" + this.college).then(res => {
        this.teachers = res.data
      })
    }else {
      this.request.get("/teacher").then(res => {
        this.teachers = res.data
      })
    }
  },

  methods: {
    load() {
      this.request.get("/classInfo/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          college: this.college,
          className: this.className,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/classInfo", this.form).then(res => {
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

      // 如果不是管理员，设置默认学院编号
      if (!this.isAdministrator) {
        this.form.collegeNo = this.userCollege;
        this.getTeacherList()
      }
    },

    handleEdit(row) {
      this.form = row
      this.dialogFormVisible = true
    },

    del(id) {
      this.request.delete("/classInfo/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/classInfo/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      if(this.isAdministrator){
      this.college=""
    }
      this.className = ""
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

    getTeacherList() {
      this.request.get("/teacher/selectByCollege/" + this.form.collegeNo).then(res => {
        this.formTeachers = res.data
      })
    },

    handleCollegeSelectChange() {
      this.performOperation();
      this.getTeacherList();
    },
    handleSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
    },

    performOperation() {
      if (this.form.gradeNo && this.form.collegeNo && this.form.majorNo) {
          this.generateClassNo();
      } else {
        console.log('下拉框还未全选！');
      }
    },
    generateClassNo() {
      // 封装成对象
      const requestData = {
        gradeNo: this.form.gradeNo,
        college: this.form.collegeNo,
        major: this.form.majorNo,
      };
      // 发送请求到后台，将 requestData 传递给后台
      this.request.post("/classInfo/generateClassNo", requestData).then(res => {
        // 处理响应
        if (res.code === '200') {
          this.$message.success(res.msg)
          console.log(res.data)
          //赋值到输入框
          this.$set(this.form, 'classNo', res.data)
        }
      });
    }
  }
}
</script>

<style scoped>
/* 保持与学生页面相同的样式 */
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

/* 新增表格优化 */
.animated-table {
  width: 100% !important;
}

.el-table__fixed-right {
  box-shadow: -2px 0 6px rgba(0,0,0,0.05);
}

.el-table__body td {
  padding: 12px 0 !important;
}
</style>