import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

import { ACCESS_TOKEN_COOKIE, ROLE_COOKIE } from "@/lib/auth/constants";
import { decodeJwtPayload, extractRoleFromPayload } from "@/lib/auth/jwt";
import type { UserRole } from "@/types/auth";

function resolveRole(accessToken: string | undefined, roleCookie: string | undefined): UserRole | null {
  if (accessToken) {
    const payload = decodeJwtPayload(accessToken);
    if (payload) {
      const fromJwt = extractRoleFromPayload(payload);
      if (fromJwt) return fromJwt;
    }
  }
  if (roleCookie === "ADMIN" || roleCookie === "CUSTOMER") return roleCookie;
  return null;
}

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  const access = request.cookies.get(ACCESS_TOKEN_COOKIE)?.value;
  const roleCookie = request.cookies.get(ROLE_COOKIE)?.value;
  const role = resolveRole(access, roleCookie);
  const isLoggedIn = Boolean(access);

  if (pathname.startsWith("/customer")) {
    if (!isLoggedIn) {
      const url = request.nextUrl.clone();
      url.pathname = "/login";
      url.searchParams.set("next", pathname);
      return NextResponse.redirect(url);
    }
    return NextResponse.next();
  }

  if (pathname.startsWith("/admin")) {
    if (!isLoggedIn) {
      const url = request.nextUrl.clone();
      url.pathname = "/login";
      url.searchParams.set("next", pathname);
      return NextResponse.redirect(url);
    }
    if (role !== "ADMIN") {
      const url = request.nextUrl.clone();
      url.pathname = "/403";
      return NextResponse.redirect(url);
    }
    return NextResponse.next();
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/customer/:path*", "/admin/:path*"],
};
