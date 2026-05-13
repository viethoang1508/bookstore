import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type PlaceOrderRequest = { addressId: string; items: Array<{ bookId: string; quantity: number }>; promotionCode?: string };

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> { const response = await promise; return response.data; }

export const checkoutApi = {
  preview: (body: PlaceOrderRequest) => unwrap(apiRequest<BaseResponse<unknown>>({ method: "POST", path: "/users/orders/checkout-preview", body })),
  place: (body: PlaceOrderRequest) => unwrap(apiRequest<BaseResponse<unknown>>({ method: "POST", path: "/users/orders/place", body })),
} as const;