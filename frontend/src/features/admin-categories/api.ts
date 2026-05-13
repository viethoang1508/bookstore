import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type AdminCategory = {
  id: string;
  name: string;
  slug: string;
};

export type CategoryPayload = {
  name: string;
  slug?: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminCategoriesApi = {
  list: () => unwrap(apiRequest<BaseResponse<AdminCategory[]>>({ path: "/public/catalog/categories", skipAuth: true })),
  create: (payload: CategoryPayload) => unwrap(apiRequest<BaseResponse<AdminCategory>>({ path: "/admin/categories", method: "POST", body: payload })),
  update: (id: string, payload: CategoryPayload) => unwrap(apiRequest<BaseResponse<AdminCategory>>({ path: `/admin/categories/${id}`, method: "PUT", body: payload })),
  delete: (id: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/categories/${id}`, method: "DELETE" })),
} as const;