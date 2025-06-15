<template>
  <div class="container">
    <!-- 筛选条件卡片 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-container">
        <el-select
          v-model="term"
          placeholder="请选择学期"
          class="animated-select"
          style="width: 200px">
          <el-option
            v-for="item in terms"
            :key="item.value"
            :label="item.name"
            :value="item.value">
            <i class="el-icon-date"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-select
          v-model="grade"
          placeholder="请选择年级"
          class="ml-5 animated-select"
          style="width: 200px"
          @change="handleSelectChange">
          <el-option
            v-for="item in grades"
            :key="item.value"
            :label="item.name"
            :value="item.value">
            <i class="el-icon-notebook-1"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-select
          v-model="college"
          class="ml-5 animated-select"
          v-if="isAdministrator"
          placeholder="请选择学院"
          style="width: 200px"
          @change="handleSelectChange">
          <el-option
            v-for="item in colleges"
            :key="item.value"
            :label="item.name"
            :value="item.value">
            <i class="el-icon-office-building"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-select
          v-model="classNo"
          class="ml-5 animated-select"
          placeholder="请选择班级"
          style="width: 200px">
          <el-option
            v-for="item in selectClassInfos"
            :key="item.classNo"
            :label="item.className"
            :value="item.classNo">
            <i class="el-icon-s-management"></i> {{ item.className }}
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
        <el-button 
          class="ml-5 arrange-btn"
          type="success"
          v-if="isAdministrator" 
          @click="arrangeCourse">
          排课 <i class="el-icon-thumb"></i>
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
        
        <el-table-column prop="term" label="学期" min-width="120">
          <template slot-scope="scope">
            <i class="el-icon-date"></i> {{ scope.row.term }}
          </template>
        </el-table-column>

        <el-table-column label="学院" min-width="160">
          <template slot-scope="scope">
            <i class="el-icon-office-building"></i>
            {{ getLabel(colleges, scope.row.collegeNo, 'value', 'name')}}
          </template>
        </el-table-column>

        <el-table-column label="年级" min-width="120">
          <template slot-scope="scope">
            <i class="el-icon-notebook-1"></i>
            {{ getLabel(grades, scope.row.gradeNo, 'value', 'name')}}
          </template>
        </el-table-column>

        <el-table-column label="班级" min-width="180">
          <template slot-scope="scope">
            <i class="el-icon-s-management"></i>
            {{ getLabel(classInfos, scope.row.classNo, 'classNo', 'className')}}
          </template>
        </el-table-column>

        <el-table-column label="课程" min-width="200">
          <template slot-scope="scope">
            <i class="el-icon-notebook-2"></i>
            {{ getLabel(courseInfos, scope.row.courseNo, 'courseNo', 'courseName')}}
          </template>
        </el-table-column>

        <el-table-column prop="weekTime" label="周学时" min-width="100" align="center"></el-table-column>
        <el-table-column prop="weeks" label="周数" min-width="90" align="center"></el-table-column>

        <el-table-column label="时间固定" min-width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isFix ? 'success' : 'info'">
              {{ getFixLabel(scope.row.isFix) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="courseTime" label="上课时间" min-width="160"></el-table-column>
        
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
      :title="form.id ? '编辑课程计划' : '新增课程计划'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
        <el-form-item label="学期">
          <el-select 
            v-model="form.term"
            placeholder="请选择学期"
            class="full-width">
            <el-option 
              v-for="item in terms"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-date"></i> {{ item.name }}
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
              <i class="el-icon-notebook-1"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="班级">
          <el-select 
            v-model="form.classNo"
            placeholder="请选择班级"
            class="full-width">
            <el-option 
              v-for="item in formClassInfos"
              :key="item.classNo"
              :label="item.className"
              :value="item.classNo">
              <i class="el-icon-s-management"></i> {{ item.className }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="课程">
          <el-select 
            v-model="form.courseNo"
            filterable
            placeholder="请选择课程"
            class="full-width">
            <el-option 
              v-for="item in courseInfos"
              :key="item.courseNo"
              :label="item.courseName"
              :value="item.courseNo">
              <div class="course-option">
                <span class="course-name">{{ item.courseName }}</span>
                <span class="teacher-name">{{ item.teacherName }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="周学时">
          <el-input-number 
            v-model="form.weekTime"
            :min="1"
            :max="10"
            controls-position="right"
            class="full-width">
          </el-input-number>
        </el-form-item>

        <el-form-item label="周数">
          <el-input-number 
            v-model="form.weeks"
            :min="1"
            :max="20"
            controls-position="right"
            class="full-width">
          </el-input-number>
        </el-form-item>

        <el-form-item label="时间固定">
          <el-radio-group v-model="form.isFix">
            <el-radio-button :label="1">固定时间</el-radio-button>
            <el-radio-button :label="0">灵活安排</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="上课时间">
          <el-time-picker
            v-model="form.courseTime"
            is-range
            format="HH:mm"
            value-format="HH:mm"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            :disabled="form.isFix == 0"
            class="full-width">
          </el-time-picker>
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

.arrange-btn {
  background: linear-gradient(45deg, #67C23A, #85ce61);
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

.course-option {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.course-name {
  color: #303133;
}
.teacher-name {
  color: #909399;
  font-size: 0.9em;
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

/* 时间选择器样式 */
.el-date-editor--timerange.el-input__inner {
  width: 100%;
}
</style>

<script>
export default {
  name: "CoursePlan",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      term:"",
      terms:[],
      college:"",
      colleges:[],
      grade:"",
      grades:[],
      classNo:"",
      classInfo:"",
      classInfos:[],
      selectClassInfos:[],
      formClassInfos:[],
      courseInfos:[],
      form: { },
      dialogFormVisible: false,
      multipleSelection: [],

      isAdministrator:false,
      userRole:"",
      userCollege:""
    }
  },
  created() {
    this.load()
    this.request.get("/dict/term").then(res => {
      this.terms = res.data
    })
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
    this.request.get("/dict/grade").then(res => {
      this.grades = res.data
    })
    this.request.get("/courseInfo/course").then(res => {
      this.courseInfos = res.data
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
      this.request.get("/classInfo/college/"+this.college).then(res => {
        this.classInfos = res.data
      })
    }else{
      this.request.get("/classInfo").then(res => {
        this.classInfos = res.data
      })
    }
  },

  methods: {
    load() {
      this.request.get("/coursePlan/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          term: this.term,
          grade: this.grade,
          college: this.college,
          classNo: this.classNo,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    save() {
      this.request.post("/coursePlan/", this.form).then(res => {
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
      this.formClassInfos=[]
      this.dialogFormVisible = true
      this.form = { isFix: 0 }//  默认为0不选中
      if(!this.isAdministrator){
        this.form.collegeNo = this.userCollege;
      }
    },
    handleEdit(row) {
      this.formClassInfos=[]
      this.form = row
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/coursePlan/" + id).then(res => {
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
      this.request.post("/coursePlan/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },

    // 点击开始提交学期到系统后台排课
    arrangeCourse() {
      if (this.term == "") {
        alert("请选择学期");
      } else {
        this.request.get("/coursePlan/arrange/" + this.term)
            .then(res => {
              if (res.code === '200') {
                this.$message({message: res.msg, type: 'success'})
                this.$router.push('/Schedule')
              } else {
                this.$message.error(res.msg)
              }
            })
            .catch(error => {
              this.$message.error('排课失败')
            })
      }
    },

    reset() {
      if(this.isAdministrator){
        this.college=""
      }
      this.term = ""
      this.grade = ""
      this.classNo = ""
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
    getFixLabel(isFix) {
      return isFix == 1 ? "是" : "否";   //因为 == 会进行类型转换，而 === 不会
    },
    handleSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
    },

    performOperation() {
      // 若选择了年级和学院信息，查询班级信息      表单中的下拉框
      if (this.form.gradeNo && this.form.collegeNo) {
        this.request.get("/classInfo/gradeAndCollege", {
          params: {
            grade:this.form.gradeNo,
            college: this.form.collegeNo
          }
        }).then(res => {
          this.formClassInfos = res.data
        })
      }

      //查询按钮的下拉框数据
      if (this.grade && this.college) {
        this.request.get("/classInfo/gradeAndCollege",{
          params: {
            grade: this.grade,
            college: this.college,
          }
        }).then(res => {
          this.selectClassInfos = res.data
        })
      }
    }
  }
}

</script>


