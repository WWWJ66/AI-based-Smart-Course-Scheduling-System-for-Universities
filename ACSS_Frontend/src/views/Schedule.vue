<template>
  <div class="page-container">
    <el-card shadow="hover" class="card-container">
      <el-space wrap size="large">
        <el-select v-model="term" placeholder="请选择学期" style="width: 200px">
          <el-option v-for="item in terms" :key="item.name" :label="item.name" :value="item.value" />
        </el-select>

        <el-select v-if="isAdministrator" v-model="college" placeholder="请选择学院" style="width: 200px" @change="handleSelectChange">
          <el-option v-for="item in colleges" :key="item.name" :label="item.name" :value="item.value" />
        </el-select>

        <el-select v-if="isAdministrator || isAdmin" v-model="grade" placeholder="请选择年级" style="width: 200px" @change="handleSelectChange">
          <el-option v-for="item in grades" :key="item.name" :label="item.name" :value="item.value" />
        </el-select>

        <el-select v-if="isAdministrator || isAdmin" v-model="classNo" placeholder="请选择班级" style="width: 200px">
          <el-option v-for="item in classInfos" :key="item.className" :label="item.className" :value="item.classNo" />
        </el-select>

        <el-button type="primary" icon="el-icon-search" @click="querySchedule">查询课表</el-button>
        <el-button type="warning" icon="el-icon-refresh" @click="reset">重置</el-button>
        <el-button v-if="isAdministrator || isAdmin" type="info" icon="el-icon-edit" @click="editSchedule">微调课表</el-button>
      </el-space>
    </el-card>

    <el-card shadow="hover" class="table-wrapper" style="margin-top: 20px">
      <div class="table-container">
        <table class="schedule-table">
          <thead>
            <tr>
              <th>时间</th>
              <th v-for="(weekNum, weekIndex) in courseTableData.courses.length" :key="weekIndex">
                {{ "周" + digitalToChinese(weekIndex + 1, "week") }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(lesson, lessonIndex) in courseTableData.lessons" :key="lessonIndex">
              <td>
                <p>{{ "第" + digitalToChinese(lessonIndex + 1) + "节" }}</p>
                <p class="period">{{ lesson }}</p>
              </td>
              <td v-for="(course, courseIndex) in courseTableData.courses" :key="courseIndex">
                {{ courseTableData.courses[courseIndex][lessonIndex] || "-" }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </el-card>

    <!-- 微调课表对话框 -->
    <el-dialog title="课表微调" :visible.sync="dialogFormVisible" width="30%">
      <el-form label-width="80px" size="small">
        <el-form-item label="课程名称">
          <el-select clearable v-model="form.id" placeholder="请选择" style="width: 100%" @change="handleFormSelectChange">
            <el-option v-for="item in courseData" :key="item.id" :label="item.courseName" :value="item.id">
              {{ `${item.courseName}  ${item.courseTime}` }}
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="上课时间">
          <el-select clearable v-model="form.courseTime" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in courseTimeList" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>

        <el-form-item label="上课教室">
          <el-select v-model="form.classroomNo" placeholder="请选择" style="width: 100%">
            <el-option v-for="item in classroomList" :key="item.classroomNo" :label="item.classroomName" :value="item.classroomNo" />
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


<script>
export default {
  name: "Schedule",
  data() {
    return {
      term:"",
      terms:[],
      college:"",
      colleges:[],
      grade:"",
      grades:[],
      classNo:"",
      classInfos:[],

      isTeacher:false,
      isStudent:false,
      isAdministrator:false,
      isAdmin:false,
      userRole:"",
      userCollege:"",
      userNo:"",

      courseTableData: {
        lessons: [
          "08.00-9.40",
          "10.00-11.50",
          "14.00-15.40",
          "16.00-17.50",
          "19.00-21.00",
        ],
        courses: [
          [],
          [],
          [],
          [],
          [],
        ],
      },

      dialogFormVisible: false,
      form: {},
      courseTimeList:[],
      classroomList:[],

      courseData:[]


    }
  },
  created() {
    //获取字典数据  term  college grade
    this.request.get("/dict/term").then(res => {
      this.terms = res.data
    })
    this.request.get("/dict/college").then(res => {
      this.colleges = res.data
    })
    this.request.get("/dict/grade").then(res => {
      this.grades = res.data
    })
  },

  async mounted() {
    let userInfo;
    const userString = localStorage.getItem("user");

    if (userString) {
      userInfo = JSON.parse(userString); // 解析本地存储的用户信息
      this.token = userInfo.token
      this.userNo=userInfo.userNo
      try {
        const res = await this.request.get("/user/getUserRoleAndCollege/" + userInfo.userNo);
        this.userCollege = res.data.college;
        this.userRole = res.data.role;

        if (this.userRole === "ROLE_ADMINISTRATOR") {
          this.isAdministrator = true
        }else if(this.userRole === "ROLE_ADMIN"){
          this.isAdmin = true
          this.college = this.userCollege;
        } else if(this.userRole === "ROLE_TEACHER"){
           this.isTeacher=true
        }else{
          this.isStudent = true
          const res = await this.request.get("/student/getClassNoByStudentNo/" + this.userNo);
          this.classNo=res.data
        }
      } catch (error) {
        console.error('请求失败', error);
      }
    }
  },

  methods: {
    //重置查询框
    reset() {
      this.term = ""
      if(this.isAdministrator){
        this. college= ""
      }
      if(this.isAdministrator||this.isAdmin){  //系统管理员和管理员有年级和班级的下拉框
        this. grade= ""
        this. classNo= ""
      }

      //清空表格数据
      this.courseTableData.courses.map((item, index) => {
        this.courseTableData.courses[index].splice(
            0,
            this.courseTableData.courses[index].length
        );
      });
    },

    querySchedule() {
      //这段代码的作用是清空 courseTableData.courses 数组中每个元素（每行）的内容，通过将每个行的数组使用 splice 方法清空，将其长度设置为 0。这样做的目的可能是在重新填充新的数据之前，先清空已有的数据，确保 courseTableData.courses 是一个空数组。
      this.courseTableData.courses.map((item, index) => {
        this.courseTableData.courses[index].splice(
            0,
            this.courseTableData.courses[index].length
        );
      });

       // 判断是否选择了 term 和 classNo
      if (this.userRole === "ROLE_ADMINISTRATOR" || this.userRole === "ROLE_ADMIN") {
        // 如果是管理员或系统管理员，需要判断两个条件
        if (!this.term || !this.classNo) {
          this.$message.error("请先选择学期和班级");
          return;
        }
      } else {
        // 如果是学生或老师，只需要判断学期
        if (!this.term) {
          this.$message.error("请先选择学期");
          return;
        }
      }

      // 老师的课表请求地址和其他角色不一样
      const getRequestUrl = (isTeacher) => {
        return isTeacher ? "http://localhost:8101/api/schedule/teacher" : "http://localhost:8101/api/schedule";
      };

      // 获取请求地址
      const requestUrl = getRequestUrl(this.isTeacher);

      // 根据用户类型动态构造请求参数   参数也不一样
      const getRequestParams = (isTeacher) => {
        return isTeacher ? { term: this.term, teacherNo: this.userNo } : { term: this.term, classNo: this.classNo };
      };

    // 获取请求参数
      const requestParams = getRequestParams(this.isTeacher);
      this.request.get(requestUrl, {
      params: requestParams})
          .then((res) => {
            this.courseData = res.data;
            let level = 0;
            let times = 0;
            for (let index = 0; index < this.courseData.length; index++) {
              //时间段计数器
              times = times + 1;
              //第index个数据
              const item = this.courseData[index];
              if (parseInt(item.courseTime) !== times) {
                // 如果课程时间不匹配当前时间，填充空白
                this.courseTableData.courses[level].push("");
                index = index - 1;  // 回退一步，重新处理当前数据
              } else {
                // 将课程信息添加到对应的时间段
                this.courseTableData.courses[level].push(
                    item.courseName  +" --"+(this.isTeacher ? item.className : item.teacherName) +"--"+  item.classroomName
                );
              }
              if (times % 5 == 0) {
                // 当处理了5个时间段后，切换到下一列
                level = level + 1;
              }
            }
            // 显示成功消息
            this.$message({ message: "查询成功", type: "success" });
          })
          .catch((error) => {
            // 处理Ajax请求失败的情况
            this.$message.error("查询失败",error);
          });
    },

    /**
     * 数字转中文
     * @param {Number} num 需要转换的数字
     * @param {String} identifier 标识符
     * @returns {String} 转换后的中文
     *
     * 考虑了一个标识符（identifier）用于指定特定的转换规则
     * 如果标识符是 "week" 并且数字是 0 或 7，则返回 "日"。
     否则，根据数字在 character 数组中找到相应的中文字符进行返回。
     */
    digitalToChinese(num, identifier) {
      // 定义中文字符数组
      const character = [
        "零",
        "一",
        "二",
        "三",
        "四",
        "五",
        // "六",
        // "七",
        // "八",
      ];

      // 根据标识符和数字进行特定的转换
      return identifier === "week" && (num === 0 || num === 7)
          ? "日"
          : character[num];
    },

    //用户为系统管理员或者管理员时选择年级，学院下拉框，获取班级信息
    handleSelectChange() {
      // 处理每个下拉框的变化
      this.performOperation();
    },
    performOperation() {
      // 若选择了年级和学院信息，查询班级信息
      if (this.grade && this.college) {
        this.request.get("/classInfo/gradeAndCollege", {
          params: {
            grade: this.grade,
            college: this.college
          }
        }).then(res => {
          this.classInfos = res.data
        })
      }
    },


    save() {
      this.request.post("/schedule", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          //修改成功之后重新查询课表信息
          this.querySchedule()
        } else {
          this.$message.error("保存失败")
        }
      })

    },

    handleFormSelectChange() {
      this.request.get("/schedule/courseTime/"+this.form.id).then(res => {
        this.courseTimeList = res.data
      })
      this.request.get("/schedule/classroom/"+this.form.id).then(res => {
        this.classroomList = res.data
      })
    },

    editSchedule(){
        if (this.courseData.length === 0) {
          this.$message.error('请先查询课程信息');
          return;
        }
        this.dialogFormVisible = true;
      }
  }
}
</script>

<style scoped>
.page-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.card-container {
  padding: 20px;
}

.table-wrapper {
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  background-color: white;
}

.schedule-table th,
.schedule-table td {
  border: 1px solid #ebeef5;
  padding: 12px;
  font-size: 14px;
}

.schedule-table thead {
  background-color: #f2f6fc;
  font-weight: bold;
}

.schedule-table tbody tr:hover {
  background-color: #f5f7fa;
}

.period {
  color: #999;
  font-size: 12px;
}

</style>

