<template>
  <div class="container">
    <!-- 筛选条件卡片 -->
    <el-card class="filter-card" shadow="hover">
      <div class="filter-container">
        <el-select 
          v-model="grade"
          placeholder="请选择年级"
          class="animated-select"
          @change="handleGradeChange"
          style="width: 200px">
          <el-option 
            v-for="item in grades"
            :key="item.name"
            :label="item.name"
            :value="item.value"
            class="option-item">
            <i class="el-icon-notebook-2"></i> {{ item.name }}
          </el-option>
        </el-select>

        <el-select 
          v-model="college"
          class="ml-5 animated-select"
          v-if="isAdministrator"
          placeholder="请选择学院"
          @change="handleCollegeChange"
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
          v-model="classNo"
          class="ml-5 animated-select"
          placeholder="请选择班级"
          style="width: 200px">
          <el-option 
            v-for="item in selectClassInfos"
            :key="item.className"
            :label="item.className"
            :value="item.classNo"
            class="option-item">
            <i class="el-icon-s-management"></i> {{ item.className }}
          </el-option>
        </el-select>

        <el-input 
          clearable 
          placeholder="请输入学号" 
          suffix-icon="el-icon-search" 
          class="ml-5 animated-input"
          v-model="studentNo"
          style="width: 200px">
          <template #prefix>
            <i class="el-icon-user"></i>
          </template>
        </el-input>

        <el-input 
          clearable 
          placeholder="请输入姓名" 
          suffix-icon="el-icon-s-custom" 
          class="ml-5 animated-input"
          v-model="studentName"
          style="width: 200px">
          <template #prefix>
            <i class="el-icon-edit"></i>
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
          action="/student/import"
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
        :header-cell-class-name="'table-header'"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="150" align="center"></el-table-column>
        <el-table-column prop="grade" label="年级" width="200" align="center"></el-table-column>
        <el-table-column prop="college" label="学院" width="380"></el-table-column>
        <el-table-column prop="className" label="班级" width="280"></el-table-column>
        <el-table-column prop="studentNo" label="学号" width="160"></el-table-column>
        <el-table-column prop="studentName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="telephone" label="电话" width="150"></el-table-column>
        
        <el-table-column label="操作" width="220" align="center">
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
      :title="form.id ? '编辑学生' : '新增学生'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
        <el-form-item label="年级" prop="gradeNo">
          <el-select 
            v-model="form.gradeNo"
            placeholder="请选择年级"
            class="full-width">
            <el-option 
              v-for="item in grades"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="学院" prop="collegeNo" v-if="isAdministrator">
          <el-select 
            v-model="form.collegeNo"
            placeholder="请选择学院"
            class="full-width">
            <el-option 
              v-for="item in colleges"
              :key="item.value"
              :label="item.name"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="班级" prop="classNo">
          <el-select 
            v-model="form.classNo"
            placeholder="请选择班级"
            class="full-width">
            <el-option 
              v-for="item in formClassInfos"
              :key="item.classNo"
              :label="item.className"
              :value="item.classNo">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="学号">
          <el-input 
            v-model="form.studentNo"
            placeholder="自动生成"
            disabled>
          </el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="studentName">
          <el-input 
            v-model="form.studentName"
            placeholder="请输入姓名">
          </el-input>
        </el-form-item>

        <el-form-item label="电话号码" prop="telephone">
          <el-input 
            v-model="form.telephone"
            placeholder="请输入电话号码">
            <template #prefix>
              <i class="el-icon-phone"></i>
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
import axios from "axios";

export default {
  name: "Student",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      studentNo: "",
      studentName: "",
      college:"",
      colleges: [],
      grade:"",
      grades:[],
      classNo:"",
      //编辑新增框班级信息
      formClassInfos:[],
      //查询框班级信息
      selectClassInfos:[],
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      isNewRecord: true, // 默认是新增记录   生成学号标志位

      isAdministrator:false,
      userRole:"",
      userCollege:"",
      token:"",
      uploadHeaders:{"token":JSON.parse(localStorage.getItem("user")).token}
    }
  },

  created() {
    this.load()
    //请求字典数据
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
    // 请求年级数据的数据
    this.request.get("/dict/grade").then(res => {
      this.grades = res.data
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
      this.college = this.userCollege; //
    }
  },

  methods: {
    load() {
      this.request.get("/student/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          college:this.college,
          grade:this.grade,
          classNo: this.classNo,
          studentNo: this.studentNo,
          studentName: this.studentName,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    //数据导入
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
    }
    ,
    //修改或者保存
    save() {
      this.request.post("/student", this.form).then(res => {
        if (res.code==='200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    //新增
    handleAdd() {
      //清理脏数据
      this.formClassInfos=[]

      this.isNewRecord = true
      this.dialogFormVisible = true
      this.form = {}

      if(!this.isAdministrator){
        this.form.collegeNo = this.userCollege;
      }
    },

    //编辑
    handleEdit(row) {
      //清理脏数据
      this.formClassInfos=[]
      // 将后台返回的数据映射到前端表单
     this.request.get("/student/" + row.id).then(res=>{
       if (res.code==='200') {
         this.form =res.data
       }
     })
      this.isNewRecord = false; // 标记为编辑记录
      this.dialogFormVisible = true
    },

    //删除
    del(id) {
      this.request.delete("/student/" + id).then(res => {
        if (res.code==='200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },

    //多选操作
    handleSelectionChange(val) {
      console.log(val)
      this.multipleSelection = val
    },

    //批量删除
    delBatch() {
      let ids = this.multipleSelection.map(v => v.id)  // [{}, {}, {}] => [1,2,3]
      this.request.post("/student/del/batch", ids).then(res => {
        if (res.code==='200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    //下拉框重置
    reset() {
    if(this.isAdministrator){
      this.college=""
    }
      this.grade = ""
      this.classNo=""
      this.studentNo=""
      this.studentName= ""
      this.load()
    },

    exp() {
      const exportUrl = `/student/export`;
      // 使用 axios 发起 GET 请求
      axios({
        url: exportUrl,
        method: 'GET',
        responseType: 'blob',  // 设置响应类型为 blob，以便处理文件
        params: {
          college: this.college,
          grade: this.grade,
          classNo: this.classNo,
          studentNo: this.studentNo,
          studentName: this.studentName,
        },
        headers: {
          'token': this.token,  // 携带 token
        },
      })
          .then(response => {
            const contentDisposition = response.headers['content-disposition'];
            const matchName = contentDisposition.split(';')[1].split('filename=')[1].trim();
            const fileName=decodeURIComponent(matchName)
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

    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },

    //新增编辑下拉框处理逻辑
    handleSelectChange() {
      // 若学院，年级变化，班级清空
      this.form.classNo=""
      if(this.isNewRecord){
        //若为新增，学号清空
        this.form.studentNo=""
      }

      this.performOperation();
  },
    //单独处理班级下拉框变化
    handleClassSelectChange(){
      this.performOperation();
    },

    performOperation() {
      // 若选择了年级和学院信息，查询班级信息
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

      // 只有在三个下拉框都选择了值时才执行操作
      if (this.form.gradeNo && this.form.collegeNo && this.form.classNo) {
        // 如果当前是新增模式，执行生成学号操作

        if (this.isNewRecord) {
          this.generateStudentNo();
        }
      } else {
        console.log('下拉框还未全选！');
      }
    },
    //生成学号
      generateStudentNo() {
        // 封装成对象
        const requestData = {
          gradeNo: this.form.gradeNo,
          college: this.form.collegeNo,
          classNo: this.form.classNo,
        };
        // 发送请求到后台，将 requestData 传递给后台
        this.request.post("/student/generateStudentNo", requestData).then(res => {
          // 处理响应
          if (res.code==='200') {
            this.$message.success(res.msg)
            console.log(res.data)
            //this.form.studentNo=res.data
            //赋值到输入框
            this.$set(this.form, 'studentNo', res.data)
          }
        });
      },


    //查询下拉框选择逻辑
    handleGradeChange() {
      // 在这里检查学院是否也选中，如果是，则执行相应逻辑
      if (this.college) {
        this.handleBothSelected();
      }else{
        // 处理选中年级的逻辑
        this.request.get("/classInfo/grade/"+this.grade).then(res => {
          this.selectClassInfos = res.data
        })
      }
      this.classNo=""
    },
    handleCollegeChange() {
      // 处理选中学院的逻辑
      // 在这里检查年级是否也选中，如果是，则执行相应逻辑
      if (this.grade) {
        this.handleBothSelected();
      }else{
        this.request.get("/classInfo/college/"+this.college).then(res => {
          this.selectClassInfos = res.data
        })
      }
      this.classNo=""

    },
    handleBothSelected() {
      // 处理两者都选中的逻辑
      this.request.get("/classInfo/gradeAndCollege", {
        params: {
          grade: this.grade,
          college: this.college,
        }
      }).then(res => {
        this.selectClassInfos = res.data
      })
      this.classNo=""
    }
  }
}
</script>

<style scoped>
.container {
  padding: 20px;
  background: #f5f7fa;
}

/* 筛选卡片样式 */
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

.animated-select:hover {
  transform: translateY(-2px);
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

/* 操作按钮组 */
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

.animated-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 表格样式 */
.table-card {
  border-radius: 12px;
}

.animated-table {
  transition: all 0.3s ease;
}

.animated-table:hover {
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
}

.table-header {
  background: linear-gradient(45deg, #f8f9fa, #f1f3f5) !important;
  font-weight: 600;
  color: #606266;
}

.operation-icon {
  transition: all 0.3s ease;
}

.operation-icon:hover {
  transform: scale(1.1);
}

/* 分页样式 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 弹窗样式 */
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

/* 响应式调整 */
@media (max-width: 1200px) {
  .filter-container {
    flex-direction: column;
  }
  
  .ml-5 {
    margin-left: 0 !important;
    margin-top: 10px;
  }
}
</style>

