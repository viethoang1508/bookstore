"use client";

import * as React from "react";

import { Toaster } from "@/components/ui/toaster";
import { AuthProvider } from "@/providers/auth-provider";
import { QueryProvider } from "@/providers/query-provider";

export function AppProviders({ children }: { children: React.ReactNode }) {
  return (
    <QueryProvider>
      <AuthProvider>
        {children}
        <Toaster />
      </AuthProvider>
    </QueryProvider>
  );
}
