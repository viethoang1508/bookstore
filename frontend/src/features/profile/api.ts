import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type UserResponse = { id: string; firstName?: string; lastName?: string; email?: string };

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const profileApi = {
  me: () => unwrap(apiRequest<BaseResponse<UserResponse>>({ path: "/users/me" })),
} as const;