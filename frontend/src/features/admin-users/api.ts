import { profileApi } from "@/features/profile";

export const adminUsersApi = {
  me: () => profileApi.me(),
} as const;