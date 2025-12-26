import { type ComputedRef, defineComponent, inject, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';
import { useRouter } from 'vue-router';
import ChatBox from '@/chatbox/chat-box.vue';

import { useLoginModal } from '@/account/login-modal';

export default defineComponent({
  compatConfig: { MODE: 3 },
  components: {
    'chat-box': ChatBox,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');
    const router = useRouter();
    const dbData = ref<any[]>(['user001 三笔订单', 'user002 1笔订单']);

    const showComingSoon = ref(false);

    const toggleComingSoon = () => {
      showComingSoon.value = !showComingSoon.value;
    };

    // 收集用户访问信息
    const collectVisitorInfo = async () => {
      try {
        const userAgent = navigator.userAgent;

        const deviceInfo = {
          platform: navigator.platform,
          language: navigator.language,
          screen: `${window.screen.width}x${window.screen.height}`,
          colorDepth: window.screen.colorDepth,
        };

        const userAgentData = {
          loginTime: new Date().toISOString(),
          userAgent: userAgent,
        };

        const locationData = {
          location: document.referrer,
          authenticated: authenticated?.value || false,
          username: username?.value || 'anonymous',
        };

        const sessionData = {
          deviceInfo: JSON.stringify(deviceInfo),
          userAgent: JSON.stringify(userAgentData),
          location: JSON.stringify(locationData),
        };

        await axios.post('api/dian-user-sessions', sessionData);
      } catch (error) {}
    };

    // 检查用户是否已登录，如果已登录则重定向到登录后主页
    onMounted(() => {
      // if (authenticated?.value) {
      //   router.push('/dashboard');
      // }
      collectVisitorInfo();
    });

    return {
      authenticated,
      username,
      showLogin,
      t$: useI18n().t,
      showComingSoon,
      toggleComingSoon,
      dbData,
    };
  },
});
