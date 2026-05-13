"use client";

import * as React from "react";
import { MutationCache, QueryCache, QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

import { toast } from "@/hooks/use-toast";

function showApiErrorToast(error: unknown) {
  const message = error instanceof Error ? error.message : "An unexpected error occurred.";
  toast({
    title: "Request failed",
    description: message,
    variant: "destructive",
  });
}

export function QueryProvider({ children }: { children: React.ReactNode }) {
  const [client] = React.useState(
    () =>
      new QueryClient({
        queryCache: new QueryCache({
          onError: showApiErrorToast,
        }),
        mutationCache: new MutationCache({
          onError: showApiErrorToast,
        }),
        defaultOptions: {
          queries: {
            staleTime: 30_000,
            retry: 1,
            refetchOnWindowFocus: false,
          },
        },
      }),
  );

  return (
    <QueryClientProvider client={client}>
      {children}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}
