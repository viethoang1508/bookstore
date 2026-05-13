import { apiRequest } from "@/lib/api-client";
import type { BaseResponse, TokenResponse } from "@/types";

export type RegisterRequest = {
  username: string;
  email: string;
  password: string;
};

export type LoginRequest = {
  username: string;
  password: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const authApi = {
  register: (body: RegisterRequest) =>
    unwrap(
      apiRequest<BaseResponse<string>>({
        method: "POST",
        path: "/public/auth/register",
        skipAuth: true,
        body,
      }),
    ),
  login: (body: LoginRequest) =>
    unwrap(
      apiRequest<BaseResponse<TokenResponse>>({
        method: "POST",
        path: "/public/auth/login",
        skipAuth: true,
        body,
      }),
    ),
} as const;
