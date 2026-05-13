import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type OrderSummary = { orderId: string; finalAmount?: number; status?: string; items?: Array<{ bookId: string; quantity: number; unitPrice?: number }> };

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const ordersApi = {
  list: () => unwrap(apiRequest<BaseResponse<OrderSummary[]>>({ path: "/users/orders" })),
  getById: (id: string) => unwrap(apiRequest<BaseResponse<OrderSummary>>({ path: `/users/orders/${id}` })),
} as const;