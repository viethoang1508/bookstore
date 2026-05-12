"use client";

import * as React from "react";

import { ACCESS_TOKEN_COOKIE, ROLE_COOKIE } from "@/lib/auth/constants";
import { readClientCookie } from "@/lib/auth/read-cookie-client";
import { clearSessionCookies, setSessionCookies } from "@/lib/auth/session-cookies";
import type { AuthUser, UserRole } from "@/types/auth";

type AuthState = {
  user: AuthUser | null;
  isAuthenticated: boolean;
};

type AuthContextValue = AuthState & {
  /** Placeholder: call after auth_service login returns tokens. */
  setAuthenticatedSession: (params: { accessToken: string; refreshToken: string; role: UserRole }) => void;
  logout: () => void;
};

const AuthContext = React.createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = React.useState<AuthUser | null>(null);

  React.useEffect(() => {
    const access = readClientCookie(ACCESS_TOKEN_COOKIE);
    const roleRaw = readClientCookie(ROLE_COOKIE);
    const role = roleRaw === "ADMIN" || roleRaw === "CUSTOMER" ? roleRaw : null;
    if (access && role) {
      setUser({ role });
    }
  }, []);

  const setAuthenticatedSession = React.useCallback(
    (params: { accessToken: string; refreshToken: string; role: UserRole }) => {
      setSessionCookies({
        accessToken: params.accessToken,
        refreshToken: params.refreshToken,
        role: params.role,
      });
      setUser({ role: params.role });
    },
    [],
  );

  const logout = React.useCallback(() => {
    clearSessionCookies();
    setUser(null);
  }, []);

  const value = React.useMemo<AuthContextValue>(
    () => ({
      user,
      isAuthenticated: !!user,
      setAuthenticatedSession,
      logout,
    }),
    [user, setAuthenticatedSession, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuthContext() {
  const ctx = React.useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuthContext must be used within AuthProvider");
  }
  return ctx;
}
