<template>
  <div class="home row">
    <div class="col-md-3 align-center">
      <span class="margin-top">AI Customer Order Service</span>

      <h1 class="display-4" v-text="t$('home.title')"></h1>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username" v-text="t$('home.logged.message', { username })"></span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span></span>
          <a class="alert-link" @click="showLogin()" v-text="t$('global.messages.info.authenticated.link')"></a
          ><span>, let's try the AI agent chat-box...</span>
        </div>
      </div>

      <div class="text-left">
        Backend DB data:

        <br />
        <br />
        you can login with username:
        <br />
        <b>user001</b> or <b>user002</b>, password: <b>123456</b> <br />
        <br />
        <div v-for="(item, index) in dbData" :key="item.id">{{ index + 1 }}. {{ item }}</div>

        <br />
        <br />
        <span class="text-info small">
          阿里千问不是免费的，
          <br />
          本地云资源不够自己搭建ollama,
          <br />这里每个浏览器可以问5次， <br />超过5次先禁用吧。
        </span>
      </div>
    </div>
    <div class="col-md-9">
      <chat-box v-if="authenticated"></chat-box>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

<style scoped></style>
