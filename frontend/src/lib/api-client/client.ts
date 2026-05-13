import { getApiGatewayBaseUrl } from "@/lib/api-client/config";
import { ApiError } from "@/lib/api-client/errors";
import { ACCESS_TOKEN_COOKIE } from "@/lib/auth/constants";
import { readClientCookie } from "@/lib/auth/read-cookie-client";

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export type ApiRequestOptions = {
  method?: HttpMethod;
  path: string;
  query?: Record<string, string | number | boolean | undefined>;
  body?: unknown;
  headers?: Record<string, string>;
  accessToken?: string | null;
  signal?: AbortSignal;
};

function buildUrl(path: string, query?: ApiRequestOptions["query"]): string {
  const base = getApiGatewayBaseUrl();
  const normalizedPath = `${base}${path.startsWith("/") ? path : `/${path}`}`;
  const url = base.startsWith("/")
    ? new URL(normalizedPath, typeof window === "undefined" ? "http://localhost" : window.location.origin)
    : new URL(normalizedPath);
  if (query) {
    for (const [k, v] of Object.entries(query)) {
      if (v === undefined) continue;
      url.searchParams.set(k, String(v));
    }
  }
  return base.startsWith("/") ? `${url.pathname}${url.search}` : url.toString();
}

function getCookieAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return readClientCookie(ACCESS_TOKEN_COOKIE);
}

export async function apiRequest<T>(opts: ApiRequestOptions): Promise<T> {
  const method = opts.method ?? "GET";
  const url = buildUrl(opts.path, opts.query);

  const headers: Record<string, string> = {
    Accept: "application/json",
    ...opts.headers,
  };

  const hasBody = opts.body !== undefined && method !== "GET";
  if (hasBody && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json";
  }

  const token = opts.accessToken ?? getCookieAccessToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(url, {
    method,
    headers,
    body: hasBody ? JSON.stringify(opts.body) : undefined,
    signal: opts.signal,
    cache: "no-store",
  });

  const contentType = res.headers.get("content-type") ?? "";
  const isJson = contentType.includes("application/json");
  const parsed = isJson ? await res.json().catch(() => null) : await res.text().catch(() => null);

  if (!res.ok) {
    const message =
      typeof parsed === "object" && parsed && "message" in parsed && typeof (parsed as { message: unknown }).message === "string"
        ? (parsed as { message: string }).message
        : res.statusText || "Request failed";
    throw new ApiError(message, res.status, parsed);
  }

  return parsed as T;
}
