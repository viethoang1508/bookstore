import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

export type UserResponse = { id: string; fullName?: string; username?: string; email?: string; phone?: string; status?: string };
type UpdateProfileRequest = { fullName?: string; username?: string; phone?: string };

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const profileApi = {
  me: () => unwrap(apiRequest<BaseResponse<UserResponse>>({ path: "/users/me" })),
  update: (body: UpdateProfileRequest) => unwrap(apiRequest<BaseResponse<UserResponse>>({ path: "/users/update", method: "PUT", body })),
} as const;