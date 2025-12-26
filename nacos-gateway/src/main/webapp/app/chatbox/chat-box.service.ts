import axios from 'axios';

import { type ChatMessage, type ChatResponse } from '@/shared/models/chat.types';

const baseApiUrl = '/agent/order';

export default class ChatService {
  chat(message: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/chatSimulation`, { message })
        .then(res => {
          console.log(res);
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  chatSimulation(request: ChatMessage): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/chatSimulation`, request)
        .then(res => {
          console.log(res);
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  queryOrder(message: ChatMessage): Promise<ChatResponse> {
    return new Promise<ChatResponse>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/query`, message)
        .then(res => {
          console.log(res);
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
