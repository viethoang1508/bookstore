export type UserRole = "ADMIN" | "CUSTOMER";

/** Placeholder: align fields with auth_service TokenResponse. */
export type TokenResponse = {
  accessToken: string;
  refreshToken: string;
  expiresIn?: number;
  tokenType?: string;
};

export type AuthUser = {
  role: UserRole;
  /** Placeholder until user_service profile is wired. */
  email?: string;
};
