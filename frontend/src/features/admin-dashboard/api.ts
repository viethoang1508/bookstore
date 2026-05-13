import { adminBooksApi } from "@/features/admin-books";
import { adminOrdersApi } from "@/features/admin-orders";
import { adminPromotionsApi } from "@/features/admin-promotions";
import { adminUsersApi } from "@/features/admin-users";
import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type Category = { id: string };

export const adminDashboardApi = {
  summary: async () => {
    const [adminBooks, categories, orders, promotions, me] = await Promise.all([
      adminBooksApi.list(),
      apiRequest<BaseResponse<Category[]>>({ path: "/public/catalog/categories", skipAuth: true }).then((r) => r.data),
      adminOrdersApi.list(),
      adminPromotionsApi.list(),
      adminUsersApi.me(),
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