import type { UserRole } from "@/types/auth";

/**
 * Decodes JWT payload without signature verification.
 * Wire signature verification when you integrate with auth_service.
 */
export function decodeJwtPayload(token: string): Record<string, unknown> | null {
  try {
    const parts = token.split(".");
    if (parts.length !== 3 || !parts[1]) return null;
    const base64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), "=");
    const binary = atob(padded);
    const json = decodeURIComponent(
      Array.prototype.map
        .call(binary, (c: string) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    );
    return JSON.parse(json) as Record<string, unknown>;
  } catch {
    return null;
  }
}

export function extractRoleFromPayload(payload: Record<string, unknown>): UserRole | null {
  const direct = payload.role;
  if (direct === "ADMIN" || direct === "CUSTOMER") return direct;

  const roles = payload.roles;
  if (Array.isArray(roles)) {
    if (roles.includes("ADMIN")) return "ADMIN";
    if (roles.includes("CUSTOMER")) return "CUSTOMER";
  }

  const realmAccess = payload.realm_access as { roles?: string[] } | undefined;
  const ra = realmAccess?.roles;
  if (Array.isArray(ra)) {
    if (ra.includes("ADMIN")) return "ADMIN";
    if (ra.includes("CUSTOMER")) return "CUSTOMER";
  }

  return null;
}
