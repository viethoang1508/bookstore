import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type CartResponse = { id: string; items?: Array<{ bookId: string; quantity: number }> };
async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const cartApi = {
  get: () => unwrap(apiRequest<BaseResponse<CartResponse>>({ path: "/users/cart" })),
} as const;