import { defineComponent, ref, nextTick, onMounted, inject, type ComputedRef } from 'vue';
import { useI18n } from 'vue-i18n';
import type { ChatMessage } from '@/shared/models/chat.types';
import ChatService from './chat-box.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ChatBox',
  setup() {
    // const { t$ } = useI18n();

    const username = inject<ComputedRef<string>>('currentUsername');

    const messages = ref<ChatMessage[]>([
      {
        userId: 'assistant',
        queryText: '你好！我是订单客服AI助手，有什么可以帮助你的吗？',
      },
    ]);

    const inputMessage = ref('');
    const loading = ref(false);
    const messagesContainer = ref<HTMLElement | null>(null);
    const inputRef = ref<HTMLTextAreaElement | null>(null);
    const isLimitReached = ref(false);

    const MAX_QUESTIONS = 5;

    const getBrowserId = (): string => {
      const fingerprint = [
        navigator.userAgent,
        navigator.language,
        screen.width + 'x' + screen.height,
        new Date().getTimezoneOffset(),
      ].join('|');

      let storedId = localStorage.getItem('chat_browser_id');
      if (!storedId) {
        storedId = 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
        localStorage.setItem('chat_browser_id', storedId);
      }

      return storedId;
    };

    const getQuestionCount = (): number => {
      const browserId = getBrowserId();
      const count = localStorage.getItem(`chat_question_count_${browserId}`);
      return count ? parseInt(count, 10) : 0;
    };

    const incrementQuestionCount = (): void => {
      const browserId = getBrowserId();
      const currentCount = getQuestionCount();
      localStorage.setItem(`chat_question_count_${browserId}`, String(currentCount + 1));

      if (currentCount + 1 >= MAX_QUESTIONS) {
        isLimitReached.value = true;
      }
    };

    const checkLimit = (): boolean => {
      return getQuestionCount() >= MAX_QUESTIONS;
    };

    const scrollToBottom = async () => {
      await nextTick();
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
      }
    };

    const clearQuestionLimit = () => {
      const browserId = getBrowserId();
      localStorage.removeItem(`chat_question_count_${browserId}`);
      isLimitReached.value = false;
      console.log('cleared');
    };

    const sendMessage = async () => {
      const content = inputMessage.value.trim();
      if (!content || loading.value) return;

      if (checkLimit()) {
        messages.value.push({
          userId: 'assistant',
          queryText: '抱歉，您已达到最大提问次数限制（' + MAX_QUESTIONS + '次）',
        });
        isLimitReached.value = true;
        await nextTick();
        scrollToBottom();
        return;
      }

      messages.value.push({
        userId: username?.value,
        queryText: content,
      });

      inputMessage.value = '';
      await nextTick();
      await scrollToBottom();

      loading.value = true;

      incrementQuestionCount();

      const chatService = new ChatService();

      chatService
        .queryOrder({
          userId: username?.value,
          queryText: content,
        })
        .then(res => {
          messages.value.push({
            userId: 'assistant',
            queryText: res.replyContent || res || '很抱歉，我无法回答你的问题。',
          });
          loading.value = false;
          scrollToBottom();
        })
        .catch(err => {
          console.log(err);
          loading.value = false;
          scrollToBottom();
        });
    };

    onMounted(() => {
      scrollToBottom();
    });

    return {
      t$: useI18n().t,
      messages,
      inputMessage,
      loading,
      messagesContainer,
      inputRef,
      sendMessage,
      username,
      isLimitReached,
      MAX_QUESTIONS,
      clearQuestionLimit,
    };
  },
});
