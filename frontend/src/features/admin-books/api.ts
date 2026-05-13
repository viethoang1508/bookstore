import { apiRequest } from "@/lib/api-client";
import type { BaseResponse } from "@/types";

type PageResponse<T> = { content?: T[] };

export type AdminBook = {
  id: string;
  title: string;
  slug?: string;
  isbn?: string;
  description?: string;
  authorName?: string;
  publisherName?: string;
  publishYear?: number;
  price?: number;
  stock?: number;
  thumbnailUrl?: string;
};

export type BookPayload = {
  title: string;
  slug?: string;
  isbn?: string;
  description?: string;
  authorName?: string;
  publisherName?: string;
  publishYear?: number;
  price: number;
  stock: number;
  thumbnailUrl?: string;
};

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const adminBooksApi = {
  list: () => unwrap(apiRequest<BaseResponse<PageResponse<AdminBook>>>({ path: "/admin/books" })).then((d) => d.content ?? []),
  getById: (id: string) => unwrap(apiRequest<BaseResponse<AdminBook>>({ path: `/admin/books/${id}` })),
  create: (payload: BookPayload) => unwrap(apiRequest<BaseResponse<AdminBook>>({ path: "/admin/books", method: "POST", body: payload })),
  update: (id: string, payload: Partial<BookPayload>) => unwrap(apiRequest<BaseResponse<AdminBook>>({ path: `/admin/books/${id}`, method: "PUT", body: payload })),
  delete: (id: string) => unwrap(apiRequest<BaseResponse<void>>({ path: `/admin/books/${id}`, method: "DELETE" })),
} as const;