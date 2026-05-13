import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type AdminPromotion = {
  id: string;
  code: string;
  name?: string;
  scope?: string;
  type?: string;
  value?: number;
  maxDiscount?: number;
  minOrderValue?: number;
  status?: string;
  startTime?: string;
  endTime?: string;
};

export type PromotionPayload = {
  code: string;
  name: string;
  scope: string;
  type: "PERCENT" | "FIXED";
  value: number;
  maxDiscount?: number;
  minOrderValue?: number;
  startTime: string;
  endTime: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminPromotionsApi = {
  list: () => unwrap(apiRequest<BaseResponse<AdminPromotion[]>>({ path: "/admin/promotions" })),
  create: (payload: PromotionPayload) => unwrap(apiRequest<BaseResponse<AdminPromotion>>({ path: "/admin/promotions", method: "POST", body: payload })),
  update: (id: string, payload: Partial<PromotionPayload>) =>
    unwrap(apiRequest<BaseResponse<AdminPromotion>>({ path: `/admin/promotions/${id}`, method: "PUT", body: payload })),
  assignBooks: (id: string, bookIds: string[]) =>
    unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/promotions/${id}/books`, method: "POST", body: { bookIds } })),
  delete: (id: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/promotions/${id}`, method: "DELETE" })),
  changeStatus: (id: string, status: string) =>
    unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/promotions/${id}/status`, method: "PATCH", query: { status } })),
} as const;