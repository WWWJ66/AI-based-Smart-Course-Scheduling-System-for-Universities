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
          placeholder="请输入工号" 
          suffix-icon="el-icon-search" 
          class="ml-5 animated-input"
          v-model="teacherNo"
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
          v-model="teacherName"
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
          action="http://localhost:8081/teacher/import"
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
        <el-table-column type="selection" width="155" align="center"></el-table-column>
        <el-table-column prop="college" label="学院" width="320"></el-table-column>
        <el-table-column prop="teacherNo" label="工号" width="260"></el-table-column>
        <el-table-column prop="teacherName" label="姓名" width="220"></el-table-column>
        <el-table-column prop="profession" label="职称" width="240"></el-table-column>
        <el-table-column prop="telephone" label="电话" width="260"></el-table-column>
        
        <el-table-column label="操作" width="180" align="center">
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
      :title="form.id ? '编辑教师' : '新增教师'"
      :visible.sync="dialogFormVisible"
      width="500px"
      center
      custom-class="custom-dialog">
      <el-form 
        label-width="100px" 
        size="medium"
        class="dialog-form">
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
              <i class="el-icon-office-building"></i> {{ item.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="工号">
          <el-input 
            v-model="form.teacherNo"
            placeholder="自动生成"
            disabled>
          </el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="teacherName">
          <el-input 
            v-model="form.teacherName"
            placeholder="请输入姓名">
            <template #prefix>
              <i class="el-icon-user"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="职称" prop="profession">
          <el-select 
            v-model="form.profession"
            placeholder="请选择职称"
            class="full-width">
            <el-option 
              v-for="item in profession"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              {{ item.label }}
            </el-option>
          </el-select>
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
  name: "Teacher",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      teacherNo: "",
      teacherName: "",
      college:"",
      colleges: [],
      form: {},
      dialogFormVisible: false,
      multipleSelection: [],
      isNewRecord: true,
      profession: [
        {
          label: "教授",
          value: "1"
        },
        {
          label: "副教授",
          value: "2"
        },
        {
          label: "讲师",
          value: "3"
        },
        {
          label: "助教",
          value: "4"
        }
      ],

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

  },

  //用户角色和学院获取
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
    }
  },

  methods: {
    load() {
      this.request.get("/teacher/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          college: this.college,
          teacherNo: this.teacherNo,
          teacherName: this.teacherName,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    exp() {
      const exportUrl = `http://localhost:8081/teacher/export`;

      // 使用 axios 发起 GET 请求
      axios({
        url: exportUrl,
        method: 'GET',
        responseType: 'blob',  // 设置响应类型为 blob，以便处理文件
        params: {
          college: this.college,
          teacherNo: this.teacherNo,
          teacherName: this.teacherName,
        },
        headers: {
          'token': this.token,  // 携带 token
        },
      })
          .then(response => {
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
      this.request.post("/teacher", this.form).then(res => {
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
      this.isNewRecord = true;
      this.dialogFormVisible = true
      this.form = {}
      if(!this.isAdministrator){
        this.form.collegeNo = this.userCollege;
        this.generateTeacherNo()
      }
    },

    //编辑
    handleEdit(row) {
      // 将后台返回的数据映射到前端表单
      this.request.get("/teacher/" + row.id).then(res=>{
            if (res.code==='200') {
              this.form =res.data
            }
          }
      )
      this.isNewRecord = false; // 标记为编辑记录
      this.dialogFormVisible = true
    },
    del(id) {
      this.request.delete("/teacher/" + id).then(res => {
        if (res.code==='200') {
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
      this.request.post("/teacher/del/batch", ids).then(res => {
        if (res.code==='200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      if(this.isAdministrator){   //是系统管理员才可以清空
        this.college=""
      }
      this.teacherNo = ""
      this.teacherName = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    formatEmployment (type) {
      // 根据源数据经过处理 把处理之后的数据返回
      // 定义一个映射结构
      const map = {
        1: '教授',
        2: '副教授',
        3: '讲师',
        4: '助教'
      }
      return map[type]
    },
    //学院下拉框变化
    handleSelectChange() {
      // 处理每个下拉框的变化
      // 如果当前是新增模式，执行生成工号操作
      if (this.isNewRecord) {
        this.generateTeacherNo();
      }
     else {
      console.log('修改信息');
    }
    },

    generateTeacherNo() {
      // 封装成对象
      const requestData = {
        college: this.form.collegeNo
      };

      // 发送请求到后台，将 requestData 传递给后台
      this.request.post("/teacher/generateTeacherNo", requestData).then(res => {
        // 处理响应
        if (res.code==='200') {
          this.$message.success(res.msg)
          console.log(res.data)
          this.$set(this.form, 'teacherNo', res.data)
        }
      });
    },
  }
}
</script>


<style scoped>
/* 使用与学生页面相同的样式 */
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

.animated-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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
</style>
