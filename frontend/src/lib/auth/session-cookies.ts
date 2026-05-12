"use client";

import {
  ACCESS_TOKEN_COOKIE,
  REFRESH_TOKEN_COOKIE,
  ROLE_COOKIE,
} from "@/lib/auth/constants";
import type { UserRole } from "@/types/auth";

const DEFAULT_MAX_AGE_SEC = 60 * 60 * 24 * 7; // 7 days; align with refresh TTL when integrated

function setCookie(name: string, value: string, maxAgeSec: number) {
  const secure = typeof window !== "undefined" && window.location.protocol === "https:";
  document.cookie = `${encodeURIComponent(name)}=${encodeURIComponent(value)}; Path=/; Max-Age=${maxAgeSec}; SameSite=Lax${secure ? "; Secure" : ""}`;
}

function deleteCookie(name: string) {
  document.cookie = `${encodeURIComponent(name)}=; Path=/; Max-Age=0; SameSite=Lax`;
}

export function setSessionCookies(params: {
  accessToken: string;
  refreshToken: string;
  role: UserRole;
  maxAgeSec?: number;
}) {
  const maxAge = params.maxAgeSec ?? DEFAULT_MAX_AGE_SEC;
  setCookie(ACCESS_TOKEN_COOKIE, params.accessToken, maxAge);
  setCookie(REFRESH_TOKEN_COOKIE, params.refreshToken, maxAge);
  setCookie(ROLE_COOKIE, params.role, maxAge);
}

export function clearSessionCookies() {
  deleteCookie(ACCESS_TOKEN_COOKIE);
  deleteCookie(REFRESH_TOKEN_COOKIE);
  deleteCookie(ROLE_COOKIE);
}
