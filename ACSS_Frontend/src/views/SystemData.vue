<template>
  <div class="dashboard-container">
    <!-- 数据指标卡片 -->
    <el-row :gutter="24" class="metric-row">
      <el-col :xs="24" :sm="12" :md="6" class="metric-col">
        <el-card class="metric-card student-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="metric-info">
              <div class="metric-label">学生总数</div>
              <div class="metric-value">{{ value[0] }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" class="metric-col">
        <el-card class="metric-card teacher-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="metric-info">
              <div class="metric-label">教师总数</div>
              <div class="metric-value">{{ value[1] }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6" class="metric-col">
        <el-card class="metric-card class-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon">
              <i class="el-icon-s-promotion"></i>
            </div>
            <div class="metric-info">
              <div class="metric-label">班级总数</div>
              <div class="metric-value">{{ value[2] }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6" class="metric-col">
        <el-card class="metric-card course-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-icon">
              <i class="el-icon-s-order"></i>
            </div>
            <div class="metric-info">
              <div class="metric-label">课程总数</div>
              <div class="metric-value">{{ value[3] }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="chart-row">
      <el-col :xs="24" :md="10" class="chart-col">
        <el-card class="chart-card" shadow="never">
          <div ref="pieChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="14" class="chart-col">
        <el-card class="chart-card" shadow="never">
          <div class="filter-group">
            <el-select
              v-model="term"
              class="styled-select"
              placeholder="请选择学期"
              clearable
              @change="handleSelectChange"
            >
              <el-option
                v-for="item in terms"
                :key="item.name"
                :label="item.name"
                :value="item.value"
              />
            </el-select>

            <el-select
              v-model="grade"
              class="styled-select"
              placeholder="请选择年级"
              clearable
              @change="handleSelectChange"
            >
              <el-option
                v-for="item in grades"
                :key="item.name"
                :label="item.name"
                :value="item.value"
              />
            </el-select>
          </div>
          <div ref="barChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>


<script>
import * as echarts from 'echarts';
export default {
  name: "SystemData",
  data() {
    return {
      value: [],
      pieChart: null,
      barChart: null,
      term:"",
      terms:[],
      grade:"",
      grades:[]
    }
  },
  mounted() {
    this.request.get("/dict/term").then(res => {
      this.terms = res.data
    })
    this.request.get("/dict/grade").then(res => {
      this.grades = res.data
    })
    this.initEChart();
    this.loadPieData();
    this.loadBarData();
  },

  methods: {
    initEChart() {
      this.pieChart = echarts.init(this.$refs.pieChart);
      this.barChart = echarts.init(this.$refs.barChart);
    },

    loadPieData() {
      this.request.get("/echarts/count").then(res => {
        this.value = res.data.value;
      })
      this.request.get('/echarts/queryTypeCount')
          .then(response => {
            this.renderPieChart(response.data);
          })
          .catch(error => {
            console.error('请求失败', error);
          });
    },

    async loadBarData() {
      try {
        const res = await this.request.get('/echarts/queryCoursePlanProcess', {
          params: {
            term: this.term,
            gradeNo: this.grade,
          },
        });
        console.log(res.data);
        this.renderBarChart(res.data);
      } catch (error) {
        console.error('请求失败', error);
      }
    },
    handleSelectChange(){
      this.loadBarData()
    },
    renderPieChart(data) {
      const option = {
        title: {
          text: '系统字典数据',
          subtext: '不同 Type 的数量',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        series: [
          {
            name: '类型数量',
            type: 'pie',
            radius: '65%',
            data: data.map(item => ({
              name: item.type,
              value: item.type_count,
            })),
            roseType: 'radius',
            label: {
              show: true,
            },
          },
        ],

      };
      this.pieChart.setOption(option);
    },

    //堆叠图
    renderBarChart(data) {
      // 按学院分组数据
      const groupedData = {};
      data.forEach(item => {
        const collegeName = item.collegeName;
        const className = item.className;
        const status = item.appearanceStatus === '出现' ? '已安排课程计划' : '未安排课程计划';
        //按学院放入是否安排好课程计划的列表
        if (!groupedData[collegeName]) {
          groupedData[collegeName] = {
            '已安排课程计划': [],
            '未安排课程计划': [],
          };
        }
        groupedData[collegeName][status].push(className);
      });

      // 构造堆叠图数据
      const legendData = ['已安排课程计划', '未安排课程计划'];
      const xAxisData = Object.keys(groupedData);    //x轴数据
      const seriesData = legendData.map(status => ({
        name: status,
        type: 'bar',
        stack: '总量',
        data: xAxisData.map(college => {
          const total = groupedData[college]['已安排课程计划'].length + groupedData[college]['未安排课程计划'].length;
          const count = groupedData[college][status].length;
          const percentage = total === 0 ? 0 : (count / total) * 100;    //计算百分比
          return percentage.toFixed(2);
        }),
        barWidth : 50,//柱图宽度
      }));

      // 堆叠图配置
      const option = {
        title: {
          text: '各学院班级课程计划安排情况',
          subtext: '按学院分类',
          left: 'center',
        },
        tooltip: {   //鼠标悬浮时显示数据
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
          formatter: function(params) {
            const college = params[0].name;
            return params.map(param => {
              const status = param.seriesName;
              const count = param.value;
              const classList = groupedData[college][status].join('<br>');
              return `${status}: ${count}%<br>${classList}`;
            }).join('<br>');
          },
        },
        //按钮
        legend: {
          data: legendData,
          top: 50
        },
        //x轴
        xAxis: {
          type: 'category',
          axisTick: { show: false },
          axisLabel: {
            interval: 0, // 始终显示所有标签
            rotate: 45, // 标签旋转角度，根据需要调整
          },
          data: xAxisData,
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}%', // 设置纵坐标刻度的百分比格式
          },
        },
        series: seriesData,
        //x轴滚动条
        dataZoom: {
          show: true, // 为true 滚动条出现
          realtime: true, // 实时更新
          type:'slider', // 有type这个属性，滚动条在最下面，也可以不行，写y：36，这表示距离顶端36px，一般就是在图上面。
          height: 12, // 表示滚动条的高度，也就是粗细
          start: 0, // 表示默认展示0%～80%这一段。
          end: 80,
        },

        // 设置 grid 属性来调整图表大小
        grid: {
          top: '20%', // 调整左边距
          left: '10%', // 调整左边距
          right: '10%', // 调整右边距
          bottom: '10%', // 调整底边距
          containLabel: true,
        },
    };
      // 渲染堆叠图
      this.barChart.setOption(option);
    },
  },
};

</script>
<style scoped>
.dashboard-container {
  padding: 24px;
  background: #f5f7fa;
}

/* 指标卡片样式 */
.metric-row {
  margin-bottom: 24px;
}

.metric-col {
  margin-bottom: 16px;
  padding: 0 12px;
}

.metric-card {
  border-radius: 12px;
  border: none;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.metric-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.metric-icon i {
  font-size: 28px;
  color: white;
}

.student-card .metric-icon { background: linear-gradient(45deg, #409EFF, #5ab1ff); }
.teacher-card .metric-icon { background: linear-gradient(45deg, #67C23A, #85ce61); }
.class-card .metric-icon { background: linear-gradient(45deg, #303133, #606266); }
.course-card .metric-icon { background: linear-gradient(45deg, #909399, #a6a9ad); }

.metric-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

/* 图表区域样式 */
.chart-row {
  margin-top: 24px;
}

.chart-col {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 12px;
  background: white;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.05) !important;
}

.chart-container {
  height: 600px;
  margin-top: 20px;
}

.filter-group {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.styled-select {
  width: 240px;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .metric-content {
    flex-direction: column;
    text-align: center;
  }
  
  .metric-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
  
  .filter-group {
    flex-direction: column;
  }
  
  .styled-select {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .metric-value {
    font-size: 24px;
  }
  
  .chart-container {
    height: 400px;
  }
}

/* 动画效果 */
@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.metric-card {
  animation: scaleIn 0.6s ease;
}
</style>