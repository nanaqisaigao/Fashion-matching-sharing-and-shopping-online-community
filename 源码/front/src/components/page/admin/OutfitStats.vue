<template>
  <div>
    <div class="container">
      <div class="title">穿搭信息统计分析</div>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/outfit-total.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ totalCount }}</div>
                <div class="user-info-title">穿搭总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/outfit-views.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ totalViews }}</div>
                <div class="user-info-title">总浏览量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/outfit-comments.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ totalComments }}</div>
                <div class="user-info-title">评论总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="mgb20 data-card">
            <div class="user-info">
              <img src="../../../assets/img/outfit-active.png" class="user-avator" alt="" />
              <div class="user-info-cont">
                <div class="user-info-name">{{ activeUsers }}</div>
                <div class="user-info-title">活跃用户数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>穿搭类型分布</span>
            </div>
            <div id="typeChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>季节偏好分布</span>
            </div>
            <div id="seasonChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mgb20">
        <el-col :span="24">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>每日穿搭分享数量趋势</span>
            </div>
            <div id="trendChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>热门穿搭排行榜（按浏览量）</span>
            </div>
            <div id="popularOutfitsChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>穿搭分享用户排行榜</span>
            </div>
            <div id="activeUsersChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 新增：关联商品分析 -->
      <el-row :gutter="20" class="mgb20">
        <el-col :span="24">
          <el-card shadow="hover" class="chart-card">
            <div class="chart-header">
              <span>关联商品统计分析</span>
            </div>
            <div id="relatedProductsChart" style="width: 100%; height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: "OutfitStats",
  data() {
    return {
      totalCount: 0,
      totalViews: 0,
      totalComments: 0,
      activeUsers: 0,
      typeData: [],
      seasonData: [],
      trendData: {},
      popularOutfits: [],
      topUsers: [],
      relatedProductsData: [],
      allProducts: []
    };
  },
  created() {
    this.getOutfitStats();
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts);
  },
  methods: {
    getOutfitStats() {
      // 首先获取所有商品数据
      this.$axios.post('/api/goods/frontPage', {
        pagesize: 1000,
        currentPage: 1
      }).then(goodsRes => {
        if (goodsRes.data.code == 200) {
          this.allProducts = goodsRes.data.data.list || [];
          
          // 然后获取穿搭统计数据
          this.$axios.post('/api/outfit/selectPage', {
            pagesize: 9999,
            currentPage: 1
          }).then(res => {
            if (res.data.code == 200) {
              // 使用真实穿搭数据生成统计信息
              const outfitsList = res.data.data.list || [];
              
              // 设置穿搭总数
              this.totalCount = outfitsList.length;
              
              // 计算总浏览量
              let totalViews = 0;
              
              // 类型统计
              const typeMap = {};
              
              // 季节统计
              const seasonMap = {};
              
              // 日期统计
              const dateMap = {};
              
              // 用户统计
              const userMap = {};
              
              // 热门穿搭统计
              const popularOutfitsMap = {};
              
              // 新增：关联商品统计
              const productRelationMap = {}; // 商品ID -> 被关联次数
              const productViewsMap = {}; // 商品ID -> 累计浏览量
              
              outfitsList.forEach(outfit => {
                // 累计浏览量
                if (outfit.num) {
                  totalViews += parseInt(outfit.num);
                }
                
                // 按类型分类
                if (outfit.type) {
                  typeMap[outfit.type] = (typeMap[outfit.type] || 0) + 1;
                }
                
                // 按季节分类
                if (outfit.season) {
                  seasonMap[outfit.season] = (seasonMap[outfit.season] || 0) + 1;
                }
                
                // 按日期统计发布量
                if (outfit.createTime) {
                  // 提取日期部分 (YYYY-MM-DD)
                  const dateStr = outfit.createTime.split(' ')[0];
                  dateMap[dateStr] = (dateMap[dateStr] || 0) + 1;
                }
                
                // 统计用户发布数量
                if (outfit.realname) {
                  userMap[outfit.realname] = (userMap[outfit.realname] || 0) + 1;
                }
                
                // 热门穿搭
                if (outfit.name && outfit.num) {
                  popularOutfitsMap[outfit.name] = parseInt(outfit.num);
                }
                
                // 新增：统计关联商品数据
                if (outfit.productIds && outfit.productIds !== '[]') {
                  try {
                    const productIds = JSON.parse(outfit.productIds);
                    const outfitViews = parseInt(outfit.num) || 0;
                    
                    productIds.forEach(productId => {
                      // 增加商品被关联次数
                      productRelationMap[productId] = (productRelationMap[productId] || 0) + 1;
                      
                      // 累计关联穿搭的浏览量
                      productViewsMap[productId] = (productViewsMap[productId] || 0) + outfitViews;
                    });
                  } catch (e) {
                    console.error('解析商品ID失败:', e);
                  }
                }
              });
              
              // 获取穿搭评论数据来统计评论总数
              this.$axios.post('/api/discuss/selectPage', {
                pagesize: 9999,
                currentPage: 1
              }).then(discussRes => {
                if (discussRes.data.code == 200 && discussRes.data.data) {
                  // 评论总数
                  this.totalComments = discussRes.data.data.total || discussRes.data.data.list.length || 0;
                } else {
                  this.totalComments = 0;
                }
                
                // 设置活跃用户数
                this.activeUsers = Object.keys(userMap).length;
                
                // 设置总浏览量
                this.totalViews = totalViews;
                
                // 设置类型分布数据
                this.typeData = Object.entries(typeMap)
                  .map(([name, value]) => ({ name, value }))
                  .sort((a, b) => b.value - a.value);
                
                // 设置季节分布数据
                this.seasonData = Object.entries(seasonMap)
                  .map(([name, value]) => ({ name, value }));
                
                // 设置日期趋势数据
                const sortedDates = Object.keys(dateMap).sort();
                const last7Dates = sortedDates.slice(-7); // 最近7天
                
                this.trendData = {
                  dates: last7Dates.map(date => date.substring(5)), // 只显示MM-DD
                  outfits: last7Dates.map(date => dateMap[date])
                };
                
                // 设置热门穿搭排行
                this.popularOutfits = Object.entries(popularOutfitsMap)
                  .map(([name, value]) => ({ name, value }))
                  .sort((a, b) => b.value - a.value)
                  .slice(0, 5);
                
                // 设置用户排行
                this.topUsers = Object.entries(userMap)
                  .map(([name, value]) => ({ name, value }))
                  .sort((a, b) => b.value - a.value)
                  .slice(0, 5);
                
                // 新增：设置关联商品数据
                // 将商品ID转换为商品名称，并按关联次数排序
                const productEntries = Object.entries(productRelationMap);
                const sortedProducts = productEntries
                  .sort((a, b) => b[1] - a[1])
                  .slice(0, 10); // 取前10个最多关联的商品
                
                  this.relatedProductsData = sortedProducts.map(([productId, count]) => {
                  // 转换为字符串进行比较，确保类型匹配
                  const product = this.allProducts.find(p => String(p.id) === String(productId)) || {};
                  const views = productViewsMap[productId] || 0;
                  
                  // 添加日志以便调试
                  if (!product.name) {
                    console.log('未找到商品:', productId, '可用商品IDs:', this.allProducts.map(p => p.id));
                  }
                  
                  return {
                    productId,
                    name: product.name || `商品${productId}`,
                    relationCount: count,
                    views: views,
                    price: product.money || 0
                  };
                });
                
                // 数据加载完成后初始化图表
                this.$nextTick(() => {
                  this.initCharts();
                });
              }).catch(() => {
                this.$message.warning('获取评论数据失败，部分统计可能不准确');
                this.totalComments = 0;
                this.initBaseCharts();
              });
            } else {
              this.$message.warning(res.data.msg);
              this.generateMinimalMockData();
            }
          }).catch(() => {
            this.$message.warning('获取穿搭数据失败，使用模拟数据');
            this.generateMinimalMockData();
          });
        } else {
          this.$message.warning(goodsRes.data.msg);
          this.generateMinimalMockData();
        }
      }).catch(() => {
        this.$message.warning('获取商品数据失败，使用模拟数据');
        this.generateMinimalMockData();
      });
    },
    
    // 在未获取完整数据的情况下初始化基础图表
    initBaseCharts() {
      // 数据加载完成后初始化图表
      this.$nextTick(() => {
        this.initCharts();
      });
    },
    
    // 生成最小的模拟数据（当API完全不可用时使用）
    generateMinimalMockData() {
      this.totalCount = 0;
      this.totalViews = 0;
      this.totalComments = 0;
      this.activeUsers = 0;
      
      // 穿搭类型分布
      this.typeData = [
        { value: 0, name: '暂无数据' }
      ];
      
      // 季节偏好分布
      this.seasonData = [
        { value: 0, name: '春季' },
        { value: 0, name: '夏季' },
        { value: 0, name: '秋季' },
        { value: 0, name: '冬季' }
      ];
      
      // 每日穿搭分享趋势
      const dates = [];
      const outfits = [];
      for (let i = 6; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        dates.push(date.getMonth() + 1 + '-' + date.getDate());
        outfits.push(0);
      }
      this.trendData = { dates, outfits };
      
      // 热门穿搭排行
      this.popularOutfits = [
        { name: '暂无数据', value: 0 }
      ];
      
      // 活跃用户排行
      this.topUsers = [
        { name: '暂无数据', value: 0 }
      ];
      
      // 使用模拟数据初始化图表
      this.$nextTick(() => {
        this.initCharts();
      });
    },
    
    initCharts() {
      this.initTypeChart();
      this.initSeasonChart();
      this.initTrendChart();
      this.initPopularOutfitsChart();
      this.initActiveUsersChart();
      this.initRelatedProductsChart();
    },
    
    initTypeChart() {
      const typeChart = this.$echarts.init(document.getElementById('typeChart'));
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: this.typeData.map(item => item.name)
        },
        series: [
          {
            name: '穿搭类型',
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
            data: this.typeData,
            color: ['#f3c3cc', '#e889a1', '#d65c84', '#c42b62', '#ab144d']
          }
        ]
      };
      typeChart.setOption(option);
      this.typeChart = typeChart;
    },
    
    initSeasonChart() {
      const seasonChart = this.$echarts.init(document.getElementById('seasonChart'));
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: this.seasonData.map(item => item.name)
        },
        series: [
          {
            name: '季节偏好',
            type: 'pie',
            radius: '55%',
            center: ['40%', '50%'],
            roseType: 'radius',
            itemStyle: {
              borderRadius: 8
            },
            data: this.seasonData,
            color: ['#95ddb2', '#ffb3ba', '#ffdfba', '#bacdff']
          }
        ]
      };
      seasonChart.setOption(option);
      this.seasonChart = seasonChart;
    },
    
    initTrendChart() {
      const trendChart = this.$echarts.init(document.getElementById('trendChart'));
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.trendData.dates
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          data: this.trendData.outfits,
          type: 'line',
          areaStyle: {},
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
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
      trendChart.setOption(option);
      this.trendChart = trendChart;
    },
    
    initPopularOutfitsChart() {
      const popularOutfitsChart = this.$echarts.init(document.getElementById('popularOutfitsChart'));
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
          data: this.popularOutfits.map(item => item.name)
        },
        series: [
          {
            type: 'bar',
            data: this.popularOutfits.map(item => item.value),
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 1,
                y2: 0,
                colorStops: [{
                  offset: 0, color: '#f3c3cc'
                }, {
                  offset: 1, color: '#e889a1'
                }]
              },
              borderRadius: [0, 4, 4, 0]
            }
          }
        ]
      };
      popularOutfitsChart.setOption(option);
      this.popularOutfitsChart = popularOutfitsChart;
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
          data: this.topUsers.map(item => item.name)
        },
        series: [
          {
            type: 'bar',
            data: this.topUsers.map(item => item.value),
            itemStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 1,
                y2: 0,
                colorStops: [{
                  offset: 0, color: '#95ddb2'
                }, {
                  offset: 1, color: '#56b881'
                }]
              },
              borderRadius: [0, 4, 4, 0]
            }
          }
        ]
      };
      activeUsersChart.setOption(option);
      this.activeUsersChart = activeUsersChart;
    },
    
    initRelatedProductsChart() {
      const relatedProductsChart = this.$echarts.init(document.getElementById('relatedProductsChart'));
      
      // 提取数据
      const productNames = this.relatedProductsData.map(item => item.name);
      const relationCounts = this.relatedProductsData.map(item => item.relationCount);
      const viewsCounts = this.relatedProductsData.map(item => item.views);
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            const item = params[0];
            const dataIndex = item.dataIndex;
            const product = this.relatedProductsData[dataIndex];
            return `
              <div>
                <b>${product.name}</b><br/>
                关联次数: ${product.relationCount}<br/>
                累计浏览量: ${product.views}<br/>
                商品价格: ¥${product.price}
              </div>
            `;
          }.bind(this)
        },
        legend: {
          data: ['关联次数', '累计浏览量'],
          right: 10,
          top: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'value',
            name: '次数',
            position: 'bottom',
            axisLine: {
              show: true
            },
            axisLabel: {
              formatter: '{value}'
            }
          }
        ],
        yAxis: [
          {
            type: 'category',
            inverse: true,
            data: productNames,
            axisLine: {
              show: true
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              formatter: function(value) {
                if (value.length > 10) {
                  return value.substring(0, 10) + '...';
                }
                return value;
              }
            }
          }
        ],
        series: [
          {
            name: '关联次数',
            type: 'bar',
            data: relationCounts,
            itemStyle: {
              color: '#f3c3cc'
            },
            label: {
              show: true,
              position: 'right'
            }
          },
          {
            name: '累计浏览量',
            type: 'bar',
            data: viewsCounts,
            itemStyle: {
              color: '#95ddb2'
            },
            label: {
              show: true,
              position: 'right'
            }
          }
        ]
      };
      
      relatedProductsChart.setOption(option);
      this.relatedProductsChart = relatedProductsChart;
    },
    
    resizeCharts() {
      this.typeChart && this.typeChart.resize();
      this.seasonChart && this.seasonChart.resize();
      this.trendChart && this.trendChart.resize();
      this.popularOutfitsChart && this.popularOutfitsChart.resize();
      this.activeUsersChart && this.activeUsersChart.resize();
      this.relatedProductsChart && this.relatedProductsChart.resize();
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