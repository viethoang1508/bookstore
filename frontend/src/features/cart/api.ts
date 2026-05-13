import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type CartResponse = { cartId: string; items?: Array<{ bookId: string; bookName?: string; image?: string; price?: number; quantity: number }> };
async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const cartApi = {
  get: () => unwrap(apiRequest<BaseResponse<CartResponse>>({ path: "/users/cart" })),
addItem: (bookId: string, quantity: number) => unwrap(apiRequest<BaseResponse<void>>({ path: "/users/cart/items", method: "POST", body: { bookId, quantity } })),
  updateItem: (bookId: string, quantity: number) => unwrap(apiRequest<BaseResponse<void>>({ path: `/users/cart/items/${bookId}`, method: "PUT", body: { quantity } })),
  deleteItem: (bookId: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/users/cart/items/${bookId}`, method: "DELETE" })),
} as const;