import { defineComponent, provide, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { storeToRefs } from 'pinia';


import { useAlertService } from '@/shared/alert/alert.service';
import '@/shared/config/dayjs';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'App',
  components: {

  },
  setup() {
    provide('alertService', useAlertService());

    const apiList = ref([
        {
          method: 'GET',
          url: '/api/users',
          description: '获取用户列表'
        },
        {
          method: 'GET',
          url: '/api/users/:id',
          description: '获取单个用户信息'
        },
        {
          method: 'POST',
          url: '/api/users',
          description: '创建新用户'
        },
        {
          method: 'PUT',
          url: '/api/users/:id',
          description: '更新用户信息'
        },
        {
          method: 'DELETE',
          url: '/api/users/:id',
          description: '删除用户'
        },
        {
          method: 'GET',
          url: '/api/products',
          description: '获取产品列表'
        },
        {
          method: 'GET',
          url: '/api/products/:id',
          description: '获取单个产品信息'
        }
      ]);


    return {
      apiList,
      t$: useI18n().t,
    };
  },
});
