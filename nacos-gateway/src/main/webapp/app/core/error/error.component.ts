import { type ComputedRef, type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
// import { useLoginModal } from '@/account/login-modal';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Error',
  setup() {
    // const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const errorMessage: Ref<string> = ref(null);
    const error403: Ref<boolean> = ref(false);
    const error404: Ref<boolean> = ref(false);
    const generalError: Ref<boolean> = ref(false);
    const route = useRoute();

    // Check for server-side error information in route query parameters
    const status = route.query.status;
    const error = route.query.error;
    const message = route.query.message;

    if (route.meta) {
      errorMessage.value = route.meta.errorMessage ?? null;
      error403.value = route.meta.error403 ?? false;
      error404.value = route.meta.error404 ?? false;
      generalError.value = route.meta.error ?? false;
      if (!authenticated.value && error403.value) {
        // showLogin();
      }
    }

    // Override with server-side error information if available
    if (status || error || message) {
      generalError.value = true;
      if (status === '403') {
        error403.value = true;
        generalError.value = false;
      } else if (status === '404') {
        error404.value = true;
        generalError.value = false;
      } else {
        errorMessage.value = message as string || error as string || `Error ${status}`;
      }
    }

    return {
      errorMessage,
      error403,
      error404,
      generalError,
      t$: useI18n().t,
    };
  },
});
