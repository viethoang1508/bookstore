import { adminBooksApi } from "@/features/admin-books";
import { adminOrdersApi } from "@/features/admin-orders";
import { adminPromotionsApi } from "@/features/admin-promotions";
import { adminUsersApi } from "@/features/admin-users";
import { apiRequest, ApiError } from "@/lib/api-client";import type { BaseResponse } from "@/types";

type Category = { id: string };

async function fallbackOnApiError<T>(task: () => Promise<T>, fallback: T): Promise<T> {
  try {
    return await task();
  } catch (error) {
    if (error instanceof ApiError && [401, 403, 404].includes(error.status)) {
      return fallback;
    }
    throw error;
  }
}

export const adminDashboardApi = {
  summary: async () => {
    const [adminBooks, categories, orders, promotions, me] = await Promise.all([
      fallbackOnApiError(() => adminBooksApi.list(), []),
      fallbackOnApiError(
        () => apiRequest<BaseResponse<Category[]>>({ path: "/public/catalog/categories", skipAuth: true }).then((r) => r.data),
        []
      ),
      fallbackOnApiError(() => adminOrdersApi.list(), []),
      fallbackOnApiError(() => adminPromotionsApi.list(), []),
      fallbackOnApiError(() => adminUsersApi.me(), null),
    ]);

    return {
      books: adminBooks.length,
      categories: categories.length,
      orders: orders.length,
      promotions: promotions.length,
      users: me?.id ? 1 : 0,
    };
  },
  
} as const;