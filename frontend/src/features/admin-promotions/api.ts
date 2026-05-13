import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type AdminPromotion = {
  id: string;
  code: string;
  name?: string;
  type?: string;
  value?: number;
  status?: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminPromotionsApi = {
  list: () => unwrap(apiRequest<BaseResponse<AdminPromotion[]>>({ path: "/admin/promotions" })),
  delete: (id: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/promotions/${id}`, method: "DELETE" })),
  changeStatus: (id: string, status: string) =>
    unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/promotions/${id}/status`, method: "PATCH", query: { status } })),
} as const;