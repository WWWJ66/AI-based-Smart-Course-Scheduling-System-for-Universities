<template>
  <div class="notice-container">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="college" class="filter-item" v-if="isAdministrator" placeholder="请选择学院">
        <el-option v-for="item in colleges" :key="item.name" :label="item.name" :value="item.value" />
      </el-select>

      <el-input
        v-model="title"
        class="filter-item"
        placeholder="请输入名称"
        suffix-icon="el-icon-search"
      />
      <el-button type="primary" class="animated-btn filter-item" @click="load">搜索</el-button>
      <el-button type="warning" class="animated-btn" @click="reset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" class="animated-btn" @click="handleAdd">
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
      class="notice-table"
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="view(scope.row.content)">查看内容</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="collegeNo" label="通知对象">
        <template slot-scope="scope">
          {{ getLabel(scopes, colleges, scope.row.collegeNo, 'value', 'name') }}
        </template>
      </el-table-column>
      <el-table-column prop="user" label="发布人">
        <template slot-scope="scope">
          {{ getLabel(teachers, emptyList, scope.row.user, 'teacherNo', 'teacherName') }}
        </template>
      </el-table-column>
      <el-table-column prop="time" label="发布时间" />
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button type="success" class="animated-btn" size="mini" @click="handleEdit(scope.row)">
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
            <el-button type="danger" class="animated-btn" size="mini" slot="reference">
              删除 <i class="el-icon-remove-outline" />
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2, 5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog title="通知内容" :visible.sync="dialogFormVisible" width="60%">
      <el-form label-width="80px" size="small">
        <el-form-item label="标题">
          <el-input v-model="form.title" autocomplete="off" />
        </el-form-item>

        <el-form-item label="通知对象" v-if="isAdministrator">
          <el-select v-model="form.collegeNo" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in scopes" :key="item.name" :label="item.name" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容">
          <mavon-editor ref="md" v-model="form.content" :ishljs="true" @imgAdd="imgAdd" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </el-dialog>

    <!-- 内容查看弹窗 -->
    <el-dialog title="通知内容" :visible.sync="viewDialogVis" width="60%">
      <el-card>
        <mavon-editor
          class="md"
          :value="content"
          :subfield="false"
          :defaultOpen="'preview'"
          :toolbarsFlag="false"
          :editable="false"
          :scrollStyle="true"
          :ishljs="true"
        />
      </el-card>
    </el-dialog>
  </div>
</template>

<style scoped>
.notice-container {
  padding: 20px;
  background: #f9f9f9;
}

.filter-bar,
.action-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
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
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.1);
}

.notice-table {
  border-radius: 10px;
  overflow: hidden;
}

.notice-table >>> .el-table__row:hover {
  background-color: #f5f7fa !important;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.dialog-footer {
  text-align: right;
}
</style>
<template>
  <div class="notice-container">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="college" class="filter-item" v-if="isAdministrator" placeholder="请选择学院">
        <el-option v-for="item in colleges" :key="item.name" :label="item.name" :value="item.value" />
      </el-select>

      <el-input
        v-model="title"
        class="filter-item"
        placeholder="请输入名称"
        suffix-icon="el-icon-search"
      />
      <el-button type="primary" class="animated-btn filter-item" @click="load">搜索</el-button>
      <el-button type="warning" class="animated-btn" @click="reset">重置</el-button>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" class="animated-btn" @click="handleAdd">
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
      class="notice-table"
      :data="tableData"
      border
      stripe
      :header-cell-class-name="'headerBg'"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="view(scope.row.content)">查看内容</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="collegeNo" label="通知对象">
        <template slot-scope="scope">
          {{ getLabel(scopes, colleges, scope.row.collegeNo, 'value', 'name') }}
        </template>
      </el-table-column>
      <el-table-column prop="user" label="发布人">
        <template slot-scope="scope">
          {{ getLabel(teachers, emptyList, scope.row.user, 'teacherNo', 'teacherName') }}
        </template>
      </el-table-column>
      <el-table-column prop="time" label="发布时间" />
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button type="success" class="animated-btn" size="mini" @click="handleEdit(scope.row)">
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
            <el-button type="danger" class="animated-btn" size="mini" slot="reference">
              删除 <i class="el-icon-remove-outline" />
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-sizes="[2, 5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog title="通知内容" :visible.sync="dialogFormVisible" width="60%">
      <el-form label-width="80px" size="small">
        <el-form-item label="标题">
          <el-input v-model="form.title" autocomplete="off" />
        </el-form-item>

        <el-form-item label="通知对象" v-if="isAdministrator">
          <el-select v-model="form.collegeNo" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in scopes" :key="item.name" :label="item.name" :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容">
          <mavon-editor ref="md" v-model="form.content" :ishljs="true" @imgAdd="imgAdd" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </div>
    </el-dialog>

    <!-- 内容查看弹窗 -->
    <el-dialog title="通知内容" :visible.sync="viewDialogVis" width="60%">
      <el-card>
        <mavon-editor
          class="md"
          :value="content"
          :subfield="false"
          :defaultOpen="'preview'"
          :toolbarsFlag="false"
          :editable="false"
          :scrollStyle="true"
          :ishljs="true"
        />
      </el-card>
    </el-dialog>
  </div>
</template>

<style scoped>
.notice-container {
  padding: 20px;
  background: #f9f9f9;
}

.filter-bar,
.action-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
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
  box-shadow: 0 6px 14px rgba(0, 0, 0, 0.1);
}

.notice-table {
  border-radius: 10px;
  overflow: hidden;
}

.notice-table >>> .el-table__row:hover {
  background-color: #f5f7fa !important;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.dialog-footer {
  text-align: right;
}
</style>


<script>

import axios from "axios";
const NOTIFICATION_ADMIN_URL = "/notification/admin/page";
const NOTIFICATION_ADMINISTRATOR_URL = "/notification/administrator/page";
export default {
  name: "Notification",
  data() {
    return {
      form: {},
      tableData: [],
      title: '',
      multipleSelection: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogFormVisible: false,
      content: '',
      viewDialogVis: false,

      college:"",
      colleges: [],
      scopes:[],
      isAdministrator:false,
      userRole:"",
      userCollege:"",
      teachers: [],
      emptyList: []
    }
  },
  created() {
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
    this.request.get("/dict/scope").then(res => {
      this.scopes = res.data
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
    this.load()    //若在create时调用，用户类型角色还没有获取到
  },

  methods: {
    view(content) {
      this.content = content
      this.viewDialogVis = true
    },
    // 绑定@imgAdd event
    imgAdd(pos, $file) {
      let $vm = this.$refs.md
      // 第一步.将图片上传到服务器.
      const formData = new FormData();
      formData.append('file', $file);
      axios({
        url: 'http://localhost:8081/file/upload',
        method: 'post',
        data: formData,
        headers: {'Content-Type': 'multipart/form-data'},
      }).then((res) => {
        // 第二步.将返回的url替换到文本原位置![...](./0) -> ![...](url)
        $vm.$img2Url(pos, res.data);
      })
    },

    load() {
      this.request.get(this.isAdministrator ? NOTIFICATION_ADMINISTRATOR_URL : NOTIFICATION_ADMIN_URL, {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
          college: this.college,
        },
      }).then((res) => {
        this.tableData = res.data.records;
        this.total = res.data.total;
      });
    },

    handleAdd() {
      this.dialogFormVisible = true
      this.form = {}
      this.form.collegeNo=this.collegeNo
    },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.dialogFormVisible = true
    },

    del(id) {
      this.request.delete("/notification/" + id).then(res => {
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
      this.request.post("/notification/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    save() {
      this.request.post("/notification", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    reset() {
      if(this.isAdministrator){
        this.college=""
      }
      this.title = ""
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
    getLabel(list1, list2, id, value, label) {
      // 遍历第一个列表
      const itemInList1 = list1.find(item => item[value] === id);
      if (itemInList1) {
        return itemInList1[label];
      }
      // 遍历第二个列表
      const itemInList2 = list2.find(item => item[value] === id);
      if (itemInList2) {
        return itemInList2[label];
      }
      // 如果两个列表都找不到匹配的元素，返回 id
      return id;
    }
  }
}
</script>


