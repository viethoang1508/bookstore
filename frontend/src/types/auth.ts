export type UserRole = "ADMIN" | "CUSTOMER";

/** Placeholder: align fields with auth_service TokenResponse. */
export type TokenResponse = {
  access_token: string;
  refresh_token: string;
  expires_in?: number;
  token_type?: string;
};

export type AuthUser = {
  role: UserRole;
  /** Placeholder until user_service profile is wired. */
  email?: string;
};
