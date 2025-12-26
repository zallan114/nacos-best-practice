export interface ChatMessage {
  userId: string | undefined;
  queryText: string;
}

export interface ChatResponse {
  code: number;
  msg: string;
  replyContent: string;
}
