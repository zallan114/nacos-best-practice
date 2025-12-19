<template>
  <div id="app">
    <div class="container">
    <h1>API 接口列表</h1>
    <div class="api-list">
      <div v-for="api in apiList" :key="api.url" class="api-item">
        <div class="api-method" :class="api.method.toUpperCase()">{{ api.method }}</div>
        <div class="api-url">{{ api.url }}</div>
        <div class="api-description">{{ api.description }}</div>
      </div>
    </div>
  </div>
  </div>
</template>

<script lang="ts" src="./app.component.ts"></script>

<style scoped>
.api-item {
  display: flex;
  align-items: center;
 
  margin-bottom: 10px;
}
.api-method {
  width: 80px;
  text-align: center;
  padding: 5px;
  border-radius: 5px;
  color: #fff;
  margin-right: 15px;
}
.api-method.GET {
  background-color: #28a745;
}
.api-method.POST {
  background-color: #007bff;
}
.api-method.PUT {
  background-color: #ffc107;
}
.api-method.DELETE {
  background-color: #dc3545;
}
.api-url {
  flex: 1;
  padding: 5px;
}
.api-description {
  flex: 2;
  padding: 5px;
}
</style>

