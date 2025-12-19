<template>
  <div>
    <div class="row">
      <div class="col-md-3">
 
      </div>
      <div class="col-md-9">
        <h1 v-text="t$('error.title')"></h1>

        <div v-if="errorMessage">
          <div class="alert alert-danger">{{ errorMessage }}</div>
        </div>
        <div v-if="error403" class="alert alert-danger" v-text="t$('error.http.403')"></div>
        <div v-if="error404" class="alert alert-warning" v-text="t$('error.http.404')"></div>
        <div v-if="generalError" class="alert alert-danger">
          <h3>{{ t$('error.general.title') }}</h3>
          <p>{{ errorMessage || t$('error.general.message') }}</p>
        </div>
        <div v-if="!errorMessage && !error403 && !error404 && !generalError" class="alert alert-info">
          {{ t$('error.unknown') }}
        </div>
        <div class="mt-4">
          <router-link to="/" class="btn btn-primary">{{ t$('error.backToHome') }}</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./error.component.ts"></script>
