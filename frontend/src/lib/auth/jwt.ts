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
  const normalizeRole = (value: unknown): UserRole | null => {
    if (typeof value !== "string") return null;
    if (value === "ADMIN" || value === "SUPER_ADMIN") return "ADMIN";
    if (value === "CUSTOMER") return "CUSTOMER";
    return null;
  };

  const direct = normalizeRole(payload.role);
  if (direct) return direct;

  const roles = payload.roles;
  if (Array.isArray(roles)) {
    for (const role of roles) {
      const normalized = normalizeRole(role);
      if (normalized) return normalized;
    }
  }

  const realmAccess = payload.realm_access as { roles?: string[] } | undefined;
  const ra = realmAccess?.roles;
  if (Array.isArray(ra)) {
    for (const role of ra) {
      const normalized = normalizeRole(role);
      if (normalized) return normalized;
    }
  }

  return null;
}
