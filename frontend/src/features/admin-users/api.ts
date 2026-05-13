import { profileApi } from "@/features/profile";
import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type CreateAdminPayload = {
  email: string;
  fullName?: string;
  username: string;
  password: string;
  phone?: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminUsersApi = {
  me: () => profileApi.me(),
  createAdmin: (payload: CreateAdminPayload) =>
    unwrap(apiRequest<BaseResponse<string>>({ path: "/admin/users/admin", method: "POST", body: payload })),
} as const;