import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type AdminOrder = {
  orderId: string;
  finalAmount?: number;
  status?: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminOrdersApi = {
  list: () => unwrap(apiRequest<BaseResponse<AdminOrder[]>>({ path: "/admin/orders" })),
  updateStatus: (orderId: string, status: string) =>
    unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/orders/${orderId}/status`, method: "PUT", query: { status } })),
} as const;