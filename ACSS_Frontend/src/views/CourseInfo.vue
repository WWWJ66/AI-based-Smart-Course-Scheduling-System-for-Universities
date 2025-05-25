<template>
  <div class="container">
    <!-- 筛选条件卡片 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-container">
        <el-select 
          v-model="college"
          placeholder="请选择开课学院"
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

        <el-select 
          v-model="courseAttribute"
          placeholder="请选择课程属性"
          class="ml-5 animated-select"
          style="width: 200px">
          <el-option 
            v-for="item in courseAttributes"
            :key="item.name"
            :label="item.name"
            :value="item.value"
            class="option-item">
            <i class="el-icon-collection"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-input 
          clearable 
          placeholder="请输入课程名称" 
          class="ml-5 animated-input"
          v-model="courseName"
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

        <el-upload 
          action="http://localhost:8081/courseInfo/import"
          :show-file-list="false"
          :headers="uploadHeaders"
          class="ml-5">
          <el-button 
            type="info"
            class="animated-btn"
            icon="el-icon-bottom">
            导入
          </el-button>
        </el-upload>

        <el-button 
          type="info" 
          @click="exp"
          class="ml-5 animated-btn"
          icon="el-icon-top">
          导出
        </el-button>
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
        <el-table-column label="开课学院" min-width="180">
          <template slot-scope="scope">
            <i class="el-icon-office-building"></i>
            {{ getLabel(colleges, scope.row.collegeNo, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column label="课程属性" min-width="160">
          <template slot-scope="scope">
            <i class="el-icon-collection"></i>
            {{ getLabel(courseAttributes, scope.row.courseAttribute, 'value', 'name') }}
          </template>
        </el-table-column>

        <el-table-column prop="courseNo" label="课程编号" min-width="140"></el-table-column>
        <el-table-column prop="courseName" label="课程名称" min-width="200"></el-table-column>

        <el-table-column label="上课教师" min-width="160">
          <template slot-scope="scope">
            <i class="el-icon-user"></i>
            {{ getLabel(teachers, scope.row.teacherNo, 'teacherNo', 'teacherName') }}
          </template>
        </el-table-column>

        <el-table-column label="教室类型" min-width="160">
          <template slot-scope="scope">
            <i class="el-icon-school"></i>
            {{ getLabel(classrooms, scope.row.classroomType, 'value', 'name') }}
          </template>
        </el-table-column>
        
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
      :title="form.id ? '编辑课程' : '新增课程'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
        <el-form-item label="开课学院" v-if="isAdministrator">
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

        <el-form-item label="课程属性">
          <el-select 
            v-model="form.courseAttribute"
            placeholder="请选择课程属性"
            class="full-width">
            <el-option 
              v-for="item in courseAttributes"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-collection"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="课程编号">
          <el-input 
            v-model="form.courseNo"
            disabled
            placeholder="自动生成">
          </el-input>
        </el-form-item>

        <el-form-item label="课程名称">
          <el-input 
            v-model="form.courseName"
            placeholder="请输入课程名称">
            <template #prefix>
              <i class="el-icon-notebook-2"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="上课教师">
          <el-select 
            v-model="form.teacherNo"
            placeholder="请选择教师"
            class="full-width">
            <el-option 
              v-for="item in formTeachers"
              :key="item.teacherNo"
              :label="item.teacherName"
              :value="item.teacherNo">
              <i class="el-icon-user"></i> {{ item.teacherName }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="教室类型">
          <el-select 
            v-model="form.classroomType"
            placeholder="请选择教室类型"
            class="full-width">
            <el-option 
              v-for="item in classrooms"
              :key="item.value"
              :label="item.name"
              :value="item.value">
              <i class="el-icon-school"></i> {{ item.name }}
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
  flex-wrap: wrap;
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

/* 课程特色图标 */
.el-icon-notebook-2 {
  color: #67C23A;
}
.el-icon-collection {
  color: #E6A23C;
}
</style>

<script>
import axios from "axios";

export default {
  name: "CourseInfo",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      college: "",
      colleges: [],
      courseAttribute: "",
      courseAttributes: [],
      teachers: [],
      formTeachers: [],
      classrooms: [],
      courseName: "",
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      isAdministrator:false,
      userRole:"",
      userCollege:"",
      token:"",
      uploadHeaders:{"token":JSON.parse(localStorage.getItem("user")).token}
    }
  },
  created() {
    this.load()
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
    this.request.get("/dict/courseAttribute").then(res => {
      this.courseAttributes = res.data
    })
    this.request.get("/dict/classroom").then(res => {
      this.classrooms = res.data
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
      this.request.get("/courseInfo/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          college: this.college,
          courseAttribute: this.courseAttribute,
          courseName: this.courseName,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },

    //数据导出
    exp() {
      const exportUrl = `http://localhost:8081/courseInfo/export`;
      // 使用 axios 发起 GET 请求
      axios({
        url: exportUrl,
        method: 'GET',
        responseType: 'blob',  // 设置响应类型为 blob，以便处理文件
        params: {
          college: this.college,
          courseAttribute: this.courseAttribute,
          courseName: this.courseName,
        },
        headers: {
          'token': this.token,  // 携带 token
        },
      }).then(response => {
            const contentDisposition = response.headers['content-disposition'];
            const matchName = contentDisposition.split(';')[1].split('filename=')[1].trim();
            const fileName = decodeURIComponent(matchName);
            const downloadUrl = URL.createObjectURL(new Blob([response.data]));

            // 创建一个下载链接
            const link = document.createElement('a');
            link.href = downloadUrl;
            link.setAttribute('download', fileName);
            document.body.appendChild(link);

            // 触发点击链接的操作
            link.click();
            // 清理
            document.body.removeChild(link);
          })
          .catch(error => {
            // 处理错误
            console.error('导出请求失败:', error);
          });
    },

    handleExcelImportSuccess(response) {
      // 在这里处理上传成功后的逻辑
      if (response.code === '200') {
        // 在页面上显示成功导入的数据条数，
        this.$message.success(response.msg);
      } else {
        // 处理上传失败的情况
        this.$message.error(response.msg);
      }
      this.load()
    },

    save() {
      this.request.post("/courseInfo", this.form).then(res => {
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
      this.request.delete("/courseInfo/" + id).then(res => {
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
      this.request.post("/courseInfo/del/batch", ids).then(res => {
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
      this.courseAttribute = ""
      this.courseName = ""
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
    async getTeacherList() {
      this.request.get("/teacher/selectByCollege/" + this.form.collegeNo).then(res => {
        this.formTeachers = res.data
      })
    },

    handleCollegeSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
      this.getTeacherList();
    },

    handleCourseAttributeSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
    },

    performOperation() {
      if (this.form.collegeNo && this.form.courseAttribute) {
          this.generateCourseNo();
      } else {
        console.log('下拉框还未全选！');
      }
    },
    generateCourseNo() {
      const requestData = {
        college: this.form.collegeNo,
        courseAttribute: this.form.courseAttribute,
      };
      // 发送请求到后台，将 requestData 传递给后台
      this.request.post("/courseInfo/generateCourseNo", requestData).then(res => {
        // 处理响应
        if (res.code === '200') {
          this.$message.success(res.msg)
          console.log(res.data)
          this.$set(this.form, 'courseNo', res.data)
        }
      });
    },
  }
}
</script>


