import { apiRequest } from "@/lib/api-client";
import type { BaseResponse, BookSummary } from "@/types";

type PageResponse<T> = { content?: T[] };

async function unwrap<T>(promise: Promise<BaseResponse<T>>): Promise<T> {
  const response = await promise;
  return response.data;
}

export const booksApi = {
  list: () => unwrap(apiRequest<BaseResponse<PageResponse<BookSummary>>>({ path: "/public/catalog", method: "GET" })).then((d) => d.content ?? []),
} as const;