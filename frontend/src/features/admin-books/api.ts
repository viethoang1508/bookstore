import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type PageResponse<T> = { content?: T[] };

export type AdminBook = {
  id: string;
  title: string;
  authorName?: string;
  price?: number;
  stock?: number;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminBooksApi = {
  list: () => unwrap(apiRequest<BaseResponse<PageResponse<AdminBook>>>({ path: "/admin/books" })).then((d) => d.content ?? []),
  delete: (id: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/books/${id}`, method: "DELETE" })),
} as const;