import { apiRequest } from "@/lib/api-client";
import type { BaseResponse, OrderSummary } from "@/types";

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const ordersApi = {
  list: () => unwrap(apiRequest<BaseResponse<OrderSummary[]>>({ path: "/users/orders" })),
} as const;