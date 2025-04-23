<template>
  <div>
    <div class="container">
      <div class="title">订单统计分析</div>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/order-total.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ totalCount }}</div>
                <div class="user-info-title">订单总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/order-paid.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ paidCount }}</div>
                <div class="user-info-title">已支付订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/order-shipped.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ shippedCount }}</div>
                <div class="user-info-title">待收货订单</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/order-money.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ totalSales }}元</div>
                <div class="user-info-title">总销售额</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>订单状态分布</span>
            </div>
            <div id="pieChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>每日订单量</span>
            </div>
            <div id="lineChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mgb20">
        <el-col :span="24">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>销售额趋势</span>
            </div>
            <div id="barChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>热门商品购买量排行</span>
            </div>
            <div id="hotProductsChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>活跃用户排行</span>
            </div>
            <div id="activeUsersChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "OrdersStats",
  data() {
    return {
      totalCount: 0,
      paidCount: 0,
      shippedCount: 0,
      totalSales: 0,
      orderData: [],
      statusData: [],
      dailyOrders: [],
      salesTrend: [],
      hotProducts: [],
      activeUsers: []
    };
  },
  created() {
    this.getOrdersStats();
  },
  mounted() {
    // 页面渲染完成后初始化图表
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
  },
  methods: {
    getOrdersStats() {
      // 获取订单汇总数据
      this.$axios.post('/api/orders/selectPage', {
        pagesize: 9999,
        currentPage: 1
      }).then(res => {
        if (res.data.code == 200) {
          // 使用真实订单数据生成统计信息
          const ordersList = res.data.data.list || [];
          // 设置订单总数
          this.totalCount = ordersList.length;
          
          // 统计各种状态的订单数量和总销售额
          let paidCount = 0;
          let shippedCount = 0;
          let totalSales = 0;
          
          // 状态计数
          const statusCounts = {
            '01': 0, // 待支付
            '02': 0, // 待发货
            '03': 0, // 待收货
            '04': 0  // 已完成
          };
          
          // 每日订单量统计
          const dailyOrderMap = {};
          
          // 统计商品购买量和用户活跃度
          const productMap = {};
          const userMap = {};
          
          // 销售额趋势（按月）
          const monthMap = {};
          
          ordersList.forEach(order => {
            // 统计各状态订单
            if (statusCounts.hasOwnProperty(order.status)) {
              statusCounts[order.status]++;
            }
            
            // 统计已支付和待收货订单
            if (order.status === '02') {
              paidCount++;
            } else if (order.status === '03') {
              shippedCount++;
            }
            
            // 统计总销售额
            if (order.total) {
              totalSales += parseFloat(order.total);
            }
            
            // 统计日期数据
            if (order.createTime) {
              // 提取日期部分 (YYYY-MM-DD)
              const dateStr = order.createTime.split(' ')[0];
              dailyOrderMap[dateStr] = (dailyOrderMap[dateStr] || 0) + 1;
              
              // 提取月份 (YYYY-MM)
              const monthStr = dateStr.substring(0, 7);
              const orderAmount = order.total ? parseFloat(order.total) : 0;
              monthMap[monthStr] = (monthMap[monthStr] || 0) + orderAmount;
            }
            
            // 统计用户数据
            if (order.realname) {
              userMap[order.realname] = (userMap[order.realname] || 0) + 1;
            }
          });
          
          // 获取订单详情数据来统计热门商品
          this.$axios.post('/api/orderItem/queryAll', {
            pagesize: 9999,
            currentPage: 1
          }).then(itemRes => {
            if (itemRes.data.code == 200 && itemRes.data.data) {
              const orderItems = itemRes.data.data;
              
              // 统计商品销量
              orderItems.forEach(item => {
                if (item.goodsName) {
                  productMap[item.goodsName] = (productMap[item.goodsName] || 0) + (item.num || 1);
                }
              });
              
              // 填充热门商品数据
              this.hotProducts = Object.entries(productMap)
                .map(([name, value]) => ({ name, value }))
                .sort((a, b) => b.value - a.value)
                .slice(0, 5);
            }
            
            // 设置状态分布数据
            this.statusData = [
              { value: statusCounts['01'], name: '待支付' },
              { value: statusCounts['02'], name: '待发货' },
              { value: statusCounts['03'], name: '待收货' },
              { value: statusCounts['04'], name: '已完成' }
            ];
            
            // 设置日订单量数据
            const sortedDates = Object.keys(dailyOrderMap).sort();
            const last7Dates = sortedDates.slice(-7); // 最近7天
            
            this.dailyOrders = {
              dates: last7Dates.map(date => date.substring(5)), // 只显示MM-DD
              orders: last7Dates.map(date => dailyOrderMap[date])
            };
            
            // 设置月销售额趋势
            const sortedMonths = Object.keys(monthMap).sort();
            const last6Months = sortedMonths.slice(-6); // 最近6个月
            
            this.salesTrend = {
              months: last6Months.map(month => month.substring(5) + '月'), // 只显示MM月
              sales: last6Months.map(month => monthMap[month])
            };
            
            // 设置活跃用户排行
            this.activeUsers = Object.entries(userMap)
              .map(([name, value]) => ({ name, value }))
              .sort((a, b) => b.value - a.value)
              .slice(0, 5);
            
            // 更新统计数据
            this.paidCount = paidCount;
            this.shippedCount = shippedCount;
            this.totalSales = totalSales.toFixed(2);
            
            // 数据加载完成后初始化图表
            this.$nextTick(() => {
              this.initCharts();
            });
          }).catch(() => {
            this.$message.warning('获取订单项数据失败，部分统计可能不准确');
            this.initBaseCharts();
          });
        } else {
          this.$message.warning(res.data.msg);
          this.generateMinimalMockData();
        }
      }).catch(() => {
        this.$message.warning('获取订单数据失败，使用模拟数据');
        this.generateMinimalMockData();
      });
    },
    
    // 在未获取订单项数据的情况下初始化基础图表
    initBaseCharts() {
      this.hotProducts = [
        { name: '暂无数据', value: 0 }
      ];
      
      this.initCharts();
    },
    
    // 生成最小的模拟数据（当API完全不可用时使用）
    generateMinimalMockData() {
      this.totalCount = 0;
      this.paidCount = 0;
      this.shippedCount = 0;
      this.totalSales = 0;
      
      // 订单状态分布
      this.statusData = [
        { value: 0, name: '待支付' },
        { value: 0, name: '待发货' },
        { value: 0, name: '待收货' },
        { value: 0, name: '已完成' }
      ];
      
      // 每日订单量
      const dates = [];
      const orders = [];
      for (let i = 6; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        dates.push(date.getMonth() + 1 + '-' + date.getDate());
        orders.push(0);
      }
      this.dailyOrders = { dates, orders };
      
      // 销售额趋势
      const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
      const sales = months.map(() => 0);
      this.salesTrend = { months, sales };
      
      // 热门商品
      this.hotProducts = [
        { name: '暂无数据', value: 0 }
      ];
      
      // 活跃用户
      this.activeUsers = [
        { name: '暂无数据', value: 0 }
      ];
      
      // 使用模拟数据初始化图表
      this.$nextTick(() => {
        this.initCharts();
      });
    },
    
    initCharts() {
      this.initPieChart();
      this.initLineChart();
      this.initBarChart();
      this.initHotProductsChart();
      this.initActiveUsersChart();
    },
    
    initPieChart() {
      const pieChart = this.$echarts.init(document.getElementById('pieChart'));
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: this.statusData.map(item => item.name)
        },
        series: [
          {
            name: '订单状态',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.statusData
          }
        ]
      };
      pieChart.setOption(option);
      this.pieChart = pieChart;
    },
    
    initLineChart() {
      const lineChart = this.$echarts.init(document.getElementById('lineChart'));
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.dailyOrders.dates
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: this.dailyOrders.orders,
          type: 'line',
          areaStyle: {},
          smooth: true,
          itemStyle: {
            color: '#f3c3cc'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(243, 195, 204, 0.8)'
              }, {
                offset: 1, color: 'rgba(243, 195, 204, 0.1)'
              }]
            }
          }
        }]
      };
      lineChart.setOption(option);
      this.lineChart = lineChart;
    },
    
    initBarChart() {
      const barChart = this.$echarts.init(document.getElementById('barChart'));
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: this.salesTrend.months
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: this.salesTrend.sales,
          type: 'bar',
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: '#f3c3cc'
              }, {
                offset: 1, color: '#e889a1'
              }]
            },
            borderRadius: [4, 4, 0, 0]
          }
        }]
      };
      barChart.setOption(option);
      this.barChart = barChart;
    },
    
    initHotProductsChart() {
      const hotProductsChart = this.$echarts.init(document.getElementById('hotProductsChart'));
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01]
        },
        yAxis: {
          type: 'category',
          data: this.hotProducts.map(item => item.name)
        },
        series: [
          {
            type: 'bar',
            data: this.hotProducts.map(item => item.value),
            itemStyle: {
              color: '#f3c3cc'
            }
          }
        ]
      };
      hotProductsChart.setOption(option);
      this.hotProductsChart = hotProductsChart;
    },
    
    initActiveUsersChart() {
      const activeUsersChart = this.$echarts.init(document.getElementById('activeUsersChart'));
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          boundaryGap: [0, 0.01]
        },
        yAxis: {
          type: 'category',
          data: this.activeUsers.map(item => item.name)
        },
        series: [
          {
            type: 'bar',
            data: this.activeUsers.map(item => item.value),
            itemStyle: {
              color: '#f3c3cc'
            }
          }
        ]
      };
      activeUsersChart.setOption(option);
      this.activeUsersChart = activeUsersChart;
    },
    
    resizeCharts() {
      this.pieChart && this.pieChart.resize();
      this.lineChart && this.lineChart.resize();
      this.barChart && this.barChart.resize();
      this.hotProductsChart && this.hotProductsChart.resize();
      this.activeUsersChart && this.activeUsersChart.resize();
    }
  }
};
</script>

<style scoped>
.container {
  padding: 30px;
  background: #f0f2f5;
}

.title {
  font-size: 24px;
  color: #333;
  margin-bottom: 20px;
  font-weight: bold;
}

.mgb20 {
  margin-bottom: 20px;
}

.data-card {
  height: 100px;
  overflow: hidden;
  background: #fff;
  border-radius: 10px;
  color: #666;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 10px;
  height: 100%;
}

.user-avator {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-right: 20px;
}

.user-info-cont {
  flex: 1;
  overflow: hidden;
}

.user-info-name {
  font-size: 22px;
  color: #333;
  font-weight: bold;
}

.user-info-title {
  font-size: 14px;
  color: #999;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 20px;
}

.chart-header {
  padding: 12px 24px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

@media screen and (max-width: 1200px) {
  .el-col-6 {
    width: 50%;
  }
  
  .el-col-12 {
    width: 100%;
  }
}

@media screen and (max-width: 768px) {
  .el-col-6 {
    width: 100%;
  }
}
</style> 